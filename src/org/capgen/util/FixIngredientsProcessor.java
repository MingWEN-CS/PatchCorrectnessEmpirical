package org.capgen.util;

import org.capgen.algorithm.PrimitiveCasting;
import spoon.reflect.code.CtBlock;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.*;
import spoon.support.reflect.declaration.CtClassImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FixIngredientsProcessor extends CiviProcessor{
	
	public List<String> ingredients = new ArrayList<String>();
	public List<List<String>> ingredientsContext = new ArrayList<List<String>>();
	public List<List<String>> ingredientsSiblings = new ArrayList<List<String>>();
	public List<HashSet<Pair<String, String>>> ingredientsMethod = new ArrayList<HashSet<Pair<String, String>>>();
	public List<HashSet<Pair<String, String>>> ingredientsVariable = new ArrayList<HashSet<Pair<String,String>>>();
	public List<String> ingredientsCategory = new ArrayList<String>();
//	public HashMap<String,Integer> ingredientsIndex = new HashMap<String,Integer>();
	public List<Pair<Integer,Integer>> ingredientsPosition = new ArrayList<Pair<Integer,Integer>>(); 
	public List<Pair<Integer,Integer>> ingredientsLine = new ArrayList<Pair<Integer,Integer>>(); 
	public List<String> ingredientsType = new ArrayList<String>();
	public List<String> ingredientsClassName = new ArrayList<String>();
	
	public String sourceContent;
	public HashSet<String> classNames;
	
	public FixIngredientsProcessor(String content, HashSet<String> classNames) {
		// TODO Auto-generated constructor stub
		this.sourceContent = content;
		this.classNames = new HashSet<String>(classNames);
	}
	
	public String getClassName(CtElement element) {
		CtElement parent = element;
		while (parent.getParent() != null) {
			parent = parent.getParent();
			if (parent instanceof CtClassImpl) {
				CtClassImpl classImpl = (CtClassImpl) parent;
//				System.out.println(classImpl.getSimpleName());
				return classImpl.getSimpleName();
			}
			
		}
		return "";
	}
	
	@Override
	public void process(CtElement element) {
		
		String className = getClassName(element);
//		System.out.println(className);
        if(this.classNames.contains(className) && !(element instanceof CtBlock || element instanceof CtClass
				|| element instanceof CtMethod || element instanceof CtCommentImpl)
				// || element instanceof CtTry 
				// || element instanceof CtCatch
				// || element instanceof CtTypeAccessImpl || element instanceof CtTypedNameElement)
				&& !(element.toString().startsWith("super")) && (element instanceof CtExpressionImpl || element instanceof CtStatementImpl || element instanceof CtTypeReferenceImpl)) {
			
			SourcePosition position = element.getPosition();
           
			boolean isPrimitive = true;
			if (element instanceof CtTypeReferenceImpl) {
                if (!PrimitiveCasting.primitiveConversions.containsKey(element.toString().trim())) {
					isPrimitive = false;
				}
			}
			if (position.getSourceEnd() > 0 && position.getSourceEnd() <= sourceContent.length() && isPrimitive) {  
			int end = (position.getSourceEnd() + 1 > sourceContent.length() ? sourceContent.length() : position.getSourceEnd() + 1);
            String content = sourceContent.substring(position.getSourceStart(), end);
            content = element.toString().replace("\n", "").replace("\r", "");
			if (content.startsWith("(") && content.endsWith(")"))
				content = content.substring(1, content.length() - 1);
//			content = content.replace(" ", "");
//			if (!ingredientsIndex.containsKey(content)) {
//				ingredientsIndex.put(content, ingredients.size());
				
            ingredients.add(content);
			
			if (element instanceof CtStatementImpl) {
				ingredientsCategory.add("Statement");
				ingredientsType.add(element.getClass().getSimpleName());
			}
			else if (element instanceof CtExpressionImpl) {
				ingredientsCategory.add("Expression");
				if (((CtExpressionImpl) element).getType() == null)
                    ingredientsType.add("null");
                else
                    ingredientsType.add(((CtExpressionImpl) element).getType().toString());
			} else {
				ingredientsCategory.add("PrimitiveType");
				ingredientsType.add("null");
			}
			
			ingredientsClassName.add(element.getClass().toString());
			ingredientsContext.add(new ArrayList<String>());
			ingredientsSiblings.add(new ArrayList<String>());
			ingredientsMethod.add(new HashSet<Pair<String, String>>());
			ingredientsVariable.add(new HashSet<Pair<String,String>>());
			
//			}
			ingredientsPosition.add(new Pair<Integer, Integer>(position.getSourceStart(), position.getSourceEnd()));
			ingredientsLine.add(new Pair<Integer,Integer>(position.getLine(), position.getEndLine()));
			int index = ingredients.size() - 1;
			// obtain the method invoked in this ingredients
//			if (content.equals("(org.apache.commons.math.util.MathUtils.equals(x, y, 1))")) {
			List<CtElement> elements  = element.filterChildren(new TypeFilter(CtInvocationImpl.class)).list();
			for (CtElement tmp : elements) {
				CtInvocationImpl invocation = (CtInvocationImpl) tmp;
//			
				String method = invocation.toString();
				if (method.startsWith("("))
					method = method.substring(1);
				method = method.substring(0, method.indexOf("("));
				if (method.contains("."))
				method = method.substring(method.lastIndexOf(".") + 1);
				String type = "";
				if (invocation.getType() != null)
					type = invocation.getType().toString();
				if (method.equals("")) continue;
				ingredientsMethod.get(index).add(new Pair<String,String>(type, method));
			}
//			
////		}
//			
//			// obtain the variables used in this ingredients
////			if (content.equals("(org.apache.commons.math.util.MathUtils.equals(x, y, 1))")) {
			
			elements  = element.filterChildren(new TypeFilter(CtVariableReadImpl.class)).list();
			for (CtElement tmp : elements) {
				CtVariableReadImpl variable = (CtVariableReadImpl) tmp;
                if (variable.getType() == null || variable.getType().toString().equals("") || variable.toString().equals("")) continue;
                String simpleName = variable.toString();
                if (simpleName.startsWith("(") && simpleName.endsWith(")"))
                	simpleName = simpleName.substring(1, simpleName.length() - 1);
                ingredientsVariable.get(index).add(new Pair<String,String>(variable.getType().toString(), simpleName));
			}
			
			elements  = element.filterChildren(new TypeFilter(CtVariableWriteImpl.class)).list();
			for (CtElement tmp : elements) {
				CtVariableWriteImpl variable = (CtVariableWriteImpl) tmp;
                if (variable.getType() == null || variable.getType().toString().equals("") || variable.toString().equals("")) continue;
                String simpleName = variable.toString();
                if (simpleName.startsWith("(") && simpleName.endsWith(")"))
                	simpleName = simpleName.substring(1, simpleName.length() - 1);
                ingredientsVariable.get(index).add(new Pair<String,String>(variable.getType().toString(), simpleName));
			}
			
			elements = element.filterChildren(new TypeFilter(CtFieldReadImpl.class)).list();
			for (CtElement tmp : elements) {
				CtFieldReadImpl variable = (CtFieldReadImpl) tmp;
                if (variable.getType() == null || variable.getType().toString().equals("") || variable.toString().equals("")) continue;
				String simpleName = variable.toString();
                if (simpleName.startsWith("(") && simpleName.endsWith(")"))
                	simpleName = simpleName.substring(1, simpleName.length() - 1);
				if (simpleName.contains("."))
					simpleName = simpleName.substring(simpleName.lastIndexOf(".") + 1);
				ingredientsVariable.get(index).add(new Pair<String,String>(variable.getType().toString(), simpleName));
			}
			
			elements = element.filterChildren(new TypeFilter(CtFieldWriteImpl.class)).list();
			for (CtElement tmp : elements) {
				CtFieldWriteImpl variable = (CtFieldWriteImpl) tmp;
				String simpleName = variable.toString();
                if (variable.getType() == null || variable.getType().toString().equals("") || variable.toString().equals("")) continue;
                if (simpleName.startsWith("(") && simpleName.endsWith(")"))
                	simpleName = simpleName.substring(1, simpleName.length() - 1);
				if (simpleName.contains("."))
					simpleName = simpleName.substring(simpleName.lastIndexOf(".") + 1);
				ingredientsVariable.get(index).add(new Pair<String,String>(variable.getType().toString(), simpleName));
			}
			
//			}
			// obtain the context of the ingredients
			
			CtElement parent = element.getParent();
			while (parent != null && !(parent instanceof CtMethodImpl)) {
				if (!(parent instanceof CtBlock))
					ingredientsContext.get(index).add(parent.getClass().getName());
				parent = parent.getParent();
			}
			
			// We extract the context of siblings of the target node
			// Retrieve the enclosing block 
//			if (element instanceof CtStatementImpl || element) {
				parent = element.getParent();
				while (parent != null && !(parent instanceof CtBlockImpl))
					parent = parent.getParent();
				
                if (parent != null) {
				elements = parent.filterChildren(new TypeFilter(CtStatementImpl.class)).list();
				
				for (CtElement tmp : elements) {
					if (!(tmp instanceof CtBlock))
						ingredientsSiblings.get(index).add(tmp.getClass().getName());
				}
				
				elements = parent.filterChildren(new TypeFilter(CtInvocationImpl.class)).list();
				for (CtElement tmp : elements) {
					ingredientsSiblings.get(index).add(tmp.getClass().getName());
				}
                }
				// then retrieve all the statements in this block
//			}
		    }
//			if (parent != null && parent.getParent() != null) {
//				ingredientsContext.get(index).add(parent.getParent().getClass().getName());
//			}
		}
	}
}
