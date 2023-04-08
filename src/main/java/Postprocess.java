import org.eclipse.jgit.api.Git;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Postprocess {

    public static void main(String[] args) {

        System.out.println(new File(".").getAbsolutePath());
        File revDir = new File("/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/refactoring-miner/jsoup");

        try (Git git = Git.open(new File("jsoup"))) {
            git.checkout().setName("e97f564d2450702d2b74ff35ecc5ad5c1e57fc0d").call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String buildCommand = "mvn install -DskipTests=true ";
        try {
            new Executor().setDirectory(revDir).exec(buildCommand);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
