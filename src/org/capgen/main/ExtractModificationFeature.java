package org.capgen.main;

import org.capgen.entity.ModificationFeature;
import org.capgen.util.ASTHelper;
import org.capgen.util.ASTNodeType;
import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.*;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.Generators;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.ITree;
import org.capgen.util.FileToLines;
import org.capgen.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class ExtractModificationFeature {
	
	
	private static int getParentId(ITree node, List<ITree> nodes) {
		int index = -1;
		List<HashSet<Integer>> nodeChildIds = new ArrayList<HashSet<Integer>>();
		for (int i = 0; i < nodes.size(); i++) {
			List<ITree> children = nodes.get(i).getChildren();
			HashSet<Integer> ids = new HashSet<Integer>();
			for (ITree child : children) {
				ids.add(child.getId());
			}
			nodeChildIds.add(ids);
		}
		
		for (int i = 0; i < nodes.size(); i++) {
			if (nodeChildIds.get(i).contains(node.getId()))
				return i;
		}
		return index;
	}
	
	private static boolean withinRange(Pair<Integer,Integer> r1, Pair<Integer,Integer> r2) {
		if (r1.getKey() >= r2.getKey() && r1.getValue() <= r2.getValue()) return true;
		else return false;
	}

	public static HashMap<Integer, Integer> getNodeCount(String filePath, HashSet<Integer> lines) throws Exception{

		ITree root = Generators.getInstance().getTree(filePath).getRoot();

		List<ITree> nodes = new ArrayList<ITree>();

		if(root == null){
			System.out.println("Empty tree");
			return null;
		}
		HashMap<Integer, Integer> nodeCount = new HashMap<>();
		HashMap<Integer,Integer> dstLineMap = ASTHelper.getLineNumber(filePath);
		nodes.add(root);
		int start = 0;
		while (start < nodes.size()) {
			ITree node = nodes.get(start);
			List<ITree> children = node.getChildren();
			int line = -1;
			if (dstLineMap.containsKey(node.getPos()))
				line = dstLineMap.get(node.getPos());
			if (lines.contains(line)) {
				if (!nodeCount.containsKey(node.getType()))
					nodeCount.put(node.getType(),1);
				nodeCount.put(node.getType(), nodeCount.get(node.getType()) + 1);
			}
//			System.out.println(start + "\t" + children.size());
//			System.out.println(ASTNodeType.get(node.getType()));
			for (int i = 0; i < children.size(); i++) {
				ITree child = children.get(i);
				nodes.add(child);
			}
			start++;
		}
		return nodeCount;
	}

	public static List<Pair<String, Integer>> getVariableIndex(String filePath, HashSet<Integer> lines) throws Exception{

		ITree root = Generators.getInstance().getTree(filePath).getRoot();
		String content = FileToLines.readFile(filePath);
		List<Pair<String, Integer>> variableIndex = new ArrayList<>();
		List<ITree> nodes = new ArrayList<ITree>();
		if(root == null){
			System.out.println("Empty tree");
			return null;
		}
		HashMap<Integer, Integer> nodeCount = new HashMap<>();
		HashMap<Integer,Integer> dstLineMap = ASTHelper.getLineNumber(filePath);
		nodes.add(root);
		int start = 0;
		while (start < nodes.size()) {
			ITree node = nodes.get(start);
			List<ITree> children = node.getChildren();
			int line = -1;
			if (dstLineMap.containsKey(node.getPos()))
				line = dstLineMap.get(node.getPos());
			if (lines.contains(line)) {
				if (node.getType() == ASTNodeType.SIMPLE_NAME ||
						node.getType() == ASTNodeType.NULL_LITERAL ||
						node.getType() == ASTNodeType.NUMBER_LITERAL ||
						node.getType() == ASTNodeType.CHARACTER_LITERAL ||
						node.getType() == ASTNodeType.STRING_LITERAL ||
						node.getType() == ASTNodeType.TYPE_LITERAL ||
						node.getType() == ASTNodeType.BOOLEAN_LITERAL) {
					String name = content.substring(node.getPos(), node.getEndPos());
					variableIndex.add(new Pair<>(name, node.getPos()));
				}
			}
//			System.out.println(start + "\t" + children.size());
//			System.out.println(ASTNodeType.get(node.getType()));
			for (int i = 0; i < children.size(); i++) {
				ITree child = children.get(i);
				nodes.add(child);
			}
			start++;
		}
		return variableIndex;
	}

	public static List<ModificationFeature> ExtractASTDifferenceFeature(String fileSrc, String fileDst) throws Exception {
		ITree src = Generators.getInstance().getTree(fileSrc).getRoot();
		ITree dst = Generators.getInstance().getTree(fileDst).getRoot();


		String buggyContent = FileToLines.readFile(fileSrc);
		String fixedContent = FileToLines.readFile(fileDst);

//		if (newLine) {
//			buggyContent = FileToLines.fileToStringNewLineBreak(fileSrc);
//			fixedContent = FileToLines.fileToStringNewLineBreak(fileDst);
//		}

		Matcher m = Matchers.getInstance().getMatcher(src, dst);
		MappingStore mapping = m.getMappings();
		m.match();
		ActionGenerator g = new ActionGenerator(src, dst, mapping);	
		List<Action> actions = g.generate();
		
		List<ITree>  nodes = new ArrayList<ITree>();
	
		for (Action action : actions) {
			nodes.add(action.getNode());
		}
		
		List<Integer> parentId = new ArrayList<Integer>();
		
		for (int i = 0; i < nodes.size(); i++) {
			ITree node = nodes.get(i);
			int index = getParentId(node, nodes);
			parentId.add(index);
//			System.out.println(i + "\t" + index);
		}
		
		ModificationFeature completeFeature = new ModificationFeature();
		ModificationFeature filteredFeature = new ModificationFeature();
		
		for (int i = 0; i < nodes.size(); i++) {
			
			Action action = actions.get(i);
			ITree node = nodes.get(i);
			int opId = -1;
			if (action instanceof Insert) {
				opId = 0;	
			} else if (action instanceof Delete) {
				opId = 1;
			} else if (action instanceof Move || action instanceof Update) {	
				if (action instanceof Move) opId = 2;
				else opId = 3;	
			}

			String ingredient = "";
			// System.out.println(opId + "\t" + nodes.get(i));

			int length = node.getEndPos() - node.getPos();
			if (length > 0 && length < 1000) {
				if (opId == 1 || opId == 2 || opId == 3)
					ingredient = buggyContent.substring(node.getPos(), node.getEndPos());
				else
					ingredient = fixedContent.substring(node.getPos(), node.getEndPos());
				//System.out.println(opId + "\t" + AST.ASTNodeType.get(node.getType()));
			}
			ingredient = ingredient.replace("\n", "").replace("\r", "");
			ingredient = ingredient.replaceAll("[ ]{2,}", " ").toLowerCase();
			//System.out.println(ingredient + "\t" + Splitter.splitSourceCode(ingredient).length);
//			System.out.println(action + "\t" + node.toString() + "\t" + node.getPos() + "\t" + node.getEndPos());
			if (parentId.get(i) == -1) {
				filteredFeature.addFeature(node.getType(), opId, ingredient);
			}
			completeFeature.addFeature(node.getType(), opId, ingredient);
			int featureId = (node.getType() - 1) + opId * ModificationFeature.nodeNum;
			//System.out.println(node.getType() + "\t" + ASTNodeType.get(node.getType()) + "\t" + ingredient);
		}
		List<ModificationFeature> features = new ArrayList<>();
		features.add(filteredFeature);
		features.add(completeFeature);
		return features;	
	}
	
	public static List<ModificationFeature> ExtractASTDifferenceFeature(String fileSrc, String fileDst, List<Pair<Integer,Integer>> lineRanges) throws Exception {
		
		
		
		ITree src = Generators.getInstance().getTree(fileSrc).getRoot();
		ITree dst = Generators.getInstance().getTree(fileDst).getRoot();
		Matcher m = Matchers.getInstance().getMatcher(src, dst);
		MappingStore mapping = m.getMappings();
		m.match();
		ActionGenerator g = new ActionGenerator(src, dst, mapping);	
		List<Action> actions = g.generate();
		
		
		List<ITree>  nodes = new ArrayList<ITree>();
		
		
		for (Action action : actions) {
			nodes.add(action.getNode());
		}
		
		List<Integer> parentId = new ArrayList<Integer>();
		
		for (int i = 0; i < nodes.size(); i++) {
			ITree node = nodes.get(i);
			int index = getParentId(node, nodes);
			parentId.add(index);
//			System.out.println(i + "\t" + index);
		}
		
		HashMap<Integer,Integer> srcLineMap = ASTHelper.getLineNumber(fileSrc);
		HashMap<Integer,Integer> dstLineMap = ASTHelper.getLineNumber(fileDst);
		
		List<ModificationFeature> mfs = new ArrayList<ModificationFeature>();
		for (int i = 0; i < lineRanges.size(); i++) {
			mfs.add(new ModificationFeature());
		}
		
		System.out.println("Ranges:==");
		for (int i = 0; i < lineRanges.size(); i++) {
			System.out.println(lineRanges.get(i).toString());
		}
		
		for (int i = 0; i < nodes.size(); i++) {
			if (parentId.get(i) == -1) {
				Action action = actions.get(i);
				ITree node = nodes.get(i);
				int startLine = 0;
				int endLine = 0;
				int opId = -1;
				if (action instanceof Insert) {
					opId = 0;
					startLine = dstLineMap.get(node.getPos());
					endLine = dstLineMap.get(node.getEndPos());
					
				} else if (action instanceof Delete) {
					opId = 1;
					ITree parent = node.getParent();
//					ITree dstNode = mapping.getDst(parent);

					// Get line range
					List<ITree> children = parent.getChildren();
					int begin = srcLineMap.get(children.get(0).getPos());
					int end =  srcLineMap.get(children.get(children.size() - 1).getEndPos());
					for (int j = 0; j < children.size(); j++) {
						ITree child = children.get(j);
						if (child.equals(node)) {
							int preIndex = j - 1;
							int nextIndex = j + 1;
							while (preIndex >=0 && mapping.getDst(children.get(preIndex)) == null) 
								preIndex--;
							while (nextIndex < children.size() && mapping.getDst(children.get(nextIndex)) == null)
								nextIndex++;
							
//							System.out.println("Index == " + preIndex + "\t" + nextIndex + "\t" + children.size());
							if (preIndex >=0 && mapping.getDst(children.get(preIndex)) != null)
								begin = dstLineMap.get(mapping.getDst(children.get(preIndex)).getEndPos());
							if (nextIndex < children.size() && mapping.getDst(children.get(nextIndex)) != null)
								end = dstLineMap.get(mapping.getDst(children.get(nextIndex)).getPos());
							break;
						}
					}
					
//					System.out.println(srcLineMap.get(node.getPos()) + "\t" + srcLineMap.get(node.getEndPos()));
//					System.out.println(begin + "\t" + end);
					startLine = begin;
					endLine = end;
//					System.out.println(srcLineMap.get(parent.getPos()) + "\t" + srcLineMap.get(parent.getEndPos()));
//					startLine = dstLineMap.get(dstNode.getPos());
//					endLine = dstLineMap.get(dstNode.getEndPos());
				
				} else if (action instanceof Move || action instanceof Update) {
					
					if (action instanceof Move) opId = 2;
					else opId = 3;
					
					ITree dstNode = mapping.getDst(node);
					startLine = dstLineMap.get(dstNode.getPos());
					endLine = dstLineMap.get(dstNode.getEndPos());
//					System.out.println(startLine + "\t" + endLine);
				} 
				
				Pair<Integer,Integer> range = new Pair<Integer,Integer>(startLine, endLine);
				
				int flag = -1;
				for (int index = 0; index < lineRanges.size(); index++) {
					if (withinRange(range, lineRanges.get(index)))
						flag = index;
				}
//				System.out.println("flag\t" + flag + "\t" + startLine + "\t" + endLine);
				if (flag >= 0) {
					ModificationFeature mf = new ModificationFeature();
					System.out.println(node.getType() + "\t" + opId);
					mf.addFeature(node.getType(), opId);
//					System.out.println(mf.toString());
					mfs.get(flag).addFeatures(mf);
					System.out.println(action.getClass() + "\t" + ASTNodeType.get(node.getType()) + "\t" + startLine + "\t" + endLine);
				}
			}
		}
		System.out.println(mfs.toString());
		return mfs;
	}

	private static int getChildIndex(ITree parent, ITree node) {
		int index = 0;
		List<ITree> children = parent.getChildren();
		for (int i = 0; i < children.size(); i++) 
			if (children.get(i).equals(node)) return index;
		return 0;
	}
	
	private static List<String> getTargetLabelName(ITree node) {
		List<String> names = new ArrayList<String>();
		List<ITree> queueList = new ArrayList<ITree>();
		queueList.add(node);
		int index = 0;
		while (index < queueList.size()) {
			ITree current = queueList.get(index++);
			ITree parent = current.getParent();
			List<ITree> children = current.getChildren();
			for (ITree child : children)
				queueList.add(child);
			if (!current.getLabel().equals("") && current.getType() == ASTNodeType.SIMPLE_NAME ) {
				if (ASTNodeType.SimpleNameParent.contains(parent.getType())) {
					if (parent.getType() == ASTNodeType.METHOD_INVOCATION && getChildIndex(parent,current) > 0)
						names.add(current.getLabel());	
					else names.add(current.getLabel());
				}
			}
		}
		return names;
	}


	public static void main(String[] args) throws Exception {
		String prefix = "../Defects4J";
		String file1 = prefix + File.separator + "Splitter.java";
		String file2 = prefix + File.separator + "Splitter2.java";
		Pair<Integer,Integer> range = new Pair<Integer,Integer>(230,250);
		List<Pair<Integer,Integer>> ranges = new ArrayList<Pair<Integer,Integer>>();
		ranges.add(range);
		
		Run.initGenerators();
		
		List<ModificationFeature> feature = ExtractModificationFeature.ExtractASTDifferenceFeature(file1, file2);
		feature.get(1).printChangeAction();
		//feature.get(0).printChangeAction();
	}
}
