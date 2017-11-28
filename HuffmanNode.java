
public class HuffmanNode {
    int value = -1;
    int freq;
    
    public HuffmanNode() { }
    public HuffmanNode(int value, int freq) {
        setValue(value);
        setFreq(freq);
    }
    
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

    public int getFreq() { return freq; }
    public void setFreq(int freq) { this.freq = freq; }
}