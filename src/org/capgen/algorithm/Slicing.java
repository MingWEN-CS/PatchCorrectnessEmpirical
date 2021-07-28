package org.capgen.algorithm;

import org.capgen.entity.CFG;
import org.capgen.entity.CFGNode;
import org.capgen.entity.VariableUsage;

import java.util.Collection;
import java.util.HashSet;

public abstract class Slicing {
	
	public CFG cfg;
	public Collection<VariableUsage> variableUsages;
	
	public Slicing(CFG cfg, Collection<VariableUsage> variableUsages) {
		this.cfg = cfg;
		this.variableUsages = variableUsages;
	}
	
	protected HashSet<Integer> getUseNodes(int lineStart, int lineEnd, Collection<VariableUsage> variableUsages){
		if(lineEnd<lineStart){
			lineEnd = lineStart;
		}
		HashSet<Integer> nodeIds = new HashSet<Integer>();
		for(int lineNum = lineStart; lineNum <= lineEnd; lineNum++){
			for(VariableUsage vu:variableUsages){
				if(vu.isUseLine(lineNum)){
					nodeIds.add(vu.getNodeID());
				}
			}
		}
		return nodeIds;
	}
	
	protected HashSet<Integer> getDefOrSetNodes(int lineStart, int lineEnd, Collection<VariableUsage> variableUsages){
		if(lineEnd<lineStart){
			lineEnd = lineStart;
		}
		HashSet<Integer> nodeIds = new HashSet<Integer>();
		for (int lineNum = lineStart; lineNum <= lineEnd; lineNum++) {
			for (VariableUsage vu : variableUsages) {
				if(vu.isDefineLine(lineNum) || vu.isSetLine(lineNum)){
					nodeIds.add(vu.getNodeID());
				}
			}
		}
		return nodeIds;
	}
	
	public abstract HashSet<CFGNode> getSlicedNode(int line);
	
	
}
