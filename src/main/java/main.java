import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.util.List;


public class main {

    public static void main(String[] args) throws Exception {
        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

        Repository repo = null;

        {
            try {
                repo = gitService.cloneIfNotExists(
                        "apache/commons-jexl",
                        "https://github.com/apache/commons-jexl.git");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        miner.detectBetweenCommits(repo,
//                "d23fc99a99ac44f2a4352899e2a6d12d26a74503", "c716d1de3fe1bde6a330629939c45745e9e65e95",
//                new RefactoringHandler() {
//                    @Override
//                    public void handle(String commitId, List<Refactoring> refactorings) {
//                        System.out.println("Refactorings at " + commitId);
//                        for (Refactoring ref : refactorings) {
//                            System.out.println(ref.toString());
//                        }
//                    }
//                });
        miner.detectAtCommit(repo, "f6413a34e77cf43b18c6625a0f77f47ffa466ee3", new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> refactorings) {
                System.out.println("Refactorings at " + commitId);
                for (Refactoring ref : refactorings) {
                    System.out.println(ref.toString());
                }
            }
        });
    }

}
