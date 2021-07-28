package org.capgen.main;

import org.capgen.algorithm.BackwardSlicing;
import org.capgen.algorithm.ForwardSlicing;
import org.capgen.algorithm.Slicing;
import org.capgen.entity.CFG;
import org.capgen.entity.CFGNode;
import org.capgen.entity.Function;
import org.capgen.entity.VariableUsage;
import org.capgen.util.*;

import java.io.File;
import java.util.*;

public class ExtractSemanticFeature {

    public List<String> extractContextFeatures(String astFile, String cfgFile, String useFile, List<Integer> lineID) throws Exception {

        List<String> slicedContext = new ArrayList<String>();
        HashMap<Integer, Function> functions = AnalyzeUnderstand.reloadFunctionInfo(cfgFile);
        HashMap<String, Integer> functionsMap = new HashMap<String, Integer>();
        for (int fid : functions.keySet()) {
            Function function = functions.get(fid);
            String hash = function.getName() + "\t" + function.getSourceFileName() + "\t" + function.getStartLine() + "\t" + function.getEndLine();
            functionsMap.put(hash, fid);
//			System.out.println(hash + "\t" + fid);
        }
//		System.out.println(functions.size());
        HashMap<Integer, HashMap<Integer, VariableUsage>> variableUses = AnalyzeUnderstand.reloadVariableUsage(useFile, functionsMap);
//		for (int id : variableUses.keySet()) {
//			System.out.println(id);
//			HashMap<Integer, VariableUsage> uses = variableUses.get(id);
//			for (int line : uses.keySet())
//				System.out.println(line + "\t" + uses.get(line).getDefLines());
//		}
//
//		System.out.println(variableUses.size());
        HashMap<Integer, HashSet<Integer>> functionTargetLines = new HashMap<Integer, HashSet<Integer>>();

        HashMap<Integer, HashSet<Integer>> lineASTMap = AnalyzeUnderstand.reloadASTMapping(astFile);


        for (int line : lineID) {
            for (int fid : functions.keySet()) {
                if (functions.get(fid).withinRange(line)) {
                    if (!functionTargetLines.containsKey(fid))
                        functionTargetLines.put(fid, new HashSet<Integer>());
                    functionTargetLines.get(fid).add(line);
                }
            }
        }

        // slice the program function per function
        for (int fid : functionTargetLines.keySet()) {
            Function function = functions.get(fid);
            CFG cfg = function.getCFG();

            if (cfg.getEntry() == null || cfg.getExit() == null)
                continue;
            if (!variableUses.containsKey(fid)) continue;
            Collection<VariableUsage> variableUsages = variableUses.get(fid).values();
            HashSet<Integer> lines = functionTargetLines.get(fid);


            // backward and forward slicing to see the relations with the current node and the context nodes
            Slicing forwardSlicing = new ForwardSlicing(cfg, variableUsages);
            Slicing backwaSlicing = new BackwardSlicing(cfg, variableUsages);
            for (int line : lines) {

                List<VariableUsage> useVariables = new ArrayList<VariableUsage>();
                List<VariableUsage> defVariables = new ArrayList<VariableUsage>();

                for (VariableUsage variable : variableUsages) {
                    if (variable.getUseLines().contains(line))
                        useVariables.add(variable);
                    if (variable.getDefLines().contains(line) || variable.getSetLines().contains(line))
                        defVariables.add(variable);
                }
                HashSet<Integer> targetASTs = lineASTMap.get(line);
                if (targetASTs == null) continue;
                // go through all variables to see if there are parameters

//				System.out.println("Forward Slicing");
                HashSet<CFGNode> slicedNodes = forwardSlicing.getSlicedNode(line);
                if (line == 1135)
                    System.out.println(line + "\t" + slicedNodes.size() + "\tforward");
                for (CFGNode node : slicedNodes) {
                    if (line == 1135)
                        System.out.println(node.getLineStart() + "\t" + node.getLineEnd());
                    for (int l = node.getLineStart(); l <= node.getLineEnd(); l++) {
                        if (!lineASTMap.containsKey(l)) continue;
                        // Also considered the inserted lines
                        // We can remove those lines inserted together by adding a checker
                        //if (lines.contains(l)) continue;

                        HashSet<Integer> contextAsts = lineASTMap.get(l);

                        for (int ast : contextAsts) {
                            slicedContext.add(ASTNodeType.get(ast));
                        }

                    }
                }

//				System.out.println("Backward Slicing");
                slicedNodes = backwaSlicing.getSlicedNode(line);
                if (line == 1135)
                    System.out.println(line + "\t" + slicedNodes.size());
                for (CFGNode node : slicedNodes) {
                    for (int l = node.getLineStart(); l <= node.getLineEnd(); l++) {
                        //if (l == line) continue;
                        if (line == 1135)
                            System.out.println(node.getLineStart() + "\t" + node.getLineEnd());
                        if (!lineASTMap.containsKey(l)) continue;
//						System.out.println("Line:\t" + l);
                        // Also considered the inserted lines
                        // We can remove those lines inserted together by adding a checker
                        //if (lines.contains(l)) continue;
                        HashSet<Integer> contextAsts = lineASTMap.get(l);

                        for (int ast : contextAsts) {
                            slicedContext.add(ASTNodeType.get(ast));
                        }

                    }
                }
            }
        }

        return slicedContext;
    }

    public void generateSemanticForIngredientFile(String saveLoc, File file) throws Exception {
        String astFile = saveLoc + File.separator + file.getName() + ".ast";
        String useFile = saveLoc + File.separator + file.getName() + ".use";
        String cfgFile = saveLoc + File.separator + file.getName() + ".cfg";

        String ingredientsFile = saveLoc + File.separator + file.getName() + ".ingredients";
        String saveFile = saveLoc + File.separator + file.getName() + ".semantic";
        //File checkFile = new File(saveFile);
        //if (checkFile.exists() && !Properties.updateData) return;
        List<String> saveLines = new ArrayList<String>();
        List<String> ingredients = FileToLines.fileToLines(ingredientsFile);

        HashSet<Integer> targetLines = new HashSet<Integer>();
        HashMap<Integer, List<String>> lineSlicedNodes = new HashMap<Integer, List<String>>();
        System.out.println(ingredientsFile);
        for (String ingredient : ingredients) {
            String[] split2 = ingredient.split("\t");
            if (split2.length < 5) continue;
            if (!split2[4].contains(":")) continue;
            split2 = split2[4].split(":");
            int startLine = Integer.parseInt(split2[0]);
            int endLine = Integer.parseInt(split2[1]);
            for (int l = startLine; l <= endLine; l++) {
                targetLines.add(l);
            }
        }

        for (int line : targetLines) {
            List<Integer> tmpLines = new ArrayList<Integer>();
            tmpLines.add(line);


            List<String> slicedContext = extractContextFeatures(astFile, cfgFile, useFile, tmpLines);
            lineSlicedNodes.put(line, slicedContext);
        }

        for (String ingredient : ingredients) {
            String[] split2 = ingredient.split("\t");
            int startLine = 0;
            int endLine = -1;
            if (split2.length >= 5) {
                if (!split2[4].contains(":")) continue;
                split2 = split2[4].split(":");

                startLine = Integer.parseInt(split2[0]);
                endLine = Integer.parseInt(split2[1]);
            }
            List<String> contexts = new ArrayList<String>();
            for (int l = startLine; l <= endLine; l++) {
                contexts.addAll(lineSlicedNodes.get(l));
            }
            saveLines.add(contexts.toString());
        }

        WriteLinesToFile.writeLinesToFile(saveLines, saveFile);
    }

    public void extractSemanticInfo(String saveLoc, File file) throws Exception {
        String buggyFile = file.getAbsolutePath();
        System.out.println("BuggyFile:\t" + buggyFile);
        file = new File(buggyFile);
        if (!file.exists()) return;

        String astFile = saveLoc + File.separator + file.getName() + ".ast";
        String udbFile = saveLoc + File.separator + file.getName() + ".udb";
        String useFile = saveLoc + File.separator + file.getName() + ".use";
        String cfgFile = saveLoc + File.separator + file.getName() + ".cfg";

        File indexFile = new File(astFile);
        if (!indexFile.exists()) {
            GetLineToASTMapping.mapLineToAST(buggyFile, astFile);
        }

        indexFile = new File(udbFile);
        if (!indexFile.exists()) {
            List<String> sourceFiles = new ArrayList<String>();
            sourceFiles.add(buggyFile);
            UnderstandJavaSourceFiles.createAnalysisDB(sourceFiles, udbFile);
        }

        indexFile = new File(cfgFile);
        if (!indexFile.exists()) {
            AnalyzeUnderstand.extractCFG(udbFile, cfgFile);
            System.out.println("Finish extracting cfg file\t" + cfgFile);
        }

        indexFile = new File(useFile);
        if (!indexFile.exists()) {
            AnalyzeUnderstand.extractVariableUse(udbFile, useFile);
            System.out.println("Finish extracting use file\t" + useFile);
        }
    }
}
