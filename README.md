# Huffman Encoding

This is the Huffman compression algorithm, implemented in Java. Features a heap implementation that was used just for this project.

**Usage:**

```
java HEncode myfile.txt
--> the output is myfile.txt.huf

java HDecode myfile.txt.huf
--> the output is myfile.txt
```

**Sample compression 1**

The following *lorem ipsum* text file (583 bytes) was compressed with a compression ratio of ~1.15 (resulting size: 507 bytes).

> Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent nec ipsum pharetra, accumsan ligula in, consequat lectus. Integer at fermentum erat. Vivamus finibus risus sit amet pharetra maximus. Nulla eu orci eget dui accumsan posuere. Aenean arcu orci, mollis vel venenatis sed, interdum sit amet lectus. Maecenas venenatis tincidunt ante, id semper velit pharetra in. Ut commodo risus ultricies lectus condimentum tincidunt. Etiam luctus nec felis ut faucibus. Maecenas sit amet egestas sapien. Nullam finibus semper est a pharetra. Etiam sollicitudin eget sem auctor egestas.

**Sample compression 2**

Another *lorem* sample was tested. Initial size was 89,547 bytes, and the compressed size was 48,318. The compression ratio is ~1.85.

*Text was created with: **https://lipsum.com/***
