
public class HDecode {
	public static void main(String[] args) {
        boolean displayUsage = false;

        if (args.length == 1) {

            String input = args[0];
            
            if (Utility.fileExists(input)) {                
                if (input.toLowerCase().endsWith(".huf")) {
                    String output = input.substring(0, input.length() - 4);
                    
                    long start = System.currentTimeMillis();
    
                    Huffman.decode(input, output);
    
                    long elapsedMillis = System.currentTimeMillis() - start;    
                    System.out.println("Elapsed: " + (elapsedMillis / 1000.0) + "s");
    
                } else {
                    displayUsage = true;
                }
            } else {
                System.out.println("Input file not found!");
                return;
            }

        } else {
            displayUsage = true;
        }

        if (displayUsage) {
            System.out.println("Incorrect input arguments.");
            System.out.println("Usage: java HDecode <filename>");
            System.out.println();
            System.out.println("\t<filename>\tthe address of the input file to be decoded, ends with .huf; the output will be <filename> without .huf");
            System.out.println();
        }
	}
}