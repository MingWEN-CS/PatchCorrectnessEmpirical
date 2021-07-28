package org.capgen.entity;

import java.util.HashSet;

public class VariableUsage {
	
	// the node id represents the id of the variable
	private int nodeID = -1;
	private String variableType = "";
	private int methodID = -1;	
	public String name;
	
	private HashSet<Integer> defineLines = new HashSet<Integer>();
	private HashSet<Integer> setLines = new HashSet<Integer>();
	private HashSet<Integer> useLines = new HashSet<Integer>();
	
	public int getNodeID() {
		return nodeID;
	}
	
	public VariableUsage(String name) {
		this.name = name;
	}
	
	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}
	
	public HashSet<Integer> getDefLines() {
		return defineLines;
	}
	
	public String getVariableType() {
		if (variableType.equals(PARAM))
			return PARAM;
		else if (defineLines.size() == 0) {
			return GLOBAL;
		} else return VARIABLE;
	}
	public void setVariableType(String variableType) {
		this.variableType = variableType;
	}

	public HashSet<Integer> getSetLines() {
		return setLines;
	}
	public HashSet<Integer> getUseLines() {
		return useLines;
	}
	
	public boolean isGlobalForThisFunction(){
		if(defineLines.size()==0 || variableType.equals(PARAM)){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isGlobal() {
		if(defineLines.size()==0 && !isParam())
			return true;
		else return false;
	}
	
	public boolean isParam() {
		return variableType.equals(PARAM);
	}
	
	public boolean isDefineLine(int lineNum){
		if(defineLines.contains(lineNum)){
			return true;
		}else{
			return false;
		}
	}
	public boolean isSetLine(int lineNum){
		if(setLines.contains(lineNum)){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isUseLine(int lineNum){
		if(useLines.contains(lineNum)){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isRelevantLine(int lineNum){
		if(isDefineLine(lineNum) || isUseLine(lineNum) || isSetLine(lineNum)){
			return true;
		}else{
			return false;
		}
	}
	
	public static final String PARAM = "param";
	public static final String VARIABLE = "variable";
	public static final String GLOBAL = "global";
	
	public static final String DEFINE = "define";
	public static final String SET = "set";
	public static final String USE = "use";

	public int getMethodID() {
		return methodID;
	}
	public void setMethodID(int methodID) {
		this.methodID = methodID;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + methodID;
		result = prime * result + nodeID;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariableUsage other = (VariableUsage) obj;
		if (methodID != other.methodID)
			return false;
		if (nodeID != other.nodeID)
			return false;
		return true;
	}
	
}
