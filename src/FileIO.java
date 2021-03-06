import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;

public class FileIO {
    private FileIO() {
        throw new AssertionError("This class cannot be instantiated");
    }

    public static String[] readFileLines(String filename) throws FileNotFoundException {
        ArrayList<String> fileLines = new ArrayList<>();
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        decoder.onMalformedInput(CodingErrorAction.IGNORE);

        try (BufferedReader bufferedReader =
                     new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename)), decoder))) {
            bufferedReader.lines().forEach(fileLines::add);
        } catch (IOException e) {
            throw new FileNotFoundException();
        }

        return fileLines.toArray(new String[fileLines.size()]);
    }

    public static void assertExists(final String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            throw new RuntimeException("File not found!");
        }
    }
}
