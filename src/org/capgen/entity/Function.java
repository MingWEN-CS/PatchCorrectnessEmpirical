package org.capgen.entity;

import java.util.HashSet;

public class Function {
	private int funID= -1;
	private String name = "";
	private boolean isAbstract = false;
	private int startLine = -1;
	private int endLine = -1;
	private String sourceFileName = "";
	private String cfgString = "";
	public String getCfgString() {
		return cfgString;
	}
	public void setCfgString(String cfgString) {
		this.cfgString = cfgString;
	}
	private CFG cfg = null;
	public CFG getCFG(){
		if(cfg==null && cfgString!=null && !cfgString.equals("")){
			cfg = new CFG(cfgString);
		}
		return cfg;
	}
	
	public HashSet<Integer> getLines(){
		HashSet<Integer> lines = new HashSet<Integer>();		
		for(int i=startLine; i<endLine; i++){
			lines.add(i);
		}
		if(lines.size()==0){
			lines.add(startLine);
		}
		return lines;
	}
	
	public boolean withinRange(int line) {
		if (startLine <= line && line <= endLine) return true;
		else return false;
	}
	
	
	
	public String getName() {
		return name;
	}
	public int getFunID() {
		return funID;
	}
	public void setFunID(int funID) {
		this.funID = funID;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isAbstract() {
		return isAbstract;
	}
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	public int getStartLine() {
		return startLine;
	}
	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}
	public int getEndLine() {
		return endLine;
	}
	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	public String getSourceFileName() {
		return sourceFileName;
	}
	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}
	
}
