
public class HEncode {
	public static void main(String[] args) {
        boolean displayUsage = false;

        if (args.length == 1) {

            String input = args[0];
            String output = input + ".huf";
            
            if (Utility.fileExists(input)) {
                long start = System.currentTimeMillis();

                Huffman.encode(input, output);

                long elapsedMillis = System.currentTimeMillis() - start;    
                System.out.println("Elapsed: " + (elapsedMillis / 1000.0) + "s");
            } else {
                System.out.println("Input file not found!");
            }
            
        } else {
            displayUsage = true;
        }

        if (displayUsage) {
            System.out.println("Incorrect input arguments.");
            System.out.println("Usage: java HEncode <filename>");
            System.out.println();
            System.out.println("\t<filename>\tthe address of the input file to be encoded; the output will be <filename>.huf");
            System.out.println();
        }
	}
}