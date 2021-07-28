package org.capgen.algorithm;

import org.capgen.util.IngredientInfo;
import org.capgen.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CiviOperator {
	
	public IngredientInfo target;
	public HashSet<Pair<String, String>> targetReachableVariable;
	public HashSet<Pair<String, String>> targetReachableMethods;
	public static HashSet<String> infixOperator = new HashSet<String>();
	
	static {
		String operator = "*:/:%:+:-:<<:>>:>>>:<:>:<=:>=:==:!=:^:&:!:&&:||";
		String[] split = operator.split(":");
		for (String tmp : split)
			infixOperator.add(tmp);
	}
	
	
	public static Class<?> getClassByName(String className) throws ClassNotFoundException {
		if (className.contains(" "))
			className = className.split(" ")[1];
		
		Class<?> c = Class.forName(className);
		return c;
	}
	
	// Since Spoon use different class names, we need to transfer to Spoon classes
	// We replace the variable name / Filed Name and Method Name
	
	public static boolean isSimpleName(String className){

		if (className.endsWith("CtVariableAccessImpl") || className.endsWith("CtVariableReadImpl") || className.endsWith("CtVariableWriteImpl") ||
				className.endsWith("CtFieldAccessImpl") || className.endsWith("CtFieldReadImpl") || className.endsWith("CtFieldWriteImpl"))
			return true;
		else return false;
	}
	
	public static boolean isInfixExpression(String className){

		if (className.endsWith("CtBinaryOperatorImpl"))
			return true;
		else return false;
	}
	
	public static boolean isForLoop(String className) {
		if (className.endsWith("CtForImpl"))
			return true;
		else return false;
	}
	
	public static boolean isConditionalExpression(String className) {
		if (className.endsWith("CtConditionalImpl"))
			return true;
		else return false;
	}
	
	public static boolean isMethodInvocation(String className) {

		if (className.endsWith("CtInvocationImpl"))
			return true;
		else return false;
	}
	
	public static boolean isSuffixOrPrefix(String className) {
		if (className.endsWith("CtUnaryOperatorImpl"))
			return true;
		else return false;
	}
	
	public static boolean isExpressionStatement(String className) {
		// Based on empirical findings, nearly all the expression statements are method invocations
		return isMethodInvocation(className) || isSuffixOrPrefix(className);
	}
	
	public static boolean isTypeAndPrimitiveType(String className){

		if (className.endsWith("CtTypeReferenceImpl"))
			return true;
		else return false;
	}
	
	public static boolean isAssignment(String className){

		if (className.endsWith("CtAssignmentImpl"))
			return true;
		else return false;
	}
	
	public static boolean isIfStatement(String className) {
		if (className.endsWith("CtIfImpl")) 
			return true;
		else return false;
	}
	
	public static boolean isLiteral(String className) {
		return className.endsWith("CtLiteralImpl");
		
	}
	
	public static boolean isMethodDeclaration(String className) {
		if (className.endsWith("CtMethodImpl")) 
			return true;
		else return false;
	}
	
	public static boolean isConstructorCall(String className) {
		if (className.endsWith("CtConstructorCallImpl"))
			return true;
		else return false;
	}
	
	public static boolean isCatchStatement(String className) {
		if (className.endsWith("CtCatchImpl"))
			return true;
		else return false;
	}
	
	public static boolean isTryStatement(String className) {
		if (className.endsWith("CtTryImpl"))
			return true;
		else return false;
	}
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	public List<String> getSignatureTypes(String paraSignature, HashSet<Pair<String, String>> requiredVariables) {
		paraSignature = paraSignature.replace(" ", "");
		String[] params = paraSignature.split(",");
		List<String> signatureTypes = new ArrayList<String>();
		HashMap<String, String> variableType = new HashMap<String, String>();
		for (Pair<String, String> pair : requiredVariables) {
			variableType.put(pair.getValue().replace(" ", ""), pair.getKey());
		}
		for (String para : params) {
			if (variableType.containsKey(para))
				signatureTypes.add(variableType.get(para));
			else if (variableType.containsKey("(" + para + ")"))
				signatureTypes.add(variableType.get("(" + para + ")"));
			else if (para.startsWith("(") && para.endsWith(")")) {
				para = para.substring(1, para.length() - 1);
				if (variableType.containsKey(para))
					signatureTypes.add(variableType.get(para));
			} else {
				if (isNumeric(para)) {
					if (para.contains("."))
						signatureTypes.add("double");
					else signatureTypes.add("int");
				}
			}
		}
		return signatureTypes;
	}
	
	protected boolean isCompatible(String class1, String class2) {
		try {
			if (class1.endsWith("<?>"))
				class1 = class1.substring(0, class1.indexOf("<?>"));
			if (class2.endsWith("<?>"))
				class2 = class2.substring(0, class2.indexOf("<?>"));
			Class<?> c1 = Class.forName(class1);
			Class<?> c2 = Class.forName(class2);
			if (c1.isAssignableFrom(c2)) return true;
			return false;
		} catch (ClassNotFoundException e) {
			System.out.println("Class \t" + class1 + " or " + class2 + "can not be resolved");
			return false;
		}
	}
	
	public static double getReplaceScore(String className) {
		double score = 0;
		if (isSimpleName(className))
			score = 0.18;
		else if (isLiteral(className))
			score = 0.000;
		else if (isInfixExpression(className))
			score = 0.1600;
		else if (isTypeAndPrimitiveType(className))
			score = 0.0666;
		else if (isAssignment(className))
			score = 0.0333;
		else if (isConditionalExpression(className))
			score = 0.013479624;
		return score;
	}
//	
//	public static double getInsertScore(String targetName, String sourceName) {
//		double score = 0;
//		if (isMethodDeclaration(targetName)) {
//			if (isExpressionStatement(sourceName)) {
//				score = 0.9324;
//			} else if (isIfStatement(sourceName))
//				score = 0.2664;
//		}
//		else if (isMethodInvocation(targetName)) {
//			if (isSimpleName(sourceName))
//				score = 0.8991;
//			else if (isInfixExpression(sourceName))
//				score = 0.5328;
//			else if (isMethodInvocation(sourceName))
//				score = 0.8325;
//			else if (isTypeAndPrimitiveType(sourceName))
//				score = 0.4329;
//		}
//		else if (isIfStatement(targetName)) {
//			if (isExpressionStatement(sourceName))
//				score = 0.8658;
//			else if (isIfStatement(sourceName))
//				score = 0.5994;
//		}
//		else if (isTryStatement(targetName) && isExpressionStatement(sourceName))
//				score = 0.3996;
//		return score;
//	}
//	
//	public static double getDeleteScore(String targetName, String sourceName) {
//		double score = 0;
//		if (isMethodInvocation(targetName)) {
//			if (isSimpleName(sourceName))
//				score = 0.7326;
//			else if (isMethodInvocation(sourceName) || isConstructorCall(sourceName))
//				score = 0.5328;
//			else if (isInfixExpression(sourceName))
//				score = 0.2997;
//			else if (isLiteral(sourceName))
//				score = 0.1998;
//		} else if (isMethodDeclaration(targetName)) {
//			if (isExpressionStatement(sourceName)) {
//				score = 0.6327;
//			}
//		} else if (isIfStatement(targetName)) {
//			if (isInfixExpression(sourceName))
//				score = 0.666;
//			else if (isExpressionStatement(sourceName))
//				score = 0.333;
//		} else if (isInfixExpression(targetName)) {
//			if (isSuffixOrPrefix(sourceName))
//				score = 0.2331;
//			else if (isMethodInvocation(sourceName))
//				score = 0.0999;
//		} else if (isCatchStatement(targetName)) {
//			if (isExpressionStatement(sourceName))
//				score = 0.1332;
//		}
//		return score;
//	}
	
	public static double getInsertScore(String targetName, String sourceName) {
		double score = 0;
		if (isMethodDeclaration(targetName)) {
			if (isExpressionStatement(sourceName)) {
				score = 0.0815;
			} else if (isIfStatement(sourceName))
				score = 0.00627;
		}
		else if (isMethodInvocation(targetName)) {
			if (isSimpleName(sourceName))
				score = 0.03134;
			else if (isInfixExpression(sourceName))
				score = 0.01254;
			else if (isMethodInvocation(sourceName))
				score = 0.02821;
			else if (isTypeAndPrimitiveType(sourceName))
				score = 0.00752;
		}
		else if (isIfStatement(targetName))
			if (isExpressionStatement(sourceName))
				score = 0.03041;
			else if (isIfStatement(sourceName))
				score = 0.01411;
			else if (isTryStatement(targetName) && isExpressionStatement(sourceName))
				score = 0.00752;
		return score;
	}
	
	public static double getDeleteScore(String targetName, String sourceName) {
		double score = 0;
		if (isMethodInvocation(targetName)) {
			if (isSimpleName(sourceName))
				score = 0.01912;
			else if (isMethodInvocation(sourceName) || isConstructorCall(sourceName))
				score = 0.01567;
			else if (isInfixExpression(sourceName))
				score = 0.00658;
			else if (isLiteral(sourceName))
				score = 0.003761;
		} else if (isMethodDeclaration(targetName)) {
			if (isExpressionStatement(sourceName)) {
				score = 0.01567;
			}
		} else if (isIfStatement(targetName)) {
			if (isInfixExpression(sourceName))
				score = 0.014420;
			else if (isExpressionStatement(sourceName))
				score = 0.007210;
		} else if (isInfixExpression(targetName)) {
			if (isSuffixOrPrefix(sourceName))
				score = 0.004075;
			else if (isMethodInvocation(sourceName))
				score = 0.002821;
		} else if (isCatchStatement(targetName)) {
			if (isExpressionStatement(sourceName))
				score = 0.003134;
		}
		return score;
	}
}
