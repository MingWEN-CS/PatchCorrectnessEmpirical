package org.capgen.util;


import java.io.File;

public class Commands {
	
	public static Pair<String,String> extractBuggyVersion(String project, int bid, String saveTo) {
		
		String[] commands = {
				"defects4j",
				"checkout",
				"-p",
				project,
				"-v",
				bid + "b", 
				"-w",
				saveTo
		};

		printCommands(commands);
		ExecCommand executor = new ExecCommand();
		Pair<String,String> result = executor.execOneThread(commands, saveTo);
		return result;
		
	}

	public static Pair<String, String> getDiffofTwoFiles(String file1, String file2) {
		String[] commands = {
				"diff",
				file1,
				file2
		};

		printCommands(commands);
		ExecCommand executor = new ExecCommand();
		Pair<String,String> result = executor.execOneThread(commands, ".");
		return result;
	}

	public static Pair<String,String> generateRandoopTestCases(
			String targetLibraryAndDependancy,
			String classList,
			int seed,
			int timeLimit,
			String outputDir,
			String workingPath) {
		
		String[] commands = {
				"java",
				"-cp",
				"./lib/randoop-all-3.0.8.jar" + File.pathSeparatorChar + targetLibraryAndDependancy,
				"randoop.main.Main",
				"gentests",
				"--classlist=" + classList,
				"--ignore-flaky-tests=true",
				"--junit-output-dir=" + outputDir,
				"--randomseed=" + seed,
				"--timelimit=" + timeLimit
		};
		
		printCommands(commands);
		ExecCommand executor = new ExecCommand();
		Pair<String,String> result = executor.execOneThread(commands, workingPath);
		return result;
	}
	
	public static Pair<String,String> compileJUnitTestCases(
			String command,
			String dependencies,
			String testPath,
			String workingPath) {
	
		String[] commands = {
				command,
				"-cp",
				"./lib/junit-4.12.jar:./lib/hamcrest-all-1.3.jar:" + dependencies,
				testPath
		};
		
		printCommands(commands);
		ExecCommand executor = new ExecCommand();
		Pair<String,String> result = executor.execOneThread(commands, ".");
		return result;
	}
	
	public static Pair<String,String> generatePiTestMutationTest(
			String command,
			String[] dependancies,
			String reportDir,
			String sourceDir,
			String excludeClasses,
			String targetClasses,
			String targetTests,
			String workingPath,
			int timeBudget
			) {
		
		
		// required pitest libraries
		String classPath = "./lib/pitest-command-line-1.1.10.jar:./lib/junit-4.12.jar:./lib/pitest-1.1.10.jar:./lib/hamcrest-all-1.3.jar";
		
		// path for the classes under testing and its dependencies
		for (String dependancy : dependancies) {
			classPath += ":" + dependancy;
		}
		
		String[] commands = {
				command,
				"-cp",
				classPath,
				"org.pitest.mutationtest.commandline.MutationCoverageReport",
				"--reportDir",
				reportDir,
				"--sourceDirs",
				sourceDir,
				"--excludedClasses",
				excludeClasses.equals("") ? "CIVI_UNMATCH_FORMAT" : "\"" + excludeClasses + "\"", 
				"--targetClasses",
				"\"" + targetClasses + "\"",
				"--mutators",
				"ALL",
				"--targetTests",
				targetTests,
				"--timeoutConst",
				"" + timeBudget
		};
		
		printCommands(commands);
		ExecCommand executor = new ExecCommand();
		Pair<String,String> result = executor.execOneThread(commands, workingPath);
		return result;
	}
	
	public static Pair<String,String> extractFixedVersion(String project, int bid, String saveTo) {
		
		String[] commands = {
				"defects4j",
				"checkout",
				"-p",
				project,
				"-v",
				bid + "f", 
				"-w",
				saveTo
		};

		printCommands(commands);
		ExecCommand executor = new ExecCommand();
		Pair<String,String> result = executor.execOneThread(commands, saveTo);
		return result;
		
	}

	
	public static Pair<String, String> gitDiffofTwoFiles(String file1, String file2) {
		String[] commands = {
				"git",
				"diff",
				file1,
				file2
		};
		
		printCommands(commands);
		ExecCommand executor = new ExecCommand();
		Pair<String,String> result = executor.execOneThread(commands, ".");
		return result;
	}


	
	public static Pair<String, String> compileJUnit(String[] dependancies, String toLoc, String sourceLoc) {
		String classPath = "./lib/pitest-command-line-1.1.10.jar:./lib/junit-4.12.jar:./lib/pitest-1.1.10.jar:./lib/hamcrest-all-1.3.jar";
		
		// path for the classes under testing and its dependencies
		for (String dependancy : dependancies) {
			classPath += ":" + dependancy;
		}
		String[] commands = {
				"javac",
				"-d",
				toLoc,
				"-cp",
				classPath,
				sourceLoc
		};
		printCommands(commands);
		ExecCommand executor = new ExecCommand();
		Pair<String, String> result = executor.execOneThread(commands, ".");
		return result;
	}
	
	public static Pair<String, String> compileSourceFile(String command, String[] dependancies, 
			String toLoc, 
			String sourceLoc,
			String workingPath) {
		// path for the classes under testing and its dependencies
		String classPath = "";
		for (String dependancy : dependancies) {
			classPath += File.pathSeparator + dependancy;
		}
		String[] commands = {
				command,
				"-d",
				toLoc,
				"-cp",
				classPath,
				sourceLoc
		};
		printCommands(commands);
		ExecCommand executor = new ExecCommand();
		Pair<String, String> result = executor.execOneThread(commands, workingPath);
		return result;
	}
	
	public static Pair<String, String> remove(String target) {
		String[] commands = {
				"rm",
				target
		};
		printCommands(commands);
		ExecCommand executor = new ExecCommand();
		Pair<String, String> result = executor.execOneThread(commands, ".");
		return result; 
	}
	
	public static Pair<String,String> generateEvosuiteTestCasesForAClass(
			String targetLibrary,
			String className,
			int seed,
			int timeLimit,
			String outputDir,
			String workingPath
			) {
		
		String[] commands = {
				"java",
				"-jar",
				"./lib/evosuite-1.0.2.jar",
				"-generateSuite",
				"-class",
				className,
				"-projectCP",
				targetLibrary,
				"-seed",
				"" + seed,
				"-Dtest_dir=" + outputDir,
				"-Dsearch_budget=" + timeLimit,
				"-Dstopping_condition=MaxTime"
		};
		
		ExecCommand executor = new ExecCommand();
		printCommands(commands);
		Pair<String,String> result = executor.execOneThread(commands, workingPath);
		return result;
	}
	
	public static Pair<String, String> copy(String source, String target) {
		String[] commands = {
				"cp",
				source,
				target
		};
		printCommands(commands);
		ExecCommand executor = new ExecCommand();
		Pair<String, String> result = executor.execOneThread(commands, ".");
		return result;  
	}
	
	public static void printCommands(String[] commands) {
		for (String command : commands) {
			System.out.print(command + " ");
		}
		System.out.println();
	} 
}
