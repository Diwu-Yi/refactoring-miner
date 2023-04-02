import org.apache.maven.cli.MavenCli;
import java.io.File;
public class Postprocess {

    public static void main(String[] args) {

        System.out.println(new File(".").getAbsolutePath());

        MavenCli cli = new MavenCli();
        cli.doMain(new String[]{"clean", "install"},
                "/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/refactoring-miner/jsoup", System.out, System.out);
    }

}
