package org.capgen.algorithm;

import java.util.HashMap;

public class PrimitiveCasting extends CiviOperator{
	
	public static HashMap<String, Integer> primitiveConversions;
	
	static {
		primitiveConversions = new HashMap<String, Integer>();
		primitiveConversions.put("byte", 0);
		primitiveConversions.put("short", 1);
		primitiveConversions.put("int", 2);
		primitiveConversions.put("long", 3);
		primitiveConversions.put("float", 4);
		primitiveConversions.put("double", 5);
	}
	
	
}
