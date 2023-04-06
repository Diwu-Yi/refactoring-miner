import org.apache.maven.cli.MavenCli;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Postprocess {

    public static void main(String[] args) {

        System.out.println(new File(".").getAbsolutePath());
        File revDir = new File("/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/refactoring-miner/jsoup");
        MavenCli cli = new MavenCli();
        cli.doMain(new String[]{"clean", "install"},
                "/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/refactoring-miner/jsoup", System.out, System.out);

        try (Git git = Git.open(new File("jsoup"))) {
            git.checkout().setName("e97f564d2450702d2b74ff35ecc5ad5c1e57fc0d").call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String buildCommand = "mvn install -DskipTests=true ";
        //String testCommand = "mvn test -Dtest=" + this.testCase + " " + "-Dmaven.test.failure.ignore=true";
        try {
            new Executor().setDirectory(revDir).exec(buildCommand);
            //new Executor().setDirectory(this.revDir).exec(testCommand, 5);
            //List<String> errorMessages = testManager.getErrors(revDir);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
