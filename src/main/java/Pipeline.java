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
    public static List<String> extractTargetClass(String refactoringType, String refactoringLine) {
        ArrayList<String> result = new ArrayList<>(2);
        String[] tempLineHolder;
        switch (refactoringType) {
            case ("Extract Method"):
                result.add(refactoringLine.split(" in class ")[1].trim());
                return result;
            case ("Rename Class"):
                return result;
            case ("Move Attribute"):
                tempLineHolder = refactoringLine.split(" from class ");
                result.add(tempLineHolder[2].trim());
                result.add(tempLineHolder[1].split(" to ")[0].trim());
                return result;
            case ("Move And Rename Attribute"):
                return result;
            case ("Replace Attribute"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Rename Method"):
                result.add(refactoringLine.split(" in class ")[1].trim());
                return result;
            case ("Inline Method"):
                result.add(refactoringLine.split(" in class ")[1].trim());
                return result;
            case ("Move Method"):
                tempLineHolder = refactoringLine.split(" from class");
                result.add(tempLineHolder[2].trim());
                result.add(tempLineHolder[1].split(" to ")[0].trim());
                return result;
            case ("Move And Rename Method"):
                tempLineHolder = refactoringLine.split(" from class ");
                result.add(tempLineHolder[2].trim());
                result.add(tempLineHolder[1].split(" to ")[0].trim());
                return result;
            case ("Pull Up Method"):
                tempLineHolder = refactoringLine.split(" from class ");
                result.add(tempLineHolder[2].trim());
                result.add(tempLineHolder[1].split(" to ")[0].trim());
                return result;
            case ("Move Class"):
                tempLineHolder = refactoringLine.split(" moved to ");
                result.add(tempLineHolder[1].trim());
                result.add(tempLineHolder[0].split("Move Class ")[0]);
                return result;
            case ("Move And Rename Class"):
                return result;
            case ("Move Source Folder"):
                return result;
            case ("Pull Up Attribute"):
                tempLineHolder = refactoringLine.split(" from class ");
                result.add(tempLineHolder[2].trim());
                result.add(tempLineHolder[1].split(" to ")[0].trim());
            case ("Push Down Attribute"):
                tempLineHolder = refactoringLine.split(" from class ");
                result.add(tempLineHolder[2].trim());
                result.add(tempLineHolder[1].split(" to ")[0].trim());
                return result;
            case ("Push Down Method"):
                tempLineHolder = refactoringLine.split(" from class ");
                result.add(tempLineHolder[2].trim());
                result.add(tempLineHolder[1].split(" to ")[0].trim());
                return result;
            case ("Extract Interface"):
                return result;
            case ("Extract Superclass"):
                tempLineHolder = refactoringLine.split(" from classes ");
                String[] temp2 = tempLineHolder[1].replace("[", "").replace("]", "").split(", ");
                Collections.addAll(result, temp2);
                result.add(tempLineHolder[0].split("Extract Superclass ")[0].trim());
                return result;
            case ("Extract Subclass"):
                tempLineHolder = refactoringLine.split(" from class ");
                result.add(tempLineHolder[1].trim());
                result.add(tempLineHolder[0].split("Extract Subclass ")[0].trim());
                return result;
            case ("Extract Class"):
                tempLineHolder = refactoringLine.split(" from class ");
                result.add(tempLineHolder[1].trim());
                result.add(tempLineHolder[0].split("Extract Class ")[0].trim());
                return result;
            case ("Extract And Move Method"):
                tempLineHolder = refactoringLine.split(" in class ");
                result.add(tempLineHolder[1].split(" & moved to class ")[0].trim());
                result.add(tempLineHolder[1].split(" & moved to class ")[1].trim());
                return result;
            case ("Move And Inline Method"):
                return result;
            case ("Rename Package"):
                return result;
            case ("Move Package"):
                return result;
            case ("Extract Variable"):
                tempLineHolder = refactoringLine.split(" from class ");
                result.add(tempLineHolder[1].trim());
                return result;
            case ("Extract Attribute"):
                result.add(refactoringLine.split(" in class ")[1].trim());
                return result;
            case ("Inline Variable"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Inline Attribute"):
                return result;
            case ("Rename Variable"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Rename Parameter"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Rename Attribute"):
                result.add(refactoringLine.split(" in class ")[1].trim());
                return result;
            case ("Merge Variable"):
                return result;
            case ("Merge Parameter"):
                return result;
            case ("Merge Attribute"):
                return result;
            case ("Split Variable"):
                return result;
            case ("Split Parameter"):
                return result;
            case ("Split Attribute"):
                return result;
            case ("Replace Variable With Attribute"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Parameterize Variable"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Localize Parameter"):
                return result;
            case ("Parameterize Attribute"):
                return result;
            case ("Remove Method Annotation"):
                tempLineHolder = refactoringLine.split(" from class");
                result.add(tempLineHolder[1].trim());
                return result;
            case ("Change Return Type"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Change Variable Type"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Change Parameter Type"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Change Attribute Type"):
                result.add(refactoringLine.split(" in class ")[1].trim());
                return result;
            case ("Add Method Annotation"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Modify Method Annotation"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Add Attribute Annotation"):
                return result;
            case ("Remove Attribute Annotation"):
                return result;
            case ("Modify Attribute Annotation"):
                return result;
            case ("Add Class Annotation"):
                result.add(refactoringLine.split(" in class ")[1].trim());
                return result;
            case ("Remove Class Annotation"):
                return result;
            case ("Modify Class Annotation"):
                result.add(refactoringLine.split(" in class ")[1].trim());
                return result;
            case ("Add Parameter Annotation"):
                return result;
            case ("Remove Parameter Annotation"):
                return result;
            case ("Modify Parameter Annotation"):
                return result;
            case ("Add Parameter"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Remove Parameter"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Reorder Parameter"):
                return result;
            case ("Add Variable Annotation"):
                return result;
            case ("Remove Variable Annotation"):
                return result;
            case ("Add Thrown Exception Type"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Remove Thrown Exception Type"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Change Thrown Exception Type"):
                return result;
            case ("Change Method Access Modifier"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Change Attribute Access Modifier"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Encapsulate Attribute"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Add Method Modifier"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Remove Method Modifier"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Add Attribute Modifier"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Remove Attribute Modifier"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Add Variable Modifier"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Remove Variable Modifier"):
                return result;
            case ("Change Class Access Modifier"):
                result.add(refactoringLine.split(" in class ")[1].trim());
                return result;
            case ("Add Class Modifier"):
                result.add(refactoringLine.split(" in class ")[1].trim());
                return result;
            case ("Remove Class Modifier"):
                result.add(refactoringLine.split(" in class ")[1].trim());
                return result;
            case ("Split Package"):
                return result;
            case ("Merge Package"):
                return result;
            case ("Change Type Declaration Kind"):
                return result;
            case ("Collapse Hierarchy"):
                return result;
            case ("Replace Loop With Pipeline"):
                return result;
            case ("Replace Pipeline With Loop"):
                return result;
            case ("Replace Anonymous With Lambda"):
                return result;
            case ("Merge Class"):
                return result;
            case ("Split Class"):
                return result;
            case ("Split Conditional"):
                return result;
            case ("Invert Condition"):
                return result;
            case ("Merge Conditional"):
                result.add(refactoringLine.split(" from class ")[1].trim());
                return result;
            case ("Merge Catch"):
                return result;
            case ("Merge Method"):
                return result;
            case ("Split Method"):
                return result;
            case ("Move Code"):
                return result;
            default:
                System.out.println("reached default branch, something went wrong");
                return result;
        }
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
        //check if all repos from the benchmark dataset are present in this repo's root dir
        // if not, clone it from github
        GitService gitService = new GitServiceImpl();

        for (String aRepo : repoUnderInvestigation) {

            try {
                gitService.cloneIfNotExists(
                        aRepo,
                        "https://github.com/" + aRepo + ".git");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // now, apply refactoring miner
        FileWriter myWriter = new FileWriter("/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/refactoring-miner/src/refactoring.txt");

        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

        for (String key : relevantProjectWithRic.keySet()) {
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
            String repoLocator = "";
            for (String repoEntry: repoUnderInvestigation) {
                if (repoEntry.contains(key)) {
                    repoLocator = repoEntry;
                    break;
                }
            }
            Repository repo1 = gitService.cloneIfNotExists(
                    repoLocator,
                    "https://github.com/" + repoLocator + ".git");
            System.out.println(key);
            List<String> rics = relevantProjectWithRic.get(key);

            for (String ric : rics) {
                miner.detectAtCommit(repo1, ric, new RefactoringHandler() {
                    @Override
                    public void handle(String commitId, List<Refactoring> refactorings) {
                        System.out.println("Refactorings at " + commitId);
                        try {
                            myWriter.write(key + ", " + "Refactorings at " + commitId + "\n");
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

        myWriter.close();

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
        String currProj = "";
        List<String> classes = new ArrayList<>();
        refactoringTypes.sort(Comparator.comparingInt(String::length).reversed());
        while ((refLine = scanner.readLine()) != null) {
            if (refLine.contains(", Refactorings at ")) {
                if (!Objects.equals(currSha, "")) {
                    targetWriter.write(currProj);
                    targetWriter.write(", ");
                    targetWriter.write(currSha);
                    targetWriter.write(", ");
                    targetWriter.write(classes.toString());
                    targetWriter.write("\n");
                    classes = new ArrayList<>();
                }
                currSha = refLine.split("Refactorings at ")[1].trim();
                currProj = refLine.split("Refactorings at ")[0].trim();
                continue;
            }
            for (String type : refactoringTypes) {
                if (refLine.contains(type)) {
                    System.out.println(type);
                    System.out.println(refLine);
                    classes.addAll(extractTargetClass(type, refLine));
                    break;
                }
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
        // Experiment : target method identification --> see above
        // Differential test case 报了一个错 , 看跟 ric 是不是也是一样的 bug
        // 覆盖率：同时走过 fixing 的地方，
        // 调evosuite 的 api 接口, 暂无， future todo

        // Step 4: the differential testing step is performed in the evosuite-plus-plus repository
        // This pipeline module ends here and performs the task up to the generation of call graphs

    }
}
