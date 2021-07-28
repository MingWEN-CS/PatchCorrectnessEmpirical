package org.capgen.util;

import com.scitools.understand.Database;
import com.scitools.understand.Entity;
import com.scitools.understand.Reference;
import com.scitools.understand.Understand;
import org.capgen.entity.Function;
import org.capgen.entity.VariableUsage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class AnalyzeUnderstand {
	
	public static HashMap<Integer, HashSet<Integer>> reloadASTMapping(String filename) {
		HashMap<Integer, HashSet<Integer>> mapping = new HashMap<Integer, HashSet<Integer>>();
		List<String> lines = FileToLines.fileToLines(filename);
		for (String line : lines) {
			String[] split = line.split("\t");
			int startLine = Integer.parseInt(split[0]);
			int type = Integer.parseInt(split[2]);
			if (!mapping.containsKey(startLine))
				mapping.put(startLine, new HashSet<Integer>());
			mapping.get(startLine).add(type);
		}
		return mapping;
	}
	
	public static HashMap<Integer, HashMap<Integer, VariableUsage>> reloadVariableUsage(String variableUsageFile, HashMap<String, Integer> functions){
		List<String> lines = FileToLines.fileToLines(variableUsageFile,1);
		HashMap<Integer, HashMap<Integer, VariableUsage>> functionVariableUsage = new HashMap<Integer, HashMap<Integer, VariableUsage>>();
		
		for(String line:lines){
			String[] splits = line.split("\t");
			int nodeID = Integer.parseInt(splits[0]);
			String methodHash = splits[6] + "\t" + splits[7] + "\t" + splits[8] + "\t" + splits[9];
			int methodID = functions.get(methodHash);
		
			if(functionVariableUsage.get(methodID)==null){
				functionVariableUsage.put(methodID, new HashMap<Integer, VariableUsage>());
			}
			if(functionVariableUsage.get(methodID).get(nodeID)==null){
				functionVariableUsage.get(methodID).put(nodeID, new VariableUsage(splits[1]));
			}
			VariableUsage vu = functionVariableUsage.get(methodID).get(nodeID);
			vu.setNodeID(nodeID);
			vu.setMethodID(methodID);	
			String variableType = splits[3];
			vu.setVariableType(variableType);
			
			int refLineNum = Integer.parseInt(splits[4]);
			String usageType = splits[5];
			if(usageType.equals(VariableUsage.DEFINE)){
				vu.getDefLines().add(refLineNum);
			}else if(usageType.equals(VariableUsage.SET)){
				vu.getSetLines().add(refLineNum);
			}else if(usageType.equals(VariableUsage.USE)){
				vu.getUseLines().add(refLineNum);
			}else{
				System.err.println("unknown usage type for the variable info:"+ line);
			}
		}
		return functionVariableUsage;
	}
	
	public static HashMap<Integer,Function> reloadFunctionInfo(String CFGFile){
		HashMap<Integer,Function> functionMap = new HashMap<Integer,Function>();
		List<String> lines = FileToLines.fileToLines(CFGFile,1);
		for(String line:lines){
			String[] splits = line.split("\t",-1);
			Function function = new Function();
			
			function.setFunID(Integer.parseInt(splits[0]));
			function.setName(splits[1]);
			function.setSourceFileName(splits[3]);
			function.setStartLine(Integer.parseInt(splits[4]));
			function.setEndLine(Integer.parseInt(splits[5]));
			String kind = splits[6];			
			if(kind.contains("Abstract")){
				function.setAbstract(true);
			}else{
				function.setAbstract(false);
			}	
			String cfgString = splits[7];
			function.setCfgString(cfgString);
			functionMap.put(function.getFunID(), function);
		}
		return functionMap;
	}
	
	
	public static void extractCFG(String filename, String saveFile) throws Exception {
		if (new File(saveFile).exists()) return;
		Database database = Understand.open(filename);
		Entity[] entities = database.ents("Java Method Constructor Member, Java Method Member");
		int num = 0;
		int IOFlushNum = 10000;
		List<String> lines = new ArrayList<String>();
		//header line 
		lines.add("method ID \t method name \t params \t source file \t start line \t end line \t method kind \t CFG"); 
		
		for(Entity entity:entities){
			if(entity.library()!=null && !entity.library().equals("")){
				continue;
			}
			
			Reference[] refs = entity.refs("Definein", "", false);
			if(refs.length==0){
				continue;
			}

			String file = EntityUtil.getFile(entity);
			int startLine = EntityUtil.getLineStart(entity);
			int endLine = EntityUtil.getLineEnd(entity);
			
			lines.add(entity.id()+"\t"+entity.longname(true)+"\t" + entity.parameters() + "\t" 
				+ file + "\t" + startLine + "\t" + endLine + "\t" 
				+ entity.kind().name() + "\t" + entity.freetext("CGraph"));
			if(lines.size()>=IOFlushNum){
				WriteLinesToFile.writeLinesToFile(lines, saveFile);
				lines.clear();
			}
			num++;
		}
		if(!lines.isEmpty()){
			WriteLinesToFile.writeLinesToFile(lines, saveFile);
		}
		System.out.println(num);
		database.close();
	}
	
	public static void extractVariableUse(String filename, String saveFile) throws Exception {
		
		if (new File(saveFile).exists()) return;
		Database database = Understand.open(filename);
		Entity[] entities = database.ents("Java Method Constructor Member, Java Method Member");
		System.out.println("There are " + entities.length + " entites!");
		
		List<String> lines = new ArrayList<String>();
		//header
		lines.add("variable ID \t variable name \t variable type \t reference kind (param or variable) \t reference line \t define or set or use? \t method name \t source file \t start line \t end line");
//		lines.add("variable ID \t variable name \t variable type \t reference kind (param or variable) \t reference line \t define or set or use? \t method ID \t method name \t source file \t start line \t end line");
		int IOFlushNum = 10000;
		
		for(Entity entity:entities){
			if(entity.library()!=null && !entity.library().equals("")){
				continue;
			}
			
			Reference[] refs = entity.refs("Definein", "", false);
			if(refs.length==0){
				continue;
			}

			String file = EntityUtil.getFile(entity);
			int startLine = EntityUtil.getLineStart(entity);
			int endLine = EntityUtil.getLineEnd(entity);
			String methodInfo = entity.longname(true)+"\t" + file + "\t" + startLine + "\t" + endLine;
//			String methodInfo = entity.id()+"\t"+entity.longname(true)+"\t" + file + "\t" + startLine + "\t" + endLine;
			
			Reference[] parameterRefs = entity.refs("Define", "Parameter", false);
			for(Reference paramRef:parameterRefs){
				String variableName = paramRef.ent().name();
				String variableType = paramRef.ent().type();
				int refLine = paramRef.line();
				lines.add(paramRef.ent().id() + "\t" + variableName + "\t" + variableType + "\t"+ "param" + "\t" + refLine + "\t"+ "define" + "\t" + methodInfo);
			}
			
			parameterRefs = entity.refs("Set", "Parameter", false); 
			for(Reference paramRef:parameterRefs){
				String variableName = paramRef.ent().name();
				String variableType = paramRef.ent().type();
				int refLine = paramRef.line();
				lines.add(paramRef.ent().id() + "\t" + variableName + "\t" + variableType + "\t"+ "param" + "\t" + refLine + "\t"+ "set" + "\t" + methodInfo);
			}
			
			parameterRefs = entity.refs("Use", "Parameter", false); 
			for(Reference paramRef:parameterRefs){
				String variableName = paramRef.ent().name();
				String variableType = paramRef.ent().type();
				int refLine = paramRef.line();
				lines.add(paramRef.ent().id() + "\t" + variableName + "\t" + variableType + "\t"+ "param" + "\t" + refLine + "\t"+ "use" + "\t" + methodInfo);
			}
			
			Reference[] variableRefs =  entity.refs("Define", "Variable", false);
			for(Reference varRef:variableRefs){
				String variableName = varRef.ent().name();
				String variableType = varRef.ent().type();
				int refLine = varRef.line();
				lines.add(varRef.ent().id() + "\t" + variableName + "\t" + variableType + "\t"+ "variable" + "\t" + refLine + "\t"+ "define" + "\t" + methodInfo);
			}
			
			variableRefs =  entity.refs("Set", "Variable", false);
			for(Reference varRef:variableRefs){
				String variableName = varRef.ent().name();
				String variableType = varRef.ent().type();
				int refLine = varRef.line();
				lines.add(varRef.ent().id() + "\t" + variableName + "\t" + variableType + "\t"+ "variable" + "\t" + refLine + "\t"+ "set" + "\t" + methodInfo);
			}
			
			variableRefs =  entity.refs("Use", "Variable", false);
			for(Reference varRef:variableRefs){
				String variableName = varRef.ent().name();
				String variableType = varRef.ent().type();
				int refLine = varRef.line();
				lines.add(varRef.ent().id() + "\t" + variableName + "\t" + variableType + "\t"+ "variable" + "\t" + refLine + "\t"+ "use" + "\t" + methodInfo);
			}
			
			if(lines.size()>=IOFlushNum){
				WriteLinesToFile.appendLinesToFile(lines, saveFile);
				lines.clear();
			}			
		}
		if(!lines.isEmpty()){
			WriteLinesToFile.appendLinesToFile(lines, saveFile);
		}
		database.close();
	}
	
	public static void main(String[] args) throws Exception {
		
		String filename = "../Datasets_ChangePredict/Lucene/fileIndex/bbefd26.udb";
		String useFile = "../Datasets_ChangePredict/Lucene/fileIndex/bbefd26.use";
		String cfgFile = "../Datasets_ChangePredict/Lucene/fileIndex/bbefd26.cfg";
		String astFile = "../Datasets_ChangePredict/Lucene/fileIndex/bbefd26.ast";
		extractVariableUse(filename, "");
	}
}
