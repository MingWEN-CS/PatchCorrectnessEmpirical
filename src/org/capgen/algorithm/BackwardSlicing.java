package org.capgen.algorithm;

import org.capgen.entity.CFG;
import org.capgen.entity.CFGNode;
import org.capgen.entity.VariableUsage;

import java.util.*;

public class BackwardSlicing extends Slicing {
	
	public BackwardSlicing(CFG cfg, Collection<VariableUsage> variableUsages) {
		super(cfg, variableUsages);
	}

	@Override
	public HashSet<CFGNode> getSlicedNode(int line) {
		
		List<CFGNode> startingRelevantNodes = cfg.getNodes(line);
//		System.out.println(line + "\t" + startingRelevantNodes.toString());
		HashMap<CFGNode, HashSet<Integer>>
		nodeToAliveRelevantVariableSet = new HashMap<CFGNode, HashSet<Integer>>();		
	
		HashSet<CFGNode> visitiedNodes = new HashSet<CFGNode>();
		HashSet<CFGNode> visitingNodes = new HashSet<CFGNode>();
		HashSet<CFGNode> toVisitNodes = new HashSet<CFGNode>();
		toVisitNodes.addAll(startingRelevantNodes);

		boolean isUpdated = false;
		int numIteration = 0;
		
		HashSet<CFGNode> sliceNodes = new HashSet<CFGNode>();
		
		while(toVisitNodes.size()!=0){
			numIteration++;
			visitingNodes.addAll(toVisitNodes);
			toVisitNodes.clear();
			
			HashSet<CFGNode> potentialNodes = new HashSet<CFGNode>();
//			System.out.println("Iteration:" + numIteration + "\t" + visitingNodes.toString());
			for(CFGNode node : visitingNodes){
//				System.out.println("==== visiting node :\t" + node.toString());
				visitiedNodes.add(node);
				HashSet<Integer> useVariableNodes = getUseNodes(node.getLineStart(), node.getLineEnd(), variableUsages);
				HashSet<Integer> defineOrSetVariableNodes = getDefOrSetNodes(node.getLineStart(), node.getLineEnd(), variableUsages);
			
				defineOrSetVariableNodes.removeAll(useVariableNodes);
				useVariableNodes.removeAll(defineOrSetVariableNodes);
				
//				System.out.println(useVariableNodes.toString());
//				System.out.println(defineOrSetVariableNodes.toString());
				
				HashSet<Integer> relevantAliveVariablesFromSuccessors = new HashSet<Integer>();
				for(CFGNode successor : node.getSuccessors()){
					if(nodeToAliveRelevantVariableSet.get(successor)!=null){
						relevantAliveVariablesFromSuccessors.addAll(nodeToAliveRelevantVariableSet.get(successor));
					}
				}
//				System.out.println(node + "\t relevant node size\t" + relevantAliveVariablesFromSuccessors.size());
				if(relevantAliveVariablesFromSuccessors.size()==0){
					
					if(startingRelevantNodes.contains(node)){
						if(nodeToAliveRelevantVariableSet.get(node)==null){
							nodeToAliveRelevantVariableSet.put(node, useVariableNodes);
							isUpdated = true;
							sliceNodes.add(node);
							potentialNodes.addAll(node.getPredecessors());
						}else if(!nodeToAliveRelevantVariableSet.get(node).containsAll(useVariableNodes) && useVariableNodes.size()>0){
							nodeToAliveRelevantVariableSet.get(node).addAll(useVariableNodes);
							isUpdated = true;
							sliceNodes.add(node);
							potentialNodes.addAll(node.getPredecessors());
						}
					}
				} else{
					
					// propagation from successors to current node
					
					// If the REF(successor) defined or set in the current node
					if (defineOrSetVariableNodes.size() != 0 && !Collections.disjoint(defineOrSetVariableNodes, relevantAliveVariablesFromSuccessors)) {
						
						
						HashSet<Integer> refNodeSet = new HashSet<Integer>();
						refNodeSet.addAll(useVariableNodes);
						refNodeSet.addAll(relevantAliveVariablesFromSuccessors);
						refNodeSet.removeAll(defineOrSetVariableNodes);
						if (nodeToAliveRelevantVariableSet.get(node) == null) {
							nodeToAliveRelevantVariableSet.put(node, refNodeSet);
							isUpdated = true;
						} else {
							if (!nodeToAliveRelevantVariableSet.get(node).containsAll(refNodeSet)) {
								nodeToAliveRelevantVariableSet.get(node).addAll(refNodeSet);
								isUpdated = true;
							}
						}
						
						// Also need to add to the slice since it has influence on variables 
						sliceNodes.add(node);
					}
					
					// IF none of the REF(successor) defined or set in the current node, just propagate
					if (defineOrSetVariableNodes.size() == 0 || Collections.disjoint(defineOrSetVariableNodes, relevantAliveVariablesFromSuccessors)) {
						if (nodeToAliveRelevantVariableSet.get(node) == null) {
							nodeToAliveRelevantVariableSet.put(node, new HashSet<Integer>());
							nodeToAliveRelevantVariableSet.get(node).addAll(relevantAliveVariablesFromSuccessors);
							isUpdated = true;
						}
						else if (!nodeToAliveRelevantVariableSet.get(node).containsAll(relevantAliveVariablesFromSuccessors)) {
							nodeToAliveRelevantVariableSet.get(node).addAll(relevantAliveVariablesFromSuccessors);
							isUpdated = true;
						}
							
					}
//					System.out.println("Add predecessors\t" + node.getPredecessors().toString());
					potentialNodes.addAll(node.getPredecessors());
				}
				// Add newly updated nodes to the next round
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
//		System.out.println("conduct backward slicing via " + numIteration + " iterations.");
//		for (CFGNode node : sliceNodes) {
//			System.out.println(node.toString());
//		}
		
		return sliceNodes;
	}
}
