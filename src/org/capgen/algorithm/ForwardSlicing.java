package org.capgen.algorithm;

import org.capgen.entity.CFG;
import org.capgen.entity.CFGNode;
import org.capgen.entity.VariableUsage;

import java.util.*;

public class ForwardSlicing extends Slicing{

	public ForwardSlicing(CFG cfg, Collection<VariableUsage> variableUsages) {
		super(cfg, variableUsages);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HashSet<CFGNode> getSlicedNode(int line) {
		// TODO Auto-generated method stub
		HashSet<CFGNode> sliceNodes = new HashSet<CFGNode>();
		
		List<CFGNode> startingRelevantNodes = cfg.getNodes(line);
//		System.out.println(line + "\t" + startingRelevantNodes.toString());
		HashMap<CFGNode, HashSet<Integer>> nodeToAliveRelevantVariableSet = new HashMap<CFGNode, HashSet<Integer>>();		
	
		HashSet<CFGNode> visitiedNodes = new HashSet<CFGNode>();
		HashSet<CFGNode> visitingNodes = new HashSet<CFGNode>();
		HashSet<CFGNode> toVisitNodes = new HashSet<CFGNode>();
		toVisitNodes.addAll(startingRelevantNodes);
		
		boolean isUpdated = false;
		int numIteration = 0;
	
		while (toVisitNodes.size() != 0) {
			numIteration++;
			visitingNodes.addAll(toVisitNodes);
			toVisitNodes.clear();
			HashSet<CFGNode> potentialNodes = new HashSet<CFGNode>();
//			System.out.println("Iteration:" + numIteration + "\t" + visitingNodes.toString());
			for (CFGNode node : visitingNodes) {
//				System.out.println("==== visiting node :\t" + node.toString());
				visitiedNodes.add(node);
				HashSet<Integer> useVariableNodes = getUseNodes(node.getLineStart(), node.getLineEnd(), variableUsages);
				HashSet<Integer> defineOrSetVariableNodes = getDefOrSetNodes(node.getLineStart(), node.getLineEnd(), variableUsages);
				
				useVariableNodes.removeAll(defineOrSetVariableNodes);
				HashSet<Integer> relevantAliveVariablesFromPredecessors = new HashSet<Integer>();
				for(CFGNode successor : node.getPredecessors()){
					if(nodeToAliveRelevantVariableSet.get(successor)!=null){
						relevantAliveVariablesFromPredecessors.addAll(nodeToAliveRelevantVariableSet.get(successor));
					}
				}
				
				if (line == 1133) {
					System.out.println("Print:\t" + numIteration + "\t" + useVariableNodes + "\t" + defineOrSetVariableNodes);
					System.out.println(relevantAliveVariablesFromPredecessors.toString());
				}
				
//				System.out.println("Define\t" + defineOrSetVariableNodes);
//				System.out.println("Use\t" + useVariableNodes);
				// Deal the case of no alive variables

				if(relevantAliveVariablesFromPredecessors.size()==0){	
					if(startingRelevantNodes.contains(node)){
						
						if(nodeToAliveRelevantVariableSet.get(node)==null){
							nodeToAliveRelevantVariableSet.put(node, defineOrSetVariableNodes);
							if (node.getSuccessors().size() > 0)
								nodeToAliveRelevantVariableSet.get(node).addAll(useVariableNodes);
							isUpdated = true;
							sliceNodes.add(node);
							potentialNodes.addAll(node.getSuccessors());
						}else if(!nodeToAliveRelevantVariableSet.get(node).containsAll(defineOrSetVariableNodes) && defineOrSetVariableNodes.size()>0){
							nodeToAliveRelevantVariableSet.get(node).addAll(defineOrSetVariableNodes);
							if (node.getSuccessors().size() > 0)
								nodeToAliveRelevantVariableSet.get(node).addAll(useVariableNodes);
							isUpdated = true;
							sliceNodes.add(node);
							potentialNodes.addAll(node.getSuccessors());
						}
					}
				} else {
					// propagation from predecessors to the current node 
					
					// IF any DEForSET(predecessors) used in the current node
		
					if (useVariableNodes.size() != 0 && !Collections.disjoint(useVariableNodes, relevantAliveVariablesFromPredecessors)) {
						HashSet<Integer> refNodeSet = new HashSet<Integer>();
						refNodeSet.addAll(defineOrSetVariableNodes);
						refNodeSet.addAll(relevantAliveVariablesFromPredecessors);
						if (nodeToAliveRelevantVariableSet.get(node) == null) {
							nodeToAliveRelevantVariableSet.put(node, refNodeSet);
							if (node.getSuccessors().size() > 0)
								nodeToAliveRelevantVariableSet.get(node).addAll(useVariableNodes);
							isUpdated = true;
						} else {
							if (!nodeToAliveRelevantVariableSet.get(node).containsAll(refNodeSet)) {
								nodeToAliveRelevantVariableSet.get(node).addAll(refNodeSet);
								if (node.getSuccessors().size() > 0)
									nodeToAliveRelevantVariableSet.get(node).addAll(useVariableNodes);
								isUpdated = true;
							}
						}
						// Also need to add to the slice since it has been influenced by starting node 
						sliceNodes.add(node);
					}
					
					// IF none of the DEF(predecessors) used in the current node, just propagate
					else {
						if (defineOrSetVariableNodes.size() != 0 && !Collections.disjoint(defineOrSetVariableNodes, relevantAliveVariablesFromPredecessors)) {
							relevantAliveVariablesFromPredecessors.removeAll(defineOrSetVariableNodes);
						}
						if (useVariableNodes.size() == 0 || Collections.disjoint(useVariableNodes, relevantAliveVariablesFromPredecessors)) {
							if (nodeToAliveRelevantVariableSet.get(node) == null) {
								nodeToAliveRelevantVariableSet.put(node, new HashSet<Integer>());
								nodeToAliveRelevantVariableSet.get(node).addAll(relevantAliveVariablesFromPredecessors);
								isUpdated = true;
							}
							else if (!nodeToAliveRelevantVariableSet.get(node).containsAll(relevantAliveVariablesFromPredecessors)) {
								nodeToAliveRelevantVariableSet.get(node).addAll(relevantAliveVariablesFromPredecessors);
								isUpdated = true;
							}
						}
					}
							
					potentialNodes.addAll(node.getSuccessors());
				}
				for(CFGNode potentialNode : potentialNodes){
					if(!visitiedNodes.contains(potentialNode)){
						toVisitNodes.add(potentialNode);
					} else {
						if(isUpdated){
							toVisitNodes.add(potentialNode);
						}
					}
				}
				isUpdated = false;
			}
			
			visitingNodes.clear();
		}
//		System.out.println("conduct forward slicing via " + numIteration + " iterations.");
//		for (CFGNode node : sliceNodes) {
//			System.out.println(node.toString());
//		}
		return sliceNodes;
	}
	
}
