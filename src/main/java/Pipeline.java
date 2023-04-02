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
import sootup.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaIdentifierFactory;
import sootup.java.core.JavaProject;
import sootup.java.core.JavaSootClass;
import sootup.java.core.language.JavaLanguage;
import sootup.java.core.views.JavaView;
import java.io.*;
import java.util.*;

/**
 * Integrate steps required to run regression testing with evosuite-plus-plus.
 */
public class Pipeline {

    public static String extractTargetClass(String refactoringType, String refactoringLine) {
        switch (refactoringType) {
            case "Add Thrown Exception Type":
                return "";
        }
        return "something went wrong";
    }

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
            String ric = tempLine[1].trim();
            if (relevantProjectWithRic.containsKey(proj)) {
                List<String> currRics = relevantProjectWithRic.get(proj);
                currRics.add(ric);
            } else {
                relevantProjectWithRic.put(proj, new ArrayList<>(10));
            }
        }
        bufferedReader.close();

        //Step 2: Applying Refactoring Miner for all ric commits to identify refactoring changes
        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

        String repoFile = "/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/refactoring-miner/src/repo.txt";
        File file1 = new File(repoFile);
        BufferedReader repoReader = new BufferedReader(new FileReader(file1));
        String currRepo;
        Set<String> repoUnderInvestigation = new HashSet<>();
        while ((currRepo = repoReader.readLine()) != null) {
            if (!currRepo.isEmpty()) {
                repoUnderInvestigation.add(currRepo.trim());
            }
        }
        repoReader.close();

        for (String aRepo: repoUnderInvestigation) {
            String folder = aRepo.split("/")[0];
            Repository repo;

            try {
                repo = gitService.cloneIfNotExists(
                        folder,
                        "https://github.com/" + aRepo + ".git");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            for (String key: relevantProjectWithRic.keySet()) {

                /*
                //API 2: Report all refactoring changes in commits between 2 commits (ric & rfc in this case)
                miner.detectBetweenCommits(repo,
                    "d23fc99a99ac44f2a4352899e2a6d12d26a74503", "c716d1de3fe1bde6a330629939c45745e9e65e95",
                    new RefactoringHandler() {
                        @Override
                        public void handle(String commitId, List<Refactoring> refactorings) {
                            System.out.println("Refactorings at " + commitId);
                            for (Refactoring ref : refactorings) {
                                System.out.println(ref.toString());
                            }
                        }
                    });
                */

                //API 1: Report refactoring changes in one single commit (ric)
                // when developers code new features, they are not sure if the change will cause regression bugs
                // whether it is worthwhile to generate regression test case for particular change , eg. change of a method
                // a class, and soon on -->
                // measure the accuracy, whether the approach recommend regression (1)
                // RQ2 whether refactoring miner gives good result, on ric versus any commit
                // RQ3
                Repository repo = gitService.openRepository(key);
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
                myWriter.close();
            }


        // Step 2.5: for each refactoring under one commit, extract out the target class and the target method if
        // applicable (taxonomy preprocess)
        String taxonomyStorage = "/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/refactoring-miner/src/taxonomy.txt";
        File taxonomy = new File(taxonomyStorage);
        BufferedReader taxonomyLoader = new BufferedReader(new FileReader(taxonomy));
        String strTax;
        List<String> refactoringTypes = new ArrayList<>(100);
        while ((strTax = taxonomyLoader.readLine()) != null) {
            refactoringTypes.add(strTax.trim());
        }
        taxonomyLoader.close();

        String refactoringStorage = "/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/refactoring-miner/src/refactoring.txt";
        File refactorings = new File(refactoringStorage);
        BufferedReader refactoringScanner = new BufferedReader(new FileReader(refactorings));
        HashSet<String> encounteredTypes = new HashSet<>();
        String refExampleLine;
        String exampleStorage = "/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/refactoring-miner/src/taxonomy-example-of-current-benchmark.txt";
        FileWriter exampleWriter = new FileWriter(exampleStorage);
        while ((refExampleLine = refactoringScanner.readLine()) != null) {
            if (refExampleLine.contains(", Refactorings at ")) {
                continue;
            }
            if (encounteredTypes.size() == refactoringTypes.size()) {
                break;
            }
            for (String refactoringType : refactoringTypes) {
                if (refExampleLine.contains(refactoringType)) {
                    if (encounteredTypes.contains(refactoringType)) {
                        continue;
                    }
                    encounteredTypes.add(refactoringType);
                    refExampleLine = refExampleLine.replace("\t", " ");
                    exampleWriter.write("For Refactor Change Type: " + refactoringType + " the example is: "
                            + refExampleLine + "\n");
                }
            }
        }
        refactoringScanner.close();
        exampleWriter.close();

        // Alternative step 3: extraction of target class with corresponding commit sha

        BufferedReader scanner = new BufferedReader(new FileReader(refactorings));
        String refLine;
        String target = "/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/refactoring-miner/src/targetClass.txt";
        FileWriter targetWriter = new FileWriter(target);
        String currSha = "";
        List<String> classes = new ArrayList<>();
        while ((refLine = scanner.readLine()) != null) {
            if (refLine.contains(", Refactorings at ")) {
                if(!Objects.equals(currSha, "")) {
                    targetWriter.write(currSha);
                    targetWriter.write(", ");
                    targetWriter.write(classes.toString());
                    targetWriter.write("\n");
                    classes = new ArrayList<>();
                }
                currSha = refLine.split("Refactorings at ")[1].trim();
                continue;
            }
            if (refLine.split(" class ").length > 1) {
                classes.add(refLine.split(" class ")[1].trim());
            }
        }
        scanner.close();
        targetWriter.close();

        //Step 3: Construct of Call Graph with SootUp library --> code
        // for handling of cases where the method containing the refactoring change is not directly testable
        // for example, if the method is a private method
        System.out.println("start construction of call graph");
        System.out.println(System.getProperty("java.home"));
        AnalysisInputLocation<JavaSootClass> inputLocation =
                new JavaClassPathAnalysisInputLocation(
                        "/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/evosuite-plus-plus/shell/src/test/resources/jsoup-1.11.3-SNAPSHOT.jar");

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

        ClassType classDeclarationType = project.getIdentifierFactory().getClassType("org.jsoup.Jsoup");
        ClassType returnType = project.getIdentifierFactory().getClassType("org.jsoup.nodes.Document");
        ClassType type1 = project.getIdentifierFactory().getClassType("java.io.File");
        ClassType type2 = project.getIdentifierFactory().getClassType("java.lang.String");
        ClassType type3 = project.getIdentifierFactory().getClassType("java.lang.String");
        ArrayList<ClassType> params = new ArrayList<>(3);
        params.add(type1);
        params.add(type2);
        params.add(type3);
        MethodSignature entryMethodSignature =
                JavaIdentifierFactory.getInstance()
                        .getMethodSignature(
                                classDeclarationType,
                                JavaIdentifierFactory.getInstance()
                                        .getMethodSubSignature(
                                                "parse", returnType, params));
        CallGraphAlgorithm cha = new ClassHierarchyAnalysisAlgorithm(view, typeHierarchy);

        CallGraph cg = cha.initialize(Collections.singletonList(entryMethodSignature));

        cg.callsFrom(entryMethodSignature).forEach(System.out::println);

        //Step 3.5 refactoring miner 报出来的 refactor 是否包含target method (引起 bug 的修改)

        //Step 4: Differential Testing with evosuite
        // Experiment : target method identification -->
        // Differential test case 报了一个错 , 看跟 ric 是不是也是一样的 bug
        // 覆盖率：同时走过 fixing 的地方，
//        System.out.println("Working Directory = " + System.getProperty("user.dir"));
//        String[] arguments = new String[] {"/bin/bash", "-c", "java -jar evosuite-1.0.6.jar -regressionSuite -projectCP jsoup-1.13.1-SNAPSHOT_correct.jar -Dregressioncp=\"jsoup-1.11.3-SNAPSHOT.jar\" -class org.jsoup.parser.CharacterReader"};
//        // 调evosuite 的 api 接口


        // Step 4: the differential testing step is performed in the evosuite-plus-plus repository
        // This pipeline module ends here and performs the task up to the generation of call graphs

    }
}
