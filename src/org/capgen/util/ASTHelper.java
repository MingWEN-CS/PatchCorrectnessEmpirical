package org.capgen.util;

import org.eclipse.jdt.core.dom.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;


public class ASTHelper {
	
	public static HashMap<Integer,Integer> getLineNumber(String file) throws Exception{
		int lineNum = 1;
		HashMap<Integer,Integer> charLineMap = new HashMap<Integer,Integer>();
		String fileContent = ReadFileToList.readFiles(file);
		char[] charArray = fileContent.toCharArray();
		System.out.println("File Length : \t" + charArray.length);
		charLineMap.put(0, 0);
		for(int i=0; i<charArray.length; i++){
			charLineMap.put(i + 1, lineNum);
			if(charArray[i]=='\n'){
				lineNum++;
			}
		}		
		return charLineMap;
	}
	
	public static List<ASTNode> getDescendents(ASTNode node) {
		List<ASTNode> allNodes = new ArrayList<ASTNode>();
		int index = 0;
		allNodes.add(node);
//		System.out.println("Node:\t" + node.toString() + allNodes.toString());
		while (index < allNodes.size()) {
			ASTNode current = allNodes.get(index);
//			System.out.println(index);
//			System.out.println(current.toString());
			List<ASTNode> children = getChildren(current);
//			System.out.println("children size :\t" + children.size());
			for (ASTNode child : children) {
				allNodes.add(child);
			}
			index++;
		}
		return allNodes;
	}
	
	public static List<ASTNode> getChildren(ASTNode node) {
	    List<ASTNode> children = new ArrayList<ASTNode>();
	    List list = node.structuralPropertiesForType();
//	    System.out.println(list.size());
		for (int i = 0; i < list.size(); i++) {
//	    	System.out.println(list.get(i).getClass());
	    	Object child = node.getStructuralProperty((StructuralPropertyDescriptor)list.get(i));
	    	if (child instanceof List) {
	    		List childList = (List) child;
	    		for (int j = 0; j < childList.size(); j++) {
	    			Object tmp = childList.get(j);
	    			if (tmp != null && tmp instanceof ASTNode)
	    				children.add((ASTNode) tmp);
	    		}
	    		
	    	} else if (child != null && child instanceof ASTNode) {
	    		children.add((ASTNode) child);
	    	} else if (child instanceof SingleVariableDeclaration) {
	    		System.out.println(child.toString());
	    	}
//			System.out.println(children.size());
	    }
	    return children;
	}

	
	private static void preOrderVisit(ASTNode node, List<ASTNode> nodes) {
		if (node != null) {
			nodes.add(node);
			List<ASTNode> children = getChildren(node);
			if (node instanceof IfStatement) {
				
			} else {
				for (int i = 0; i < children.size(); i++) {
					preOrderVisit(children.get(i), nodes);
				}
			}
		}
	}
	
	public static List<List<ASTNode>> getPrimePath(ASTNode node) {
		List<List<ASTNode>> pathes = new ArrayList<List<ASTNode>>();
		pathes.add(new ArrayList<ASTNode>());
		Stack<ASTNode> nodeStack = new Stack<ASTNode>();
//		nodeStack.push(node);
		
		if (node instanceof MethodDeclaration) {
			if (((MethodDeclaration) node).getBody() != null)
				nodeStack.push(((MethodDeclaration) node).getBody());
		} else {
			nodeStack.push(node);
		}
		
		while (nodeStack.empty() == false) {
			System.out.println("The size of node stack:\t" + nodeStack.size());
			ASTNode current = nodeStack.peek();
			
			
			nodeStack.pop();
//			System.out.println(current.toString());
			if (current instanceof IfStatement) {
				for (int p = 0; p < pathes.size(); p++)
					pathes.get(p).add(((IfStatement) current).getExpression());
				
//				System.out.println(((IfStatement) current).getExpression());
				
				List<List<ASTNode>> subtrees = getPrimePath(((IfStatement) current).getThenStatement());
				
				
				
				int prePathNum = pathes.size();
				
				System.out.println("Dealing with IF branch");
//				System.out.println( ASTNodeType.get(((IfStatement) current).getThenStatement().getNodeType()));
				for (int i = 0; i < subtrees.size(); i++) {
					if (subtrees.get(i).get(0) instanceof Block)
						subtrees.get(i).remove(0);
//					for (int j = 0; j < subtree.size(); j++)
					
//						System.out.println(subtree.get(j).toString());
				}
				
				for (int p = 0; p < prePathNum; p++) {
					/*
					 * Create new paths
					 * 
					 * */
					for (int q = 0; q < subtrees.size(); q++) {
						List<ASTNode> path = new ArrayList<ASTNode>(pathes.get(p));
						path.addAll((subtrees.get(q)));
						pathes.add(path);
					}
				}
				
				System.out.println("Finishing dealing with IF branch");
//				System.out.println(((IfStatement) current).getElseStatement());
				if (((IfStatement) current).getElseStatement() != null) {
					subtrees = getPrimePath( ((IfStatement) current).getElseStatement());
					
					for (int i = 0; i < subtrees.size(); i++) {
						if (subtrees.get(i).get(0) instanceof Block)
							subtrees.get(i).remove(0);
//						for (int j = 0; j < subtree.size(); j++)
						
//							System.out.println(subtree.get(j).toString());
					}
					
					for (int p = 0; p < prePathNum; p++) {
						for (int q = 0; q < subtrees.size(); q++) {
							pathes.get(p).addAll(subtrees.get(q));
						}
					}
						
				}
				
				System.out.println("Finishing dealing with Else branch");
				
			} else if (current instanceof SwitchStatement) {
				System.out.println("Switch Statement");
				List<ASTNode> statements = ((SwitchStatement) current).statements();
				for (int i = 0; i < statements.size(); i++) {
					
					if (statements.get(i) instanceof SwitchCase) {
						List<ASTNode> children = getChildren(statements.get(i));
						System.out.println(children.size());
						System.out.println(statements.get(i).toString());
					}
//					System.out.println("Statement:\t" + i);
//					System.out.println(ASTNodeType.get(statements.get(i).getNodeType()));
				}
				
			} else if (current instanceof WhileStatement) {
				
			} 
			
			else {
				for (int i = 0; i < pathes.size(); i++)
					pathes.get(i).add(current);
				
				List<ASTNode> children = getChildren(current);
				for (int i = children.size() - 1; i >= 0; i--) {
//					System.out.println("The " + i + "th child");
//					System.out.println(children.get(i).toString());
					nodeStack.push(children.get(i));
				}
			}
			
//			break;
		}
		System.out.println("Path size:\t" + pathes.size());
		return pathes;
	}
	
	public static List<ASTNode> getPreOrder(ASTNode node) {
		List<ASTNode> nodes = new ArrayList<ASTNode>();
		preOrderVisit(node, nodes);
		return nodes;
	}
}
