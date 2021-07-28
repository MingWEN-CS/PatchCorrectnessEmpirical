package org.capgen.entity;

import java.util.ArrayList;
import java.util.List;

public class CFG {	
	CFGNode entry = null;
	CFGNode exit = null;		
	List<CFGNode> allNodes = new ArrayList<CFGNode>();
	
	public CFG(String cfgString){
		if(cfgString.equals("null")){
			return;
		}
		String[] splits = cfgString.split(";",-1);
		
		for(int i=1; i<=splits.length; i++){
			CFGNode node = new CFGNode();
			node.nodeID = i;
			String nodeStr = splits[i-1];
			String[] nodeSplits = nodeStr.split(",",-1);			
			if(!nodeSplits[1].equals("")){
				node.lineStart = Integer.parseInt(nodeSplits[1]);
			}
			if(!nodeSplits[3].equals("")){
				node.lineEnd = Integer.parseInt(nodeSplits[3]);
			}			
			allNodes.add(node);			
		}
		for(int i=1; i<=splits.length; i++){
			String nodeStr = splits[i-1];
			String[] nodeSplits = nodeStr.split(",",-1);
			CFGNode currentNode = allNodes.get(i-1);			
			if(nodeSplits.length>6){
				for(int j=6; j<nodeSplits.length; j++){
					if(!nodeSplits[j].equals("")){
						String IDstr=nodeSplits[j];
						if(IDstr.contains(":")){
							IDstr = IDstr.substring(0, IDstr.indexOf(":"));
						}
						if(!IDstr.equals("")){
							int successorID = Integer.parseInt(IDstr);
							CFGNode successorNode = allNodes.get(successorID-1);
							currentNode.successors.add(successorNode);
							successorNode.predecessors.add(currentNode);
						}
					}
				}
			}
		}
		
		entry = allNodes.get(0);
		exit = allNodes.get(allNodes.size()-1);
		
	}	
	public List<CFGNode> getNodes(int lineNum){
		List<CFGNode> relevantNodes = new ArrayList<CFGNode>();
		for(CFGNode node:allNodes){
			if(node.getLineStart() <= lineNum && node.getLineEnd() >= lineNum){
				relevantNodes.add(node);
			}
		}	
		return relevantNodes;
	}
	public CFGNode getEntry(){
		return  entry;
	}
	public CFGNode getExit(){
		return exit;
	}
	public List<CFGNode> getAllNodes() {
		return allNodes;
	}	
	
}

