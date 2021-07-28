package org.capgen.main;

import org.capgen.util.CiviProcessor;
import org.capgen.util.GetClassNames;
import spoon.Launcher;
import org.capgen.util.*;

import java.io.File;
import java.util.*;

public class ExtractIngredientsContext {
	
	public String project;
	public String projectLoc;
	public int randomSize = 10;
	

	
	public void spoonOnFile(CiviProcessor processor, String file, String output) {
	    try {
            Launcher spoon = new Launcher();
//			System.out.println(file);
            spoon.addInputResource(file);
            spoon.setSourceOutputDirectory("./spooned" + File.separator + output);
            spoon.getEnvironment().setNoClasspath(true);
//          spoon.getEnvironment().setCommentEnabled(true);
            spoon.addProcessor(processor);
	        spoon.run();
        }   catch (Exception e) {
            System.err.println("warning\t" + e);   
        }   catch(StackOverflowError e){
            System.err.println("ouch!");
        }
	}
	public HashMap<String, List<String>> obtainIngredientsForSource(String sourceFile, String saveLoc) {
		System.out.println(sourceFile);
		HashSet<String> classNames = GetClassNames.getClassNames(sourceFile, "output");
		HashMap<String, List<String>> ingredients = new HashMap<String, List<String>>();
		File file = new File(sourceFile);
		FixIngredientsProcessor processor = new FixIngredientsProcessor(FileToLines.fileToString(sourceFile), classNames);
		spoonOnFile(processor, sourceFile, saveLoc + File.separator + "output");
		List<String> saveLines = new ArrayList<String>();
//	    System.out.println(processor.ingredients.size());	
		System.out.println(processor.ingredients.size() + "\t" + processor.ingredientsContext.size());
        for (int i = 0; i < processor.ingredients.size(); i++) {
			ingredients.put(processor.ingredients.get(i), processor.ingredientsContext.get(i));
            saveLines.add(processor.ingredientsType.get(i) + "\t" + processor.ingredientsCategory.get(i) + "\t" + processor.ingredientsClassName.get(i) 
				+ "\t" + processor.ingredientsPosition.get(i).toString() + "\t" + processor.ingredientsLine.get(i).toString() + "\t"
				+ processor.ingredients.get(i) + "\t" + processor.ingredientsMethod.get(i).toString() + "\t" 
				+ processor.ingredientsVariable.get(i).toString() + "\t" + processor.ingredientsContext.get(i).toString() + "\t" + processor.ingredientsSiblings.get(i).toString());
	}
		String saveFile = "";
		saveFile = saveLoc + File.separator + file.getName() + ".ingredients";
		WriteLinesToFile.writeLinesToFile(saveLines, saveFile);
		return ingredients;
	}

}
