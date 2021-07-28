package org.capgen.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class IngredientInfo {
	
	public int startLine = 0;
	public int endLine = 0;
	public int startSource = 0;
	public int endSource = 0;
	public String type;
	public String category;
	public String content;
	public String mutatedContent;
	public String className;
	public String sourceFileName;
	public HashSet<Pair<String, String>> requiredMethods;
	public HashSet<Pair<String, String>> requiredVariables;
	public List<String> contexts;
	public List<String> siblings;
	public List<String> semantic;
	public boolean isExpressionStatement = false;
	
	public void addSemantics(List<String> semantics) {
		this.semantic = new ArrayList<>();
		for (String semantic : semantics) {
			if (semantic.endsWith("_INVOCATION"))
				this.semantic.add("INVOCATION");
			else this.semantic.add(semantic);
		}
	}
	
	public IngredientInfo(String content) {
		
		String[] split = content.split("\t");
		type = split[0];
		category = split[1];
		className = split[2];
		if (className.contains(" "))
			className = className.substring(className.indexOf(" ") + 1);
		if (split.length < 4)
            System.out.println(content); 
        if (!split[3].contains(":"))
            System.out.println(content); 

        startSource = Integer.parseInt(split[3].substring(0, split[3].indexOf(":")));
		endSource = Integer.parseInt(split[3].substring(split[3].indexOf(":") + 1, split[3].length()));
		
		startLine = Integer.parseInt(split[4].substring(0, split[4].indexOf(":")));
		endLine = Integer.parseInt(split[4].substring(split[4].indexOf(":") + 1, split[4].length()));
		
		this.content = split[5];
		
        HashSet<String> methods = DataTransfer.turnStringToHashSetStringWithType(split[split.length - 4]);
		HashSet<String> variables = DataTransfer.turnStringToHashSetStringWithType(split[split.length - 3]);
		
		requiredMethods = new HashSet<Pair<String,String>>();
		requiredVariables = new HashSet<Pair<String,String>>();
		for (String tmp : methods) {
			String[] split2 = tmp.split(":");
			if (split2.length < 2)
                System.out.println(tmp);
            requiredMethods.add(new Pair<String,String>(split2[0].trim(), split2[1].trim()));
		}
		for (String tmp : variables) {
			String[] split2 = tmp.split(":");
			if (split2.length < 2)
                System.out.println(tmp);
            requiredVariables.add(new Pair<String,String>(split2[0].trim(), split2[1].trim()));
		}
		contexts = DataTransfer.turnStringToListString(split[split.length - 2]);
		siblings = DataTransfer.turnStringToListString(split[split.length - 1]);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof IngredientInfo) {
			IngredientInfo ii = (IngredientInfo) obj;
			if (this.startLine == ii.startLine && this.endLine == ii.endLine && 
					this.content.equals(ii.content) && this.type.equals(ii.type))
				return true;
			else return false;
		} else return false;
	}
	
	@Override
	public String toString() {
		return startLine + "\t" + content;
	}
	
}
