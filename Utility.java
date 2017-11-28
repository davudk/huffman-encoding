import java.io.File;

public final class Utility {

    public static boolean fileExists(String address) {
        return new File(address).isFile();
    }
}