import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;
import java.util.Collections;
import sootup.callgraph.CallGraph;
import sootup.callgraph.CallGraphAlgorithm;
import sootup.callgraph.ClassHierarchyAnalysisAlgorithm;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.signatures.MethodSignature;
import sootup.core.typehierarchy.ViewTypeHierarchy;
import sootup.core.types.ClassType;
import sootup.core.types.ReferenceType;
import sootup.core.types.VoidType;
import sootup.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaIdentifierFactory;
import sootup.java.core.JavaProject;
import sootup.java.core.JavaSootClass;
import sootup.java.core.language.JavaLanguage;
import sootup.java.core.views.JavaView;
import java.io.*;
import java.util.*;


public class Pipeline {

    // Assembly of pipeline steps to mine regression bugs based on refactoring changes on the benchmark of Res4j
    public static void main(String[] args) throws Exception {

        //Step 1: Setting up and Obtaining cleaned and verified entries from Res4j
        String storageFile = "/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/refactoring-miner/src/benchmarkEntry.txt";
        File file = new File(storageFile);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String str;
        Map<String, List<String>> relevantProjectWithRic = new HashMap<>();
        while ((str = bufferedReader.readLine()) != null) {
            String[] tempLine = str.split(", ");
            String proj = tempLine[tempLine.length - 1];
            String ric = tempLine[1];
            if (relevantProjectWithRic.containsKey(proj)) {
                List<String> currRics = relevantProjectWithRic.get(proj);
                currRics.add(ric);
            } else {
                relevantProjectWithRic.put(proj, new ArrayList<>(10));
            }
        }

        //Step 2: Applying Refactoring Miner for all ric commits to identify refactoring changes
        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

        Repository repo = null;

        try {
            repo = gitService.cloneIfNotExists(
                            "jhy/jsoup",
                            "https://github.com/jhy/jsoup.git");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String key: relevantProjectWithRic.keySet()) {
            //API 2: Report all refactoring changes in commits between 2 commits (ric & rfc in this case)
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

            //API 1: Report refactoring changes in one single commit (ric)
            repo = gitService.openRepository(key);
            System.out.println(key);
            List<String> rics = relevantProjectWithRic.get(key);
            FileWriter myWriter = new FileWriter("/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/refactoring-miner/src/refactoring.txt");
            for (String ric : rics) {
                miner.detectAtCommit(repo, ric, new RefactoringHandler() {
                    @Override
                    public void handle(String commitId, List<Refactoring> refactorings) {
                        System.out.println("Refactorings at " + commitId);
                        try {
                            myWriter.write(key + ", "+"Refactorings at " + commitId + "\n");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        for (Refactoring ref : refactorings) {
                            System.out.println(ref.toString());
                            try {
                                myWriter.write(ref.toString() + "\n");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
            }
        }

        //Step 3: Construct of Call Graph with SootUp library --> code
        // for handling of cases where the method containing the refactoring change is not directly testable
        // for example, if the method is a private method
        System.out.println("start construction of call graph");
        System.out.println(System.getProperty("java.home"));
        AnalysisInputLocation<JavaSootClass> inputLocation =
                new JavaClassPathAnalysisInputLocation(
                        "/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/refactoring-miner/src/main/resources");

        JavaLanguage language = new JavaLanguage(8);

        JavaProject project =
                JavaProject.builder(language)
                        .addInputLocation(inputLocation)
                        .addInputLocation(
                                new JavaClassPathAnalysisInputLocation(
                                        "/Library/Java/JavaVirtualMachines/jdk1.8.0_333.jdk/Contents/Home/jre/lib/rt.jar"))
                        .build();

        JavaView view = project.createFullView();

        ViewTypeHierarchy typeHierarchy = new ViewTypeHierarchy(view);
        ClassType classTypeA = project.getIdentifierFactory().getClassType("A");
        ClassType classTypeB = project.getIdentifierFactory().getClassType("B");

        ClassType classType1 = project.getIdentifierFactory().getClassType("Jsoup");
        ClassType classType2 = project.getIdentifierFactory().getClassType("org.jsoup.nodes.Document");
        ReferenceType type1 = project.getIdentifierFactory().getClassType("java.lang.String");
        ArrayList<String> params = new ArrayList<>(3);
        params.add("java.nio.file.Files");
        params.add("java.lang.String");
        params.add("java.lang.String");
        MethodSignature entryMethodSignature =
                JavaIdentifierFactory.getInstance()
                        .getMethodSignature(
                                classTypeA,
                                JavaIdentifierFactory.getInstance()
                                        .getMethodSubSignature(
                                                "calc", VoidType.getInstance(), Collections.singletonList(classTypeA)));
        CallGraphAlgorithm cha = new ClassHierarchyAnalysisAlgorithm(view, typeHierarchy);

        CallGraph cg = cha.initialize(Collections.singletonList(entryMethodSignature));

        cg.callsFrom(entryMethodSignature).forEach(System.out::println);

        //attempt of self-built call graph, naive approach
//        String graphFiles = "/Users/diwuyi/Documents/GitHub/refactoring-miner/src/main/resources";
//        Map<String, String> var = new HashMap<>(10);
//        Map<String, String> method = new HashMap<>(10);
//        BufferedReader bf = new BufferedReader(new FileReader(file));
//        String str;
//        Map<String, List<String>> relevantProjectWithRic = new HashMap<>();
//        while ((str = bufferedReader.readLine()) != null) {
//            String[] tempLine = str.split(", ");
//            String proj = tempLine[tempLine.length - 1];
//            String ric = tempLine[1];
//            if (relevantProjectWithRic.containsKey(proj)) {
//                List<String> currRics = relevantProjectWithRic.get(proj);
//                currRics.add(ric);
//            } else {
//                relevantProjectWithRic.put(proj, new ArrayList<>(10));
//            }
//        }

        //Step 3.5 refactoring miner 报出来的 refactor 是否包含target method (引起 bug 的修改)

        //Step 4: Differential Testing with evosuite
        // Experiment : target method identification -->
        // Differential test case 报了一个错 , 看跟 ric 是不是也是一样的 bug
        // 覆盖率：同时走过 fixing 的地方，
//        System.out.println("Working Directory = " + System.getProperty("user.dir"));
//        String[] arguments = new String[] {"/bin/bash", "-c", "java -jar evosuite-1.0.6.jar -regressionSuite -projectCP jsoup-1.13.1-SNAPSHOT_correct.jar -Dregressioncp=\"jsoup-1.11.3-SNAPSHOT.jar\" -class org.jsoup.parser.CharacterReader"};
//        // 调evosuite 的 api 接口
//
//        Process process = new ProcessBuilder(arguments).start();
//        BufferedReader procReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        String ln = "";
//        while ((ln = procReader.readLine()) != null) {
//            System.out.println(ln + "\n");
//        }
//        process.waitFor();
//        Runtime.getRuntime().exec("/bin/bash -c java -jar evosuite-1.0.6.jar -Dtools_jar_location=/Library/Java/JavaVirtualMachines/jdk1.8.0_333.jdk/Contents/Home/lib/tools.jar -regressionSuite -projectCP jsoup-1.13.1-SNAPSHOT_correct.jar -Dregressioncp=\"jsoup-1.11.3-SNAPSHOT.jar\" -class org.jsoup.parser.CharacterReader");
//        //process.waitFor();
//        System.out.println("Execution of differential testing is done");

        // Step 4: the differential testing step is performed in the evosuite-plus-plus repository
        // This pipeline module ends here and performs the task up to the generation of call graphs

    }
}
