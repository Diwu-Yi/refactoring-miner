import java.io.*;

/**
 * Build taxonomy of supported types of refactoring miner from its published results.
 */
public class Preprocess {
    public static void main(String[] args) throws IOException {
        String taxonomyReference = "/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/refactoring-miner/src/refactoring-taxonomy.txt";
        File file = new File(taxonomyReference);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String str;
        FileWriter myWriter = new FileWriter("/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/refactoring-miner/src/taxonomy.txt");
        while ((str = bufferedReader.readLine()) != null) {
            String[] tempLine = str.split("\\d+");
            myWriter.write(tempLine[0].trim() + "\n");
        }
        bufferedReader.close();
        myWriter.close();
    }
}
