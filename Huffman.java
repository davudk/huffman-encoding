import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;

public final class Huffman {
    static final int BITS_PER = 8; // never going to change, but eh might as well use a constant
    static final int ARRAY_SIZE = 256;

    public static void encode(String input, String output) {
        try (RandomAccessFile reader = new RandomAccessFile(input, "r");
            DataOutputStream writer = new DataOutputStream(new FileOutputStream(output))) {
            
            int[] charFreq = new int[ARRAY_SIZE];
            int value;
			while ((value = reader.read()) >= 0) {
                if (value < ARRAY_SIZE) {
                    charFreq[value]++;
                } else {
                    // ignore non-ascii characters
                }
            }
            reader.seek(0);
            
            int validCharCount = 0;
            for (int i = 0; i < charFreq.length; i++) {
                if (charFreq[i] > 0) validCharCount++;
            }

            BTree<HuffmanNode> root = buildHuffmanTree(charFreq);
            HuffmanCharacter[] huffChars = getHuffmanChars(root);
            int totalBitCount = computeBitCount(huffChars, charFreq);

            // System.out.println("bytes: " + (totalBitCount / 8) + "  bits: " + (totalBitCount % 8));

            // for (int i = 0; i < huffChars.length; i++) {
            //     if (huffChars[i] == null) continue;
            //     HuffmanCharacter h = huffChars[i];
            //     HuffmanNode n = h.getNode();
            //     System.out.println((char)n.getValue() + " --> x" + n.getFreq());
            // }

            // first write encoding
            writer.writeInt(validCharCount);
            for (int i = 0; i < charFreq.length; i++) {
                if (charFreq[i] <= 0) continue;
                writer.writeChar((char)i);
                writer.writeInt(charFreq[i]);
            }
            // writer.writeInt(totalBitCount);
            
            final int BUFFER_SIZE = 1024 * 64;

            byte[] buffer = new byte[BUFFER_SIZE];
            int bufferSize = 0;

            byte bits = 0;
            int bitCount = 0;
            while ((bufferSize = reader.read(buffer)) > 0) {
                for (int i = 0; i < bufferSize; i++) {
                    value = buffer[i];
                    HuffmanCharacter c = huffChars[value];
                    
                    int remaining = c.getBitCount();
                    while (remaining > 0) {
                        int bitsNeeded = BITS_PER - bitCount;

                        if (remaining <= bitsNeeded) {
                            int shiftBy = bitsNeeded - remaining;
                            int cropped = (c.getBitValue() & ((1 << remaining) - 1)) << shiftBy;
                            bits = (byte)(bits | cropped);
                            bitCount += remaining;
                            remaining = 0;
                        } else {
                            int shiftBy = remaining - bitsNeeded;
                            int cropped = (c.getBitValue() >> shiftBy) & ((1 << bitsNeeded) - 1);
                            bits = (byte)(bits | cropped);
                            remaining -= bitsNeeded;
                            bitCount = BITS_PER;
                        }

                        if (bitCount == BITS_PER) {
                            writer.writeByte(bits);
                            bitCount = 0;
                            bits = 0;
                        }
                    }
                }
            }

            // byte bits = 0;
            // int bitCount = 0;
			// while ((value = reader.read()) >= 0) {
            //     if (value < ARRAY_SIZE) {
            //         HuffmanCharacter c = huffChars[value];
                    
            //         int remaining = c.getBitCount();
            //         while (remaining > 0) {
            //             int bitsNeeded = BITS_PER - bitCount;

            //             if (remaining <= bitsNeeded) {
            //                 int shiftBy = bitsNeeded - remaining;
            //                 int cropped = (c.getBitValue() & ((1 << remaining) - 1)) << shiftBy;
            //                 bits = (byte)(bits | cropped);
            //                 bitCount += remaining;
            //                 remaining = 0;
            //             } else {
            //                 int shiftBy = remaining - bitsNeeded;
            //                 int cropped = (c.getBitValue() >> shiftBy) & ((1 << bitsNeeded) - 1);
            //                 bits = (byte)(bits | cropped);
            //                 remaining -= bitsNeeded;
            //                 bitCount = BITS_PER;
            //             }

            //             if (bitCount == BITS_PER) {
            //                 writer.writeByte(bits);
            //                 bitCount = 0;
            //                 bits = 0;
            //             }
            //         }
            //     }
            // }

            if (bitCount > 0) {               
                writer.writeByte(bits);
            }

		} catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public static void decode(String input, String output) {
        try (DataInputStream reader = new DataInputStream(new FileInputStream(input));
            BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {
            
            int[] charFreq = new int[ARRAY_SIZE];
            int validCharCount = reader.readInt();
            for (int i = 0; i < validCharCount; i++) {
                char c = reader.readChar();
                int freq = reader.readInt();
                charFreq[c] = freq;
            }
            // int totalBitCount = reader.readInt();

            BTree<HuffmanNode> root = buildHuffmanTree(charFreq);
            HuffmanCharacter[] huffChars = getHuffmanChars(root);
            int totalBitCount = computeBitCount(huffChars, charFreq);

            BTree<HuffmanNode> curr = root;
            byte bits = 0;
            int bitIndex = BITS_PER;
            int remainingTotalBitCount = totalBitCount;
            while (remainingTotalBitCount > 0) {
                if (bitIndex == BITS_PER) {
                    int value = reader.read();
                    if (value == -1) break;

                    bits = (byte)value;
                    bitIndex = 0;
                }

                int reverseBitIndex = BITS_PER - bitIndex - 1;
                int bit = (bits & (1 << reverseBitIndex)) >> reverseBitIndex;
                bitIndex++;

                if (bit == 0) {
                    curr = curr.getLeft();
                } else if (bit == 1) {
                    curr = curr.getRight();
                }

                if (curr == null) {
                    break; // what? this should never happen
                } else if (curr.isLeaf()) {
                    HuffmanNode node = curr.getValue();
                    writer.append((char)node.getValue());
                    curr = root;
                }

                remainingTotalBitCount -= 1;
            }

		} catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    static BTree<HuffmanNode> buildHuffmanTree(int[] charFreq) {
        if (charFreq == null || charFreq.length != Huffman.ARRAY_SIZE) {
            throw new IllegalArgumentException();
        }

        Heap<BTree<HuffmanNode>> nodes = new Heap<BTree<HuffmanNode>>(new HeapHuffmanTreeConstraint());

        // build valid nodes
        for (int i = 0; i < charFreq.length; i++) {
            if (charFreq[i] <= 0) continue;

            HuffmanNode n = new HuffmanNode((char)i, charFreq[i]);
            nodes.insert(new BTree<HuffmanNode>(n));
        }
        
        // start matching nodes by lowest freq.
        while (nodes.size() > 1) {
            BTree<HuffmanNode> right = nodes.extract();
            BTree<HuffmanNode> left = nodes.extract();

            HuffmanNode parentNode = new HuffmanNode();
            parentNode.setFreq(left.getValue().getFreq() + right.getValue().getFreq());
            BTree<HuffmanNode> parent = new BTree<HuffmanNode>(parentNode, left, right);
            nodes.insert(parent);
        }

        // return the root
        return nodes.extract();
    }

    static HuffmanCharacter[] getHuffmanChars(BTree<HuffmanNode> root) {
        HuffmanCharacter[] characters = new HuffmanCharacter[ARRAY_SIZE];

        class Traverser {
            public void traverse(BTree<HuffmanNode> treeNode, int bitValue, int bitCount) {
                if (treeNode == null) return;

                HuffmanNode node = treeNode.getValue();
                if (node.getValue() >= 0) {
                    HuffmanCharacter huffChar = new HuffmanCharacter(node);
                    huffChar.setBitValue(bitValue);
                    huffChar.setBitCount(bitCount);
                    characters[node.getValue()] = huffChar;
                }
                traverse(treeNode.getLeft(), bitValue << 1, bitCount + 1);
                traverse(treeNode.getRight(), (bitValue << 1) | 1, bitCount + 1);
            }
        };

        Traverser t = new Traverser();
        t.traverse(root, 0, 0);
        
        return characters;
    }
    
    static int computeBitCount(HuffmanCharacter[] huffChars, int[] charFreq) {
        int bitCount = 0;
        for (int i = 0; i < huffChars.length; i++) {
            if (charFreq[i] <= 0) continue;
            bitCount += huffChars[i].getBitCount() * charFreq[i];
        }
        return bitCount;
    }

    static class HuffmanCharacter {
        HuffmanNode node;
        int bitValue, bitCount;

        public HuffmanCharacter(HuffmanNode node) {
            this.node = node;
        }

        public HuffmanNode getNode() { return node; }
        public void setNode(HuffmanNode node) { this.node = node; }

        public int getBitValue() { return bitValue; }
        public void setBitValue(int bitValue) { this.bitValue = bitValue; }

        public int getBitCount() { return bitCount; }
        public void setBitCount(int bitCount) { this.bitCount = bitCount; }
    }

    static class HeapHuffmanTreeConstraint implements HeapConstraint<BTree<HuffmanNode>> {
        @Override
        public boolean isConstraintValid(BTree<HuffmanNode> parent, BTree<HuffmanNode> child) {
            return parent.getValue().getFreq() <= child.getValue().getFreq();
        }
    }
}