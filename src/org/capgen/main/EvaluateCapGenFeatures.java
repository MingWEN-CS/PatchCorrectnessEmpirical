package org.capgen.main;

import org.capgen.algorithm.Algorithm;
import org.capgen.entity.ModificationFeature;
import org.capgen.util.*;

import java.io.File;
import java.util.*;

public class EvaluateCapGenFeatures {


    public static HashSet<Integer> getNumsFromString(String text) {
//		System.out.println(text);
        HashSet<Integer> nums = new HashSet<Integer>();
        if (text.contains(",")) {
            String[] split = text.split(",");
            int s = Integer.parseInt(split[0]);
            int e = Integer.parseInt(split[1]);
            for (int i = s; i <=e; i++)
                nums.add(i);
        } else nums.add(Integer.parseInt(text));
//		System.out.println(nums.toString());
        return nums;
    }

    public static void mergeTwoMaps(HashMap<Integer, Integer> a, HashMap<Integer, Integer> b) {
        for (Integer key : b.keySet()) {
            if (!a.containsKey(key))
                a.put(key, b.get(key));
            else
                a.put(key, a.get(key) + b.get(key));
        }
    }

    public static String turnMapToString(HashMap<Integer, Integer> maps) {
        String content = "";
        for (int i = 1; i <= ASTNodeType.NODE_NUM; i++)
            if (maps.containsKey(i))
                content += maps.get(i) + "\t";
            else content += "0\t";

        return content;
    }

    public static List<HashSet<Integer>> getFixLocations(String buggyFile, String fixedFile) {

        Pair<String, String> results = Commands.getDiffofTwoFiles(buggyFile, fixedFile);
        System.out.println(results.getKey());
        String[] split = results.getKey().split("\n");
        HashSet<Integer> fixedNums = new HashSet<>();
        HashSet<Integer> buggyNums = new HashSet<>();
        for (String line : split) {
            line = line.trim();
            if (line.startsWith("<") || line.startsWith("---") || line.startsWith(">")) continue;
            if (line.contains("c")) {
                HashSet<Integer> ids = getNumsFromString(line.split("c")[1]);
                fixedNums.addAll(ids);
                ids = getNumsFromString(line.split("c")[0]);
                buggyNums.addAll(ids);

            } else if (line.contains("a")) {
                //nums.addAll( getNumsFromString(line.split("a")[0]) );
                HashSet<Integer> ids = getNumsFromString(line.split("a")[1]);
                fixedNums.addAll(ids);
                int num = 0;
                int min = Integer.MAX_VALUE;
                int max = Integer.MIN_VALUE;
                for (int id : ids) {
                    num ++;
                    if (id > max) max = id;
                    if (id < min) min = id;
                }
                while (num < 5) {
                    fixedNums.add(--min);
                    fixedNums.add(++max);
                    num += 2;
                }

                ids = getNumsFromString(line.split("a")[0]);
                buggyNums.addAll(ids);

                // Since it is addition, we need to include the above the below lines
                for (int id : ids) {
                    buggyNums.add(id-1);
                    buggyNums.add(id-2);
                    buggyNums.add(id+1);
                    buggyNums.add(id+2);
                }
            } else if (line.contains("d")) {
                HashSet<Integer> ids = getNumsFromString(line.split("d")[1]);
                fixedNums.addAll(ids);
                for (int id : ids) {
                    fixedNums.add(id-1);
                    fixedNums.add(id-2);
                    fixedNums.add(id+1);
                    fixedNums.add(id+2);
                }
                ids = getNumsFromString(line.split("d")[0]);
                buggyNums.addAll(ids);
                int num = 0;
                int min = Integer.MAX_VALUE;
                int max = Integer.MIN_VALUE;
                for (int id : ids) {
                    num ++;
                    if (id > max) max = id;
                    if (id < min) min = id;
                }
                while (num < 5) {
                    fixedNums.add(--min);
                    fixedNums.add(++max);
                    num += 2;
                }
            }
        }
        List<HashSet<Integer>> lines = new ArrayList<>();
        lines.add(buggyNums);
        lines.add(fixedNums);
        return lines;
    }

    public static void investigateOperation(File bugLoc) throws Exception {
        //    System.out.println(bugLoc.getAbsolutePath());
        System.out.println(bugLoc.getAbsolutePath());
        int fileIndex = 1;
        ModificationFeature toolFeatures = new ModificationFeature();
        ModificationFeature developerFeatures = new ModificationFeature();
        ModificationFeature developerToolFeatures = new ModificationFeature();
        HashMap<Integer, Integer> developerNodeFeatures = new HashMap<>();
        HashMap<Integer, Integer> buggyDeveloperNodeFeatures = new HashMap<>();
        HashMap<Integer, Integer> toolNodeFeatures = new HashMap<>();
        HashMap<Integer, Integer> buggyToolNodeFeatures = new HashMap<>();
        HashMap<Integer, Integer> toolDeveloperNodeFeatures = new HashMap<>();
        HashMap<Integer, Integer> developerToolNodeFeatures = new HashMap<>();

        List<String> developerVariables = new ArrayList<>();
        List<String> toolVariables = new ArrayList<>();
        List<String> buggyDeveloperVariables = new ArrayList<>();
        List<String> buggyToolVariables = new ArrayList<>();
        List<String> developerToolVariables = new ArrayList<>();
        List<String> toolDeveloperVariables = new ArrayList<>();

        while (true) {
            String buggyFile = bugLoc + File.separator + "buggy" + fileIndex + ".java";
            String developerPatch = bugLoc + File.separator + "developer-patch" + fileIndex + ".java";
            String toolPatch = bugLoc + File.separator + "tool-patch" + fileIndex + ".java";
            //boolean exist = new File(buggyFile).exists();
            //System.out.println(exist + "\t" + buggyFile);
            if (!new File(buggyFile).exists()) break;
            List<ModificationFeature> developerFeature = ExtractModificationFeature.ExtractASTDifferenceFeature(buggyFile, developerPatch);
            List<ModificationFeature> toolFeature = ExtractModificationFeature.ExtractASTDifferenceFeature(buggyFile, toolPatch);
            List<ModificationFeature> developerToolFeature = ExtractModificationFeature.ExtractASTDifferenceFeature(developerPatch, toolPatch);

            toolFeatures.addFeatures(toolFeature.get(1));
            developerFeatures.addFeatures(developerFeature.get(1));
            developerToolFeatures.addFeatures(developerToolFeature.get(1));


            List<HashSet<Integer>> modifiedLines = getFixLocations(buggyFile, developerPatch);
            HashSet<Integer> developerLines = modifiedLines.get(1);
            HashSet<Integer> buggyDeveloperLines = modifiedLines.get(0);

            modifiedLines = getFixLocations(buggyFile, toolPatch);
            HashSet<Integer> toolLines = modifiedLines.get(1);
            HashSet<Integer> buggyToolLines = modifiedLines.get(0);

            modifiedLines = getFixLocations(developerPatch, toolPatch);
            HashSet<Integer> developerToolLines = modifiedLines.get(0);
            HashSet<Integer> toolDeveloperLines = modifiedLines.get(1);


            HashMap<Integer, Integer> developerNodeVector =
                    ExtractModificationFeature.getNodeCount(developerPatch, developerLines);
            HashMap<Integer, Integer> buggyDeveloperNodeVector =
                    ExtractModificationFeature.getNodeCount(buggyFile, buggyDeveloperLines);
            HashMap<Integer, Integer> toolNodeVector =
                    ExtractModificationFeature.getNodeCount(toolPatch, toolLines);
            HashMap<Integer, Integer> buggyToolNodeVector =
                    ExtractModificationFeature.getNodeCount(buggyFile, buggyToolLines);

            HashMap<Integer, Integer> toolDeveloperNodeVector =
                    ExtractModificationFeature.getNodeCount(toolPatch, toolDeveloperLines);

            HashMap<Integer, Integer> developerToolNodeVector =
                    ExtractModificationFeature.getNodeCount(developerPatch, developerToolLines);


            mergeTwoMaps(developerNodeFeatures, developerNodeVector);
            mergeTwoMaps(buggyDeveloperNodeFeatures, buggyDeveloperNodeVector);
            mergeTwoMaps(toolNodeFeatures, toolNodeVector);
            mergeTwoMaps(buggyToolNodeFeatures, buggyToolNodeVector);
            mergeTwoMaps(developerToolNodeFeatures, developerToolNodeVector);
            mergeTwoMaps(toolDeveloperNodeFeatures, toolDeveloperNodeVector);

            List<Pair<String, Integer>> toolVariable = ExtractModificationFeature.getVariableIndex(toolPatch, toolLines);
            List<Pair<String, Integer>> developerVariable = ExtractModificationFeature.getVariableIndex(developerPatch, developerLines);
            List<Pair<String, Integer>> buggyToolVariable = ExtractModificationFeature.getVariableIndex(buggyFile, buggyToolLines);
            List<Pair<String, Integer>> buggyDeveloperVariable = ExtractModificationFeature.getVariableIndex(buggyFile, buggyDeveloperLines);
            List<Pair<String, Integer>> developerToolVariable = ExtractModificationFeature.getVariableIndex(developerPatch, developerToolLines);
            List<Pair<String, Integer>> toolDeveloperVariable = ExtractModificationFeature.getVariableIndex(toolPatch, toolDeveloperLines);


            Collections.sort(toolVariable);
            Collections.sort(developerVariable);
            Collections.sort(buggyToolVariable);
            Collections.sort(buggyDeveloperVariable);
            Collections.sort(developerToolVariable);
            Collections.sort(toolDeveloperVariable);

            String tmp = "";
            for (Pair<String, Integer> pair : toolVariable)
                tmp += pair.getKey() + "\t";
            toolVariables.add(tmp);
            tmp = "";
            for (Pair<String, Integer> pair : developerVariable)
                tmp += pair.getKey() + "\t";
            developerVariables.add(tmp);
            tmp = "";
            for (Pair<String, Integer> pair : buggyToolVariable)
                tmp += pair.getKey() + "\t";
            buggyToolVariables.add(tmp);
            tmp = "";
            for (Pair<String, Integer> pair : buggyDeveloperVariable)
                tmp += pair.getKey() + "\t";
            buggyDeveloperVariables.add(tmp);


            for (Pair<String, Integer> pair : toolDeveloperVariable)
                tmp += pair.getKey() + "\t";
            toolDeveloperVariables.add(tmp);

            for (Pair<String, Integer> pair : developerToolVariable)
                tmp += pair.getKey() + "\t";
            developerToolVariables.add(tmp);


            fileIndex++;
        }
        //System.out.println(toolFeatures.ingredients.toString());
        WriteLinesToFile.writeToFiles(toolFeatures.toString(), bugLoc + File.separator + "tool-modification.txt");
        WriteLinesToFile.writeToFiles(developerFeatures.toString(), bugLoc + File.separator + "developer-modification.txt");
        WriteLinesToFile.writeToFiles(developerToolFeatures.toString(), bugLoc + File.separator + "developer-tool-modification.txt");

        WriteLinesToFile.writeToFiles(toolFeatures.variableToString(), bugLoc + File.separator + "tool-variable.txt");
        WriteLinesToFile.writeToFiles(developerFeatures.variableToString(), bugLoc + File.separator + "developer-variable.txt");
        WriteLinesToFile.writeToFiles(developerToolFeatures.variableToString(), bugLoc + File.separator + "developer-tool-variable.txt");

        // save the cosine similarity of the S3 approach

        WriteLinesToFile.writeToFiles(turnMapToString(buggyDeveloperNodeFeatures) + "\n" +
                turnMapToString(developerNodeFeatures), bugLoc + File.separator + "buggy-developer-cosine.txt");
        WriteLinesToFile.writeToFiles(turnMapToString(buggyToolNodeFeatures) + "\n" +
                turnMapToString(toolNodeFeatures), bugLoc + File.separator + "buggy-tool-cosine.txt");
        WriteLinesToFile.writeToFiles(turnMapToString(toolNodeFeatures) + "\n" +
                turnMapToString(developerNodeFeatures), bugLoc + File.separator + "tool-developer-cosine.txt");
        WriteLinesToFile.writeToFiles(turnMapToString(developerToolNodeFeatures) + "\n" +
                turnMapToString(toolDeveloperNodeFeatures), bugLoc + File.separator + "developer-tool-cosine.txt");


        WriteLinesToFile.writeLinesToFile(toolVariables, bugLoc + File.separator + "tool-variables.txt");
        WriteLinesToFile.writeLinesToFile(developerVariables, bugLoc + File.separator + "developer-variables.txt");
        WriteLinesToFile.writeLinesToFile(buggyToolVariables, bugLoc + File.separator + "buggy-tool-variables.txt");
        WriteLinesToFile.writeLinesToFile(buggyDeveloperVariables, bugLoc + File.separator + "buggy-developer-variables.txt");
        WriteLinesToFile.writeLinesToFile(developerToolVariables, bugLoc + File.separator + "developer-tool-variables.txt");
        WriteLinesToFile.writeLinesToFile(toolDeveloperVariables, bugLoc + File.separator + "tool-developer-variables.txt");

    }

    public static void investigateSyntax(File bugLoc) throws Exception {
        System.out.println(bugLoc.getAbsolutePath());
        int fileIndex = 1;
        List<String> developerSyntax = new ArrayList<>();
        List<String> toolSyntax = new ArrayList<>();
        List<String> buggyDeveloperSyntax = new ArrayList<>();
        List<String> buggyToolSyntax = new ArrayList<>();
        List<String> toolDeveloperSyntax = new ArrayList<>();
        List<String> developerToolSyntax = new ArrayList<>();
        while (true) {
            String buggyFile = bugLoc + File.separator + "buggy" + fileIndex + ".java";
            String developerPatch = bugLoc + File.separator + "developer-patch" + fileIndex + ".java";
            String toolPatch = bugLoc + File.separator + "tool-patch" + fileIndex + ".java";
            if (!new File(buggyFile).exists()) break;

            ExtractIngredientsContext eic = new ExtractIngredientsContext();
            eic.obtainIngredientsForSource(buggyFile, bugLoc.getAbsolutePath());
            eic.obtainIngredientsForSource(developerPatch, bugLoc.getAbsolutePath());
            eic.obtainIngredientsForSource(toolPatch, bugLoc.getAbsolutePath());

            List<HashSet<Integer>> modifiedLines = getFixLocations(buggyFile, developerPatch);
            HashSet<Integer> developerLines = modifiedLines.get(1);
            HashSet<Integer> buggyDeveloperLines = modifiedLines.get(0);

            modifiedLines = getFixLocations(buggyFile, toolPatch);
            HashSet<Integer> toolLines = modifiedLines.get(1);
            HashSet<Integer> buggyToolLines = modifiedLines.get(0);

            modifiedLines = getFixLocations(toolPatch, developerPatch);
            HashSet<Integer> toolDeveloperLines = modifiedLines.get(0);
            HashSet<Integer> developerToolLines = modifiedLines.get(1);

            String developerIngredient = bugLoc + File.separator + "developer-patch" + fileIndex + ".java" + ".ingredients";
            String toolIngredient = bugLoc + File.separator + "tool-patch" + fileIndex + ".java" + ".ingredients";
            String buggyIngredient = bugLoc + File.separator + "buggy" + fileIndex + ".java" + ".ingredients";

            List<String> lines = FileToLines.fileToLines(developerIngredient);

            List<IngredientInfo> developerFileingredients = new ArrayList<IngredientInfo>();
            for (int i = 0; i < lines.size(); i++) {
                String tmp = lines.get(i);
                IngredientInfo ii = new IngredientInfo(tmp);
                //ii.addSemantics(fixedIngredientsSemantic.get(i));
                developerFileingredients.add(ii);
            }

            for (IngredientInfo ingredient : developerFileingredients) {
                boolean contains = true;
                for (int l = ingredient.startLine; l <= ingredient.endLine; l++)
                    if (!developerLines.contains(l)) contains = false;
                if (contains) {
                    developerSyntax.addAll(ingredient.siblings);
                    developerSyntax.addAll(ingredient.contexts);
                }

                contains = true;

                for (int l = ingredient.startLine; l <= ingredient.endLine; l++)
                    if (!developerToolLines.contains(l)) contains = false;
                if (contains) {
                    developerToolSyntax.addAll(ingredient.siblings);
                    developerToolSyntax.addAll(ingredient.contexts);
                }
            }


            lines = FileToLines.fileToLines(toolIngredient);

            List<IngredientInfo> toolFileingredients = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String tmp = lines.get(i);
                IngredientInfo ii = new IngredientInfo(tmp);
                //ii.addSemantics(fixedIngredientsSemantic.get(i));
                toolFileingredients.add(ii);
            }

            for (IngredientInfo ingredient : toolFileingredients) {
                boolean contains = true;
                for (int l = ingredient.startLine; l <= ingredient.endLine; l++)
                    if (!toolLines.contains(l)) contains = false;
                if (contains) {
                    toolSyntax.addAll(ingredient.siblings);
                    toolSyntax.addAll(ingredient.contexts);
                }

                contains = true;

                for (int l = ingredient.startLine; l <= ingredient.endLine; l++)
                    if (!toolDeveloperLines.contains(l)) contains = false;
                if (contains) {
                    toolDeveloperSyntax.addAll(ingredient.siblings);
                    toolDeveloperSyntax.addAll(ingredient.contexts);
                }
            }

            lines = FileToLines.fileToLines(buggyIngredient);
            List<IngredientInfo> buggyIngredients = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String tmp = lines.get(i);
                IngredientInfo ii = new IngredientInfo(tmp);
                buggyIngredients.add(ii);
            }

            for (IngredientInfo ingredient : buggyIngredients) {
                boolean contains = true;
                for (int l = ingredient.startLine; l <= ingredient.endLine; l++)
                    if (!buggyDeveloperLines.contains(l)) contains = false;
                if (contains) {
                    buggyDeveloperSyntax.addAll(ingredient.siblings);
                    buggyDeveloperSyntax.addAll(ingredient.contexts);
                }

                contains = true;
                for (int l = ingredient.startLine; l <= ingredient.endLine; l++)
                    if (!buggyToolLines.contains(l)) contains = false;
                if (contains) {
                    buggyToolSyntax.addAll(ingredient.siblings);
                    buggyToolSyntax.addAll(ingredient.contexts);
                }
            }

            fileIndex++;

        }

        WriteLinesToFile.writeToFiles(buggyToolSyntax.toString() + "\n" + toolSyntax.toString(),
                bugLoc + File.separator + "buggy-tool-syntax.txt");
        WriteLinesToFile.writeToFiles(buggyToolSyntax.toString() + "\n" + developerSyntax.toString(),
                bugLoc + File.separator + "buggy-developer-syntax.txt");
        WriteLinesToFile.writeToFiles(toolSyntax.toString() + "\n" + developerSyntax.toString(),
                bugLoc + File.separator + "tool-developer-syntax.txt");
        WriteLinesToFile.writeToFiles(toolDeveloperSyntax.toString() + "\n" + developerToolSyntax.toString(),
                bugLoc + File.separator + "tool-developer-syntax-2.txt");

    }

    public static void investigateSemantic(File bugLoc) throws Exception {
        System.out.println(bugLoc.getAbsolutePath());

        List<String> developerSemantic = new ArrayList<>();
        List<String> toolSemantic = new ArrayList<>();
        List<String> buggyDeveloeprSemantic = new ArrayList<>();
        List<String> buggyToolSemantic = new ArrayList<>();
        List<String> developerToolSemantic = new ArrayList<>();
        List<String> toolDeveloperSemantic = new ArrayList<>();

        int fileIndex = 1;
        while (true) {
            String buggyFile = bugLoc + File.separator + "buggy" + fileIndex + ".java";
            String developerPatch = bugLoc + File.separator + "developer-patch" + fileIndex + ".java";
            String toolPatch = bugLoc + File.separator + "tool-patch" + fileIndex + ".java";
            if (!new File(buggyFile).exists()) break;


            // this has already been computed in the investigate syntax method.

//            ExtractIngredientsContext eic = new ExtractIngredientsContext();
//            eic.obtainIngredientsForSource(buggyFile, bugLoc.getAbsolutePath());
//            eic.obtainIngredientsForSource(developerPatch, bugLoc.getAbsolutePath());
//            eic.obtainIngredientsForSource(toolPatch, bugLoc.getAbsolutePath());

            ExtractSemanticFeature esf = new ExtractSemanticFeature();
            esf.extractSemanticInfo(bugLoc.getAbsolutePath(), new File(buggyFile));
            esf.generateSemanticForIngredientFile(bugLoc.getAbsolutePath(), new File(buggyFile));

            esf.extractSemanticInfo(bugLoc.getAbsolutePath(), new File(developerPatch));
            esf.generateSemanticForIngredientFile(bugLoc.getAbsolutePath(), new File(developerPatch));

            esf.extractSemanticInfo(bugLoc.getAbsolutePath(), new File(toolPatch));
            esf.generateSemanticForIngredientFile(bugLoc.getAbsolutePath(), new File(toolPatch));


            List<HashSet<Integer>> modifiedLines = getFixLocations(buggyFile, developerPatch);
            HashSet<Integer> developerLines = modifiedLines.get(1);
            HashSet<Integer> buggyDeveloperLines = modifiedLines.get(0);

            modifiedLines = getFixLocations(buggyFile, toolPatch);
            HashSet<Integer> toolLines = modifiedLines.get(1);
            HashSet<Integer> buggyToolLines = modifiedLines.get(0);

            modifiedLines = getFixLocations(toolPatch, developerPatch);
            HashSet<Integer> developerToolLines = modifiedLines.get(1);
            HashSet<Integer> toolDeveloperLines = modifiedLines.get(0);


            String developerIngredient = bugLoc + File.separator + "developer-patch" + fileIndex + ".java" + ".ingredients";
            String toolIngredient = bugLoc + File.separator + "tool-patch" + fileIndex + ".java" + ".ingredients";
            String buggyIngredient = bugLoc + File.separator + "buggy" + fileIndex + ".java" + ".ingredients";


            String developerSemanticFile = bugLoc + File.separator + "developer-patch" + fileIndex + ".java" + ".semantic";
            String toolSemanticFile = bugLoc + File.separator + "tool-patch" + fileIndex + ".java" + ".semantic";
            String buggySemanticFile = bugLoc + File.separator + "buggy" + fileIndex + ".java" + ".semantic";


            List<String> developerSemanticLines = FileToLines.fileToLines(developerSemanticFile);
            List<String> toolSemanticLines = FileToLines.fileToLines(toolSemanticFile);
            List<String> buggySemanticLines = FileToLines.fileToLines(buggySemanticFile);

            List<String> lines = FileToLines.fileToLines(developerIngredient);

            List<IngredientInfo> developerFileingredients = new ArrayList<IngredientInfo>();
            for (int i = 0; i < lines.size(); i++) {
                String tmp = lines.get(i);
                IngredientInfo ii = new IngredientInfo(tmp);
                //ii.addSemantics(fixedIngredientsSemantic.get(i));
                developerFileingredients.add(ii);
            }

            for (int i = 0; i < developerFileingredients.size(); i++) {
                IngredientInfo ingredient = developerFileingredients.get(i);
                boolean contains = true;
                for (int l = ingredient.startLine; l <= ingredient.endLine; l++)
                    if (!developerLines.contains(l)) contains = false;
                if (contains) {
                    developerSemantic.addAll(DataTransfer.turnStringToListString(developerSemanticLines.get(i)));
                }

                contains = true;
                for (int l = ingredient.startLine; l <= ingredient.endLine; l++)
                    if (!developerToolLines.contains(l)) contains = false;
                if (contains) {
                    developerToolSemantic.addAll(DataTransfer.turnStringToListString(developerSemanticLines.get(i)));
                }
            }


            lines = FileToLines.fileToLines(toolIngredient);

            List<IngredientInfo> toolFileingredients = new ArrayList<IngredientInfo>();
            for (int i = 0; i < lines.size(); i++) {
                String tmp = lines.get(i);
                IngredientInfo ii = new IngredientInfo(tmp);
                //ii.addSemantics(fixedIngredientsSemantic.get(i));
                toolFileingredients.add(ii);
            }

            for (int i = 0; i < toolFileingredients.size(); i++) {
                IngredientInfo ingredient = toolFileingredients.get(i);
                boolean contains = true;
                for (int l = ingredient.startLine; l <= ingredient.endLine; l++)
                    if (!toolLines.contains(l)) contains = false;
                if (contains) {
                    toolSemantic.addAll(DataTransfer.turnStringToListString(toolSemanticLines.get(i)));
                    //toolSemantic.addAll(ingredient.contexts);
                }

                contains = true;
                for (int l = ingredient.startLine; l <= ingredient.endLine; l++)
                    if (!toolDeveloperLines.contains(l)) contains = false;
                if (contains) {
                    toolDeveloperSemantic.addAll(DataTransfer.turnStringToListString(toolSemanticLines.get(i)));
                    //toolSemantic.addAll(ingredient.contexts);
                }
            }

            lines = FileToLines.fileToLines(buggyIngredient);

            List<IngredientInfo> buggyIngredients = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String tmp = lines.get(i);
                IngredientInfo ii = new IngredientInfo(tmp);
                buggyIngredients.add(ii);
            }

            for (int i = 0; i < buggyIngredients.size(); i++) {
                IngredientInfo ingredient = buggyIngredients.get(i);
                boolean contains = true;
                for (int l = ingredient.startLine; l <= ingredient.endLine; l++)
                    if (!buggyToolLines.contains(l)) contains = false;
                if (contains) {
                    buggyToolSemantic.addAll(DataTransfer.turnStringToListString(buggySemanticLines.get(i)));
                    //toolSemantic.addAll(ingredient.contexts);
                }

                contains = true;
                for (int l = ingredient.startLine; l <= ingredient.endLine; l++)
                    if (!buggyDeveloperLines.contains(l)) contains = false;
                if (contains) {
                    buggyDeveloeprSemantic.addAll(DataTransfer.turnStringToListString(buggySemanticLines.get(i)));
                    //toolSemantic.addAll(ingredient.contexts);
                }

            }
            fileIndex++;
        }

        WriteLinesToFile.writeToFiles(buggyToolSemantic + "\n" + toolSemantic.toString(),
                bugLoc + File.separator + "buggy-tool-semantic.txt");
        WriteLinesToFile.writeToFiles(buggyDeveloeprSemantic + "\n" + developerSemantic.toString(),
                bugLoc + File.separator + "buggy-developer-semantic.txt");
        WriteLinesToFile.writeToFiles(toolSemantic.toString() + "\n" + developerSemantic.toString(),
                bugLoc + File.separator + "tool-developer-semantic.txt");
        WriteLinesToFile.writeToFiles(developerToolSemantic.toString() + "\n" + toolDeveloperSemantic.toString(),
                bugLoc + File.separator + "developer-tool-semantic.txt");
    }

    public static int sumIntList(List<Integer> list) {
        int sum = 0;
        for (int i : list)
            sum+= i;
        return sum;
    }

    public static void outputReuslts(File bugLoc) {

        //    System.out.println(bugLoc.getAbsolutePath());
        String location = bugLoc.getAbsolutePath().replaceAll("/", ".");
        location = location.substring(location.indexOf("patches") + 8);
        //System.out.println(location);
        List<String> developerLines = FileToLines.fileToLines(bugLoc + File.separator + "developer-modification.txt");
        List<String> toolLines = FileToLines.fileToLines(bugLoc + File.separator + "tool-modification.txt");
        List<String> developerToolLines = FileToLines.fileToLines(bugLoc + File.separator + "developer-tool-modification.txt");

        List<Integer> dmodificatin = DataTransfer.turnStringToListIntegerTab(developerLines.get(0));
        List<Integer> tmodification = DataTransfer.turnStringToListIntegerTab(toolLines.get(0));
        List<Integer> dtmodification = DataTransfer.turnStringToListIntegerTab(developerToolLines.get(0));

        int developerASTDifferencing = sumIntList(dmodificatin);
        int toolASTDifferencing = sumIntList(tmodification);
        int developerToolASTDifferencing = sumIntList(dtmodification);

        developerLines = FileToLines.fileToLines(bugLoc + File.separator + "buggy-developer-cosine.txt");
        toolLines = FileToLines.fileToLines(bugLoc + File.separator + "buggy-tool-cosine.txt");
        developerToolLines = FileToLines.fileToLines(bugLoc + File.separator + "developer-tool-cosine.txt");

        List<Integer> dmodificatinbuggy = DataTransfer.turnStringToListIntegerTab(developerLines.get(0));
        List<Integer> tmodificationbuggy = DataTransfer.turnStringToListIntegerTab(toolLines.get(0));
        dmodificatin = DataTransfer.turnStringToListIntegerTab(developerLines.get(1));
        tmodification = DataTransfer.turnStringToListIntegerTab(toolLines.get(1));

        double developerCosine = 1 - Algorithm.Cosine(dmodificatinbuggy, dmodificatin);
        double toolCosine = 1 - Algorithm.Cosine(tmodificationbuggy, tmodification);
        //toolCosine = 1 - Algorithm.Cosine(tmodification, dmodificatin);
        double developerToolCosine = 1 - Algorithm.Cosine(
                DataTransfer.turnStringToListIntegerTab(developerToolLines.get(0)),
                DataTransfer.turnStringToListIntegerTab(developerToolLines.get(1))
        );

        developerLines = FileToLines.fileToLines(bugLoc + File.separator + "developer-variables.txt");
        toolLines = FileToLines.fileToLines(bugLoc + File.separator + "tool-variables.txt");
        List<String> buggyDeveloperLines = FileToLines.fileToLines(bugLoc + File.separator + "buggy-developer-variables.txt");
        List<String> buggyToolLines = FileToLines.fileToLines(bugLoc + File.separator + "buggy-tool-variables.txt");

        List<String> buggyDeveloperVariables = DataTransfer.turnStringToListStringTab(buggyDeveloperLines.get(0));
        List<String> developerVariables = DataTransfer.turnStringToListStringTab(developerLines.get(0));

        List<String> buggyToolVariables = DataTransfer.turnStringToListStringTab(buggyToolLines.get(0));
        List<String> toolVariables = DataTransfer.turnStringToListStringTab(toolLines.get(0));

        int developerStringDistance = Algorithm.getStringEditDistance(buggyDeveloperVariables, developerVariables);
        int toolStringDistance = Algorithm.getStringEditDistance(buggyToolVariables, toolVariables);


        developerToolLines = FileToLines.fileToLines(bugLoc + File.separator + "developer-tool-variables.txt");
        List<String> toolDeveloperLines = FileToLines.fileToLines(bugLoc + File.separator + "tool-developer-variables.txt");

        List<String> developerToolVariables = DataTransfer.turnStringToListStringTab(developerToolLines.get(0));
        List<String> toolDeveloperVariables = DataTransfer.turnStringToListStringTab(toolDeveloperLines.get(0));


        int developerToolStringDistance = Algorithm.getStringEditDistance(developerToolVariables, toolDeveloperVariables);

        //toolStringDistance = Algorithm.getStringEditDistance(developerVariables, toolVariables);


        double s3developer = developerASTDifferencing + developerCosine + developerStringDistance;
        double s3tool = toolASTDifferencing + toolCosine + toolStringDistance;
        double s3developertool = developerToolASTDifferencing + developerToolCosine + developerToolStringDistance;

        double developerVariable = Algorithm.Jaccard(buggyDeveloperVariables, developerVariables);
        //System.out.println(buggyToolVariables.toString());
        //System.out.println(toolVariables.toString());

        double toolVariable = Algorithm.Jaccard(buggyToolVariables, toolVariables);
        //System.out.println(toolVariable);
        //toolVariable = Algorithm.Jaccard(developerVariables, toolVariables);
        double developerToolVariable = Algorithm.Jaccard(developerToolVariables, toolDeveloperVariables);


        developerLines = FileToLines.fileToLines(bugLoc + File.separator + "buggy-developer-syntax.txt");
        toolLines = FileToLines.fileToLines(bugLoc + File.separator + "buggy-tool-syntax.txt");
        developerToolLines = FileToLines.fileToLines(bugLoc + File.separator + "tool-developer-syntax-2.txt");

        List<String> buggydsyntax = DataTransfer.turnStringToListString(developerLines.get(0));
        List<String> buggytsyntax = DataTransfer.turnStringToListString(toolLines.get(0));
        List<String> dsyntax = DataTransfer.turnStringToListString(developerLines.get(1));
        List<String> tsyntax = DataTransfer.turnStringToListString(toolLines.get(1));
        double developerSyntax = Algorithm.Jaccard(buggydsyntax, dsyntax);
        double toolSyntax = Algorithm.Jaccard(buggytsyntax, tsyntax);
        //toolSyntax = Algorithm.Jaccard(dsyntax, tsyntax);
        double developerToolSyntax = Algorithm.Jaccard(
                DataTransfer.turnStringToListString(developerToolLines.get(0)),
                DataTransfer.turnStringToListString(developerToolLines.get(1))
        );

        developerLines = FileToLines.fileToLines(bugLoc + File.separator + "buggy-developer-semantic.txt");
        toolLines = FileToLines.fileToLines(bugLoc + File.separator + "buggy-tool-semantic.txt");
        developerToolLines = FileToLines.fileToLines(bugLoc + File.separator + "developer-tool-semantic.txt");
        List<String> buggydsemantic = DataTransfer.turnStringToListString(developerLines.get(0));
        List<String> buggytsemantic = DataTransfer.turnStringToListString(toolLines.get(0));

        List<String> dsemantic = DataTransfer.turnStringToListString(developerLines.get(1));
        List<String> tsemantic = DataTransfer.turnStringToListString(toolLines.get(1));
        double developerSemantic = Algorithm.Jaccard(buggydsemantic, dsemantic);
        double toolSemantic = Algorithm.Jaccard(buggytsemantic, tsemantic);
        //toolSemantic = Algorithm.Jaccard(dsemantic, tsemantic);
        double developerToolSemantic = Algorithm.Jaccard(DataTransfer.turnStringToListString(developerToolLines.get(0)),
                DataTransfer.turnStringToListString(developerToolLines.get(1)));

        double capgend = developerVariable * developerSyntax * developerSemantic;
        double capgent = toolVariable * toolSyntax * toolSemantic;
        double capgendt = developerToolVariable * developerToolSyntax * developerToolSemantic;



//        System.out.println(location + "\t" + s3developer + "\t" + s3tool + "\t" +
//                developerASTDifferencing + "\t" + toolASTDifferencing + "\t" + developerCosine + "\t" + toolCosine + "\t" +
//                developerStringDistance + "\t" + toolStringDistance + "\t" +
//                capgend + "\t" + capgent + "\t" + developerVariable + "\t" + toolVariable + "\t" +
//                developerSyntax + "\t" +toolSyntax + "\t" + developerSemantic + "\t" + toolSemantic);


        // The format of the output is as follows:

//        "bug\ts3-tool-buggy\ts3-tool-developer" +
//                "\tAST-tool-buggy\tAST-tool-developer\tCosine-tool-buggy\tCosine-tool-developer\ts3variable-tool-buggy" +
//                "\ts3variable-tool-developer" +
//                "\tcapgen-tool\tcapgen-developer" +
//                "\tvariable-tool-buggy\tvariable-tool-developer\tsyntax-tool-buggy\tsyntax-tool-developer" +
//                "\tsemantic-tool-buggy\tsemantic-tool-developer"

        System.out.println(location + "\t" + s3tool + "\t" + s3developertool + "\t" +
                toolASTDifferencing + "\t" + developerToolASTDifferencing + "\t" + toolCosine + "\t" + developerToolCosine + "\t" +
                toolStringDistance + "\t" + developerToolStringDistance + "\t" +
                capgent + "\t" + capgendt + "\t" + toolVariable + "\t" + developerToolVariable + "\t" +
                toolSyntax + "\t" + developerToolSyntax + "\t" + toolSemantic + "\t" + developerToolSemantic);

    }

    public static void main(String[] args) throws Exception {


        File file = new File("/Users/justin/Java/Dataset_Overfitting2/Dcorrect/CapGen/Lang/43/1");

        // This is to investigate the similarity of S3Fix and CapGen.
        // The input is a folder, which contains all the patches generated for a bug by a tool.
        // In each subfolder of this folder, it contains three files, which are
        // buggy#ID.java
        // developer-patch#ID.java
        // tool-patch#ID.java


        // The following three methods may need to be executed separately due to dependency conflict.
        investigateOperation(file);
        investigateSyntax(file);
        investigateSemantic(file);

        // Evaluate and Output all the similarity results.
        outputReuslts(file);

    }
}
