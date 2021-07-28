package org.capgen.util;

import org.eclipse.jdt.core.dom.ASTNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetLineToASTMapping {
    public static void mapLineToAST(String filename, String saveFile) throws Exception {
        ASTNode root = GetASTRoot.getASTRoot(filename);
        List<ASTNode> nodes = ASTHelper.getDescendents(root);
        HashMap<Integer, Integer> positionToLine = ASTHelper.getLineNumber(filename);
//		System.out.println(nodes.size());
        List<String> saveLines = new ArrayList<String>();
        for (ASTNode node : nodes) {
            if (ASTNodeType.SimpleNameParent.contains(node.getNodeType())) {
                int startLine = positionToLine.get(node.getStartPosition());
                int endLine = positionToLine.get(node.getStartPosition() + node.getLength());
//				System.out.println(ASTNodeType.get(node.getNodeType()) + "\t" + startLine + "\t" + endLine);
                saveLines.add(startLine + "\t" + endLine + "\t" + node.getNodeType());
            }
        }
        WriteLinesToFile.writeLinesToFile(saveLines, saveFile);
    }

    public static void main(String[] args) throws Exception {
        String filename = "../Datasets_ChangePredict/Lucene/fileIndex/aaa5b9b.java";
        String saveFile = "../Datasets_ChangePredict/Lucene/fileIndex/aaa5b9b.ast";
        mapLineToAST(filename, saveFile);
    }
}
