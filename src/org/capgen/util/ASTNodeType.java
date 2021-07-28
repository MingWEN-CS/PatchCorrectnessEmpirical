package org.capgen.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ASTNodeType {
	
	
	// JDT AST Node Type Index
	// referring to http://help.eclipse.org/juno/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Freference%2Fapi%2Findex-files%2Findex-17.html
	public static HashMap<Integer,String> ASTNodeTypeIndex = new HashMap<Integer,String>();
	public static List<Integer> SimpleNameParent = new ArrayList<Integer>();
	public static final int NODE_NUM = 100;
	public static final int	ANNOTATION_TYPE_DECLARATION = 81;
	public static final int	ANNOTATION_TYPE_MEMBER_DECLARATION = 82;
	public static final int	ANONYMOUS_CLASS_DECLARATION = 1;
	public static final int	ARRAY_ACCESS = 2;
	public static final int	ARRAY_CREATION = 3;
	public static final int	ARRAY_INITIALIZER =  4;
	public static final int	ARRAY_TYPE = 5;
	public static final int	ASSERT_STATEMENT =  6;
	public static final int	ASSIGNMENT = 7;
	public static final int	BLOCK = 8;
	public static final int	BLOCK_COMMENT = 64;
	public static final int	BOOLEAN_LITERAL = 9;
	public static final int	BREAK_STATEMENT = 10;
	public static final int	CAST_EXPRESSION = 11;
	public static final int	CATCH_CLAUSE = 12;
	public static final int	CHARACTER_LITERAL = 13;
	public static final int	CLASS_INSTANCE_CREATION = 14;
	public static final int	COMPILATION_UNIT = 15;
	public static final int	CONDITIONAL_EXPRESSION = 16;
	public static final int	CONSTRUCTOR_INVOCATION = 17;
	public static final int	CONTINUE_STATEMENT = 18;
	public static final int	DO_STATEMENT = 19;
	public static final int	EMPTY_STATEMENT = 20;
	public static final int	ENHANCED_FOR_STATEMENT = 70;
	public static final int	ENUM_CONSTANT_DECLARATION = 72;
	public static final int	ENUM_DECLARATION = 71;
	public static final int	EXPRESSION_STATEMENT = 21;
	public static final int	FIELD_ACCESS = 22;
	public static final int	FIELD_DECLARATION = 23;
	public static final int	FOR_STATEMENT = 24;
	public static final int	IF_STATEMENT = 25;
	public static final int	IMPORT_DECLARATION = 26;
	public static final int	INFIX_EXPRESSION = 27;
	public static final int	INITIALIZER = 28;
	public static final int	INSTANCEOF_EXPRESSION = 62;
	public static final int	JAVADOC = 29;
	public static final int	LABELED_STATEMENT = 30;
	public static final int	LINE_COMMENT = 63;
	public static final int	MALFORMED = 1;
	public static final int	MARKER_ANNOTATION = 78;
	public static final int	MEMBER_REF = 67;
	public static final int	MEMBER_VALUE_PAIR = 80;
	public static final int	METHOD_DECLARATION = 31;
	public static final int	METHOD_INVOCATION = 32;
	public static final int	METHOD_REF = 68;
	public static final int	METHOD_REF_PARAMETER = 69;
	public static final int	MODIFIER = 83;
	public static final int	NORMAL_ANNOTATION = 77;
	public static final int	NULL_LITERAL = 33;
	public static final int	NUMBER_LITERAL = 34;
	public static final int	ORIGINAL = 2;
	public static final int	PACKAGE_DECLARATION = 35;
	public static final int	PARAMETERIZED_TYPE = 74;
	public static final int	PARENTHESIZED_EXPRESSION = 36;
	public static final int	POSTFIX_EXPRESSION = 37;
	public static final int	PREFIX_EXPRESSION = 38;
	public static final int	PRIMITIVE_TYPE = 39;
	public static final int	PROTECT = 4;
	public static final int	QUALIFIED_NAME = 40;
	public static final int	QUALIFIED_TYPE = 75;
	public static final int	RECOVERED = 8;
	public static final int	RETURN_STATEMENT = 41;
	public static final int	SIMPLE_NAME = 42;
	public static final int	SIMPLE_TYPE = 43;
	public static final int	SINGLE_MEMBER_ANNOTATION = 79;
	public static final int	SINGLE_VARIABLE_DECLARATION = 44;
	public static final int	STRING_LITERAL = 45;
	public static final int	SUPER_CONSTRUCTOR_INVOCATION = 46;
	public static final int	SUPER_FIELD_ACCESS = 47;
	public static final int	SUPER_METHOD_INVOCATION = 48;
	public static final int	SWITCH_CASE = 49;
	public static final int	SWITCH_STATEMENT = 50;
	public static final int	SYNCHRONIZED_STATEMENT = 51;
	public static final int	TAG_ELEMENT = 65;
	public static final int	TEXT_ELEMENT = 66;
	public static final int	THIS_EXPRESSION = 52;
	public static final int	THROW_STATEMENT = 53;
	public static final int	TRY_STATEMENT = 54;
	public static final int	TYPE_DECLARATION = 55;
	public static final int	TYPE_DECLARATION_STATEMENT = 56;
	public static final int	TYPE_LITERAL = 57;
	public static final int	TYPE_PARAMETER = 73;
	public static final int	UNION_TYPE = 84;
	public static final int	VARIABLE_DECLARATION_EXPRESSION = 58;
	public static final int	VARIABLE_DECLARATION_FRAGMENT = 59;
	public static final int	VARIABLE_DECLARATION_STATEMENT = 60;
	public static final int	WHILE_STATEMENT = 61;
	public static final int	WILDCARD_TYPE = 76;
	
	static {
		ASTNodeTypeIndex.put(81,"ANNOTATION_TYPE_DECLARATION");
		ASTNodeTypeIndex.put(82,"ANNOTATION_TYPE_MEMBER_DECLARATION");
		ASTNodeTypeIndex.put(1,"ANONYMOUS_CLASS_DECLARATION");
		ASTNodeTypeIndex.put(2,"ARRAY_ACCESS");
		ASTNodeTypeIndex.put(3,"ARRAY_CREATION");
		ASTNodeTypeIndex.put(4,"ARRAY_INITIALIZER");
		ASTNodeTypeIndex.put(5,"ARRAY_TYPE");
		ASTNodeTypeIndex.put(6,"ASSERT_STATEMENT");
		ASTNodeTypeIndex.put(7,"ASSIGNMENT");
		ASTNodeTypeIndex.put(8,"BLOCK");
		ASTNodeTypeIndex.put(64,"BLOCK_COMMENT");
		ASTNodeTypeIndex.put(9,"BOOLEAN_LITERAL");
		ASTNodeTypeIndex.put(10,"BREAK_STATEMENT");
		ASTNodeTypeIndex.put(11,"CAST_EXPRESSION");
		ASTNodeTypeIndex.put(12,"CATCH_CLAUSE");
		ASTNodeTypeIndex.put(13,"CHARACTER_LITERAL");
		ASTNodeTypeIndex.put(14,"CLASS_INSTANCE_CREATION");
		ASTNodeTypeIndex.put(15,"COMPILATION_UNIT");
		ASTNodeTypeIndex.put(16,"CONDITIONAL_EXPRESSION");
		ASTNodeTypeIndex.put(17,"CONSTRUCTOR_INVOCATION");
		ASTNodeTypeIndex.put(18,"CONTINUE_STATEMENT");
		ASTNodeTypeIndex.put(19,"DO_STATEMENT");
		ASTNodeTypeIndex.put(20,"EMPTY_STATEMENT");
		ASTNodeTypeIndex.put(70,"ENHANCED_FOR_STATEMENT");
		ASTNodeTypeIndex.put(72,"ENUM_CONSTANT_DECLARATION");
		ASTNodeTypeIndex.put(71,"ENUM_DECLARATION");
		ASTNodeTypeIndex.put(21,"EXPRESSION_STATEMENT");
		ASTNodeTypeIndex.put(22,"FIELD_ACCESS");
		ASTNodeTypeIndex.put(23,"FIELD_DECLARATION");
		ASTNodeTypeIndex.put(24,"FOR_STATEMENT");
		ASTNodeTypeIndex.put(25,"IF_STATEMENT");
		ASTNodeTypeIndex.put(26,"IMPORT_DECLARATION");
		ASTNodeTypeIndex.put(27,"INFIX_EXPRESSION");
		ASTNodeTypeIndex.put(28,"INITIALIZER");
		ASTNodeTypeIndex.put(62,"INSTANCEOF_EXPRESSION");
		ASTNodeTypeIndex.put(29,"JAVADOC");
		ASTNodeTypeIndex.put(30,"LABELED_STATEMENT");
		ASTNodeTypeIndex.put(63,"LINE_COMMENT");
//		ASTNodeTypeIndex.put(1,"MALFORMED");
		ASTNodeTypeIndex.put(78,"MARKER_ANNOTATION");
		ASTNodeTypeIndex.put(67,"MEMBER_REF");
		ASTNodeTypeIndex.put(80,"MEMBER_VALUE_PAIR");
		ASTNodeTypeIndex.put(31,"METHOD_DECLARATION");
		ASTNodeTypeIndex.put(32,"METHOD_INVOCATION");
		ASTNodeTypeIndex.put(68,"METHOD_REF");
		ASTNodeTypeIndex.put(69,"METHOD_REF_PARAMETER");
		ASTNodeTypeIndex.put(83,"MODIFIER");
		ASTNodeTypeIndex.put(77,"NORMAL_ANNOTATION");
		ASTNodeTypeIndex.put(33,"NULL_LITERAL");
		ASTNodeTypeIndex.put(34,"NUMBER_LITERAL");
//		ASTNodeTypeIndex.put(2,"ORIGINAL");
		ASTNodeTypeIndex.put(35,"PACKAGE_DECLARATION");
		ASTNodeTypeIndex.put(74,"PARAMETERIZED_TYPE");
		ASTNodeTypeIndex.put(36,"PARENTHESIZED_EXPRESSION");
		ASTNodeTypeIndex.put(37,"POSTFIX_EXPRESSION");
		ASTNodeTypeIndex.put(38,"PREFIX_EXPRESSION");
		ASTNodeTypeIndex.put(39,"PRIMITIVE_TYPE");
//		ASTNodeTypeIndex.put(4,"PROTECT");
		ASTNodeTypeIndex.put(40,"QUALIFIED_NAME");
		ASTNodeTypeIndex.put(75,"QUALIFIED_TYPE");
//		ASTNodeTypeIndex.put(8,"RECOVERED");
		ASTNodeTypeIndex.put(41,"RETURN_STATEMENT");
		ASTNodeTypeIndex.put(42,"SIMPLE_NAME");
		ASTNodeTypeIndex.put(43,"SIMPLE_TYPE");
		ASTNodeTypeIndex.put(79,"SINGLE_MEMBER_ANNOTATION");
		ASTNodeTypeIndex.put(44,"SINGLE_VARIABLE_DECLARATION");
		ASTNodeTypeIndex.put(45,"STRING_LITERAL");
		ASTNodeTypeIndex.put(46,"SUPER_CONSTRUCTOR_INVOCATION");
		ASTNodeTypeIndex.put(47,"SUPER_FIELD_ACCESS");
		ASTNodeTypeIndex.put(48,"SUPER_METHOD_INVOCATION");
		ASTNodeTypeIndex.put(49,"SWITCH_CASE");
		ASTNodeTypeIndex.put(50,"SWITCH_STATEMENT");
		ASTNodeTypeIndex.put(51,"SYNCHRONIZED_STATEMENT");
		ASTNodeTypeIndex.put(65,"TAG_ELEMENT");
		ASTNodeTypeIndex.put(66,"TEXT_ELEMENT");
		ASTNodeTypeIndex.put(52,"THIS_EXPRESSION");
		ASTNodeTypeIndex.put(53,"THROW_STATEMENT");
		ASTNodeTypeIndex.put(54,"TRY_STATEMENT");
		ASTNodeTypeIndex.put(55,"TYPE_DECLARATION");
		ASTNodeTypeIndex.put(56,"TYPE_DECLARATION_STATEMENT");
		ASTNodeTypeIndex.put(57,"TYPE_LITERAL");
		ASTNodeTypeIndex.put(73,"TYPE_PARAMETER");
		ASTNodeTypeIndex.put(84,"UNION_TYPE");
		ASTNodeTypeIndex.put(58,"VARIABLE_DECLARATION_EXPRESSION");
		ASTNodeTypeIndex.put(59,"VARIABLE_DECLARATION_FRAGMENT");
		ASTNodeTypeIndex.put(60,"VARIABLE_DECLARATION_STATEMENT");
		ASTNodeTypeIndex.put(61,"WHILE_STATEMENT");
		ASTNodeTypeIndex.put(76,"WILDCARD_TYPE");	
		
		
		// Statements
		
		SimpleNameParent.add(ASSERT_STATEMENT);
		SimpleNameParent.add(BREAK_STATEMENT);
		SimpleNameParent.add(CONSTRUCTOR_INVOCATION);
		SimpleNameParent.add(CONTINUE_STATEMENT);
		SimpleNameParent.add(DO_STATEMENT);
		SimpleNameParent.add(FOR_STATEMENT);
		SimpleNameParent.add(IF_STATEMENT);
		SimpleNameParent.add(LABELED_STATEMENT);
		SimpleNameParent.add(RETURN_STATEMENT);
		SimpleNameParent.add(SUPER_CONSTRUCTOR_INVOCATION);
		SimpleNameParent.add(SWITCH_CASE);
		SimpleNameParent.add(SWITCH_STATEMENT);
		SimpleNameParent.add(SYNCHRONIZED_STATEMENT);
		SimpleNameParent.add(THROW_STATEMENT);
		SimpleNameParent.add(TRY_STATEMENT);
		SimpleNameParent.add(VARIABLE_DECLARATION_STATEMENT);
		SimpleNameParent.add(WHILE_STATEMENT);
		
		
		// Expressions
		SimpleNameParent.add(ARRAY_ACCESS);
		SimpleNameParent.add(ARRAY_CREATION);
		SimpleNameParent.add(ARRAY_INITIALIZER);
		SimpleNameParent.add(ASSIGNMENT);
		SimpleNameParent.add(BOOLEAN_LITERAL);
		SimpleNameParent.add(CAST_EXPRESSION);
		SimpleNameParent.add(CHARACTER_LITERAL);
		SimpleNameParent.add(CLASS_INSTANCE_CREATION);
		SimpleNameParent.add(CONDITIONAL_EXPRESSION);
		SimpleNameParent.add(FIELD_ACCESS);
		SimpleNameParent.add(INFIX_EXPRESSION);
		SimpleNameParent.add(METHOD_INVOCATION);
		SimpleNameParent.add(NULL_LITERAL);
		SimpleNameParent.add(NUMBER_LITERAL);
		SimpleNameParent.add(PARENTHESIZED_EXPRESSION);
		SimpleNameParent.add(POSTFIX_EXPRESSION);
		SimpleNameParent.add(PREFIX_EXPRESSION);
		SimpleNameParent.add(SUPER_METHOD_INVOCATION);
		SimpleNameParent.add(SUPER_FIELD_ACCESS);
		SimpleNameParent.add(THIS_EXPRESSION);
		SimpleNameParent.add(VARIABLE_DECLARATION_EXPRESSION);

		SimpleNameParent.add(METHOD_DECLARATION);		
	}
	
	public static String get(int id) {
		return ASTNodeTypeIndex.get(id);
	}
	
	public static boolean contains(int id) {
		return ASTNodeTypeIndex.containsKey(id);

	}
}
