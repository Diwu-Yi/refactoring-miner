import org.eclipse.jgit.api.Git;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Postprocess {

    public static void main(String[] args) {

        System.out.println(new File(".").getAbsolutePath());
        File revDir = new File("/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/refactoring-miner/jhy/jsoup");

        try (Git git = Git.open(new File("/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/refactoring-miner/jhy/jsoup"))) {
            git.checkout().setName("b92b4f6b9b3256e97bfb6a0732bf113b6da53a4c").call();
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
