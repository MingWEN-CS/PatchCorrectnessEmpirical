package org.capgen.util;

import java.io.File;
import java.util.Collection;

public class UnderstandJavaSourceFiles {

    public static void createAnalysisDB(Collection<String> sourceFiles,
                                        String analysisDBPath) throws Exception {
        String formattedProjectDBPath = (new File(analysisDBPath)).getAbsolutePath();
//		PrintStream out = System.out;
//		ByteArrayOutputStream memoryFile = new ByteArrayOutputStream();
//		System.setOut(new PrintStream(memoryFile));
        System.out.println("begin to analyze by understand tool");

        // create project
        StringBuffer commandBuffer = new StringBuffer("und create -db ");
        commandBuffer.append(formattedProjectDBPath + " ");
        commandBuffer.append("-languages java ");
        ExecCommand command = new ExecCommand();
        System.out.println(commandBuffer.toString());
        command.execOneThread(commandBuffer.toString(),".");

        // add source files
        for(String sourceFile:sourceFiles){
            String formattedSourceFileName = (new File(sourceFile))
                    .getAbsolutePath();
            commandBuffer = new StringBuffer("und -db ");
            commandBuffer.append("" + formattedProjectDBPath + " ");
            commandBuffer.append("add ");
            commandBuffer.append("" + formattedSourceFileName + " ");
            System.out.println(commandBuffer);
            command.execOneThread(commandBuffer.toString(),".");
        }
        // analyze the project
        commandBuffer = new StringBuffer("und -db ");
        commandBuffer.append("" + formattedProjectDBPath + " ");
        commandBuffer.append("analyze");
        command.execOneThread(commandBuffer.toString(),".");
//		System.setOut(out);
    }
}
