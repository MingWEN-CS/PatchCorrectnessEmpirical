package org.capgen.entity;

import com.github.gumtreediff.tree.ITree;
import org.capgen.util.ASTNodeType;
import org.capgen.util.Pair;
import org.eclipse.jdt.core.dom.AST;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ModificationFeature {
	
	
	/*
	 * This is the class for modification features
	 * 
	 * In total, there are 84 types of node (see ASTNodeType), and 4 types of operation INSERT (0) DELETE (1) MOVE (2) UPDATE (3)
	 *
	 * Therefore, we created 84 * 4 = 336 features
	 * 
	 * The ID of the feature is NODE ID + (OPERATION ID) * 84
	 * 
	 * */

	public int[] features;
	public List<String> ingredients;
	public List<String> variableIngredients;
	// Feature Size
	public static final int nodeNum = 84;
	public static final int FZ = nodeNum * 4;

	public List<ITree> nodes;
	public List<ITree> mappingNodes;
	public List<Integer> ops;

	public String buggyFileContent;
	public String fixedFileContent;
	
	public ModificationFeature() {
		features = new int[FZ];
		for (int i = 0; i < FZ; i++)
			features[i] = 0;
		nodes = new ArrayList<ITree>();
		ops = new ArrayList<Integer>();
		mappingNodes = new ArrayList<ITree>();
		ingredients = new ArrayList<>();
		variableIngredients = new ArrayList<>();
	}
	
	public ModificationFeature(String content) {
		features = new int[FZ];
		String[] splits = content.split("\t");
		for (int i = 0; i < splits.length; i++) {
			features[i] = Integer.parseInt(splits[i]);
		}
		nodes = new ArrayList<ITree>();
		ops = new ArrayList<Integer>();
		mappingNodes = new ArrayList<ITree>();
	}

	public boolean containsIngredient(String ingredient) {
		for (String string : ingredients)
			if (string.contains(ingredient))
				return true;
		return false;
	}

	public HashSet<Integer> getContainedOps(String ingredient) {
		HashSet<Integer> ops = new HashSet<>();
		for (int i = 0; i < ingredients.size(); i++) {
			if (ingredients.get(i).contains(ingredient))
				ops.add(this.ops.get(i));
		}
		return ops;
	}

	public void setBuggyFile(String content) {
		this.buggyFileContent = content;
	}

	public void setFixedFile(String content) {
		this.fixedFileContent = content;
	}

	public void addFeatureWithNode(int nodeId, int opId, ITree node, ITree mnode) {

		features[(nodeId - 1) + opId * nodeNum]++;
		nodes.add(node);
		ops.add(opId);
		mappingNodes.add(mnode);
	}

	public void addFeature(int nodeId, int opId) {
//		System.out.println(nodeId + "\t" + opId);
//		System.out.println((nodeId - 1) + opId * nodeNum);
		features[(nodeId - 1) + opId * nodeNum]++;
	}

	public void addFeature(int nodeId, int opId, String ingredient) {
		features[(nodeId - 1) + opId * nodeNum]++;
		ingredients.add(ingredient);
		ops.add(opId);

		if (nodeId == ASTNodeType.SIMPLE_NAME) {
			System.out.println(nodeId + "\t" + ASTNodeType.SIMPLE_NAME);
			variableIngredients.add(ingredient);
		}
	}

	@Override
	public String toString() {
		String f = "" + features[0];
		for (int i = 1; i < FZ; i++)
			f += "\t" + features[i];
		
		return f;
	}

	public String variableToString() {
	//	System.out.println(variableIngredients.toString());
		String content = "";
		for (String variable : variableIngredients)
			content += variable + "\t";
		return content;
	}

	public void addFeatures(ModificationFeature mf) {
		for (int i = 0; i < FZ; i++)
			features[i] += mf.features[i];
		for (int i = 0; i < mf.ingredients.size(); i++) {
			ingredients.add(mf.ingredients.get(i));
			ops.add(mf.ops.get(i));
		}
		for (int i = 0; i < mf.variableIngredients.size(); i++) {
			variableIngredients.add(mf.variableIngredients.get(i));
		}
	}
	
	public int getNumberOfActions() {
		int sum = 0;
		for (int feature : features) {
			sum += feature;
		}
		return sum;
	}
	public static Pair<Integer,Integer> getFeatureId(int id) {
		return new Pair<Integer, Integer>(id % nodeNum + 1, id / nodeNum);
	}

	public static int getAntiIndex(int index) {
		int nodeId = index % nodeNum;
		int opeId = index / nodeNum;
		int antiOpeId = 0;
		switch (opeId) {
			case 0: antiOpeId = 1; break;
			case 1: antiOpeId = 0; break;
			case 2: antiOpeId = 2; break;
			case 3: antiOpeId = 3; break;
		}
		return antiOpeId * nodeNum + nodeId;
	}

	public double getAntiCoveredActions(ModificationFeature feature) {
		int total = 0;
		int coveredTotal = 0;
		
		for (int i = 0; i < features.length; i++) {
			int antiIndex = getAntiIndex(i);
			if (features[i] > 0) {
				total += features[i];
				coveredTotal += Math.min(features[i], feature.features[antiIndex] + feature.features[i]);
			}
		}
		if (total == 0) return 1;
		return coveredTotal * 1.0 / total;
	}


	public double getCoveredActions(ModificationFeature feature) {
		int total = 0;
		int coveredTotal = 0;
		for (int i = 0; i < features.length; i++) {
			if (features[i] > 0) {
				total += features[i];
				coveredTotal += Math.min(features[i], feature.features[i]);
			}
		}
		if (total == 0) return 1;
		else return  coveredTotal * 1.0 / total;
	}
	
	public void printChangeAction() {
		for (int i = 0; i < features.length; i++) {
			if (features[i] > 0) {
				System.out.println(getFeatureName(i) + "\t" + features[i]);
			}
		}
	}


	public static String getFeatureName(int id) {
		int nodeId = id % nodeNum;
		int opeId = id / nodeNum;
//		System.out.println(nodeId + "\t" + opeId);
		String operation = "";
		nodeId++;
		switch (opeId) {
			case 0: operation = "INSERT"; break;
			case 1: operation = "DELETE"; break;
			case 2: operation = "MOVE"; break;
			case 3: operation = "UPDATE"; break;
		}
		String modification = "EMPTY";
		if (ASTNodeType.contains(nodeId))
			modification = ASTNodeType.get(nodeId);
		return new Pair<String,String>(operation,modification).toString();
	}
	
	public static void main(String[] args) {
		System.out.println(getFeatureName(101));
		System.out.println(getFeatureName(getAntiIndex(101)));
	}
}
