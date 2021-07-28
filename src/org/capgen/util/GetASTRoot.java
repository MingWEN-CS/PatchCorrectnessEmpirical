package org.capgen.util;

import org.eclipse.jdt.core.dom.*;

public class GetASTRoot {

    public static ASTNode getASTRoot(String sourceCodeFile){
        String sourceCode = ReadFileToList.readFiles(sourceCodeFile);
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(sourceCode.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        JavaRootVisitor visitor = new JavaRootVisitor();
        cu.accept(visitor);
        ASTNode root = visitor.getRootNode();
        return root;
    }

    public ASTNode getMethodAST(String sourceCodeFile){
        String sourceCode = ReadFileToList.readFiles(sourceCodeFile);
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(sourceCode.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        JavaMethodVisitor visitor = new JavaMethodVisitor("targetMethod");
        cu.accept(visitor);
        ASTNode node = visitor.getMethodNode();
        return node;
    }


}

class JavaRootVisitor extends ASTVisitor {
    private CompilationUnit unit = null;

    public ASTNode getRootNode() {
        return unit;
    }

    @Override
    public boolean visit(CompilationUnit node) {
        unit = node;
        return super.visit(node);
    }
}

class JavaMethodVisitor extends ASTVisitor {
    private CompilationUnit unit = null;
    private ASTNode targetNode = null;
    private String methodName = "";


    public JavaMethodVisitor(String name) {
        methodName = name;
    }

    public ASTNode getMethodNode() {
//		System.out.println(targetNode.toString());
        return targetNode;
    }

    @Override
    public boolean visit(CompilationUnit node) {
        unit = node;
        return super.visit(node);
    }


    @Override
    public boolean visit(MethodDeclaration node) {
        // TODO Auto-generated method stub
        System.out.println("Visiting..\t" + node.getName() + "\t" + methodName);
        if (node.getName().toString().trim().equals(methodName)) {
            System.out.println("Find the node");
            targetNode = node;
        }
        return super.visit(node);
    }



}
