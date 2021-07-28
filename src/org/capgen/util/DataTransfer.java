package org.capgen.util;


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DataTransfer {
	
	public static HashSet<Integer> stringToIntSet(String line) {
		HashSet<Integer> ids = new HashSet<Integer>();
		line = line.substring(1, line.length() - 1);
		if (!line.contains(",")) {
			if (!line.equals(""))
				ids.add(Integer.parseInt(line));
			return ids;
		}
		String[] splits = line.split(",");
		
		for (String split : splits) {
			ids.add(Integer.parseInt(split.trim()));
		}
		return ids;
	}
	
	public static HashSet<String> stringToStringSet(String line) {
		HashSet<String> ids = new HashSet<String>();
		line = line.substring(1, line.length() - 1);
		if (!line.contains(",")) {
			if (!line.equals(""))
				ids.add(line.trim());
			return ids;
		}
		String[] splits = line.split(",");
		
		for (String split : splits) {
			ids.add(split.trim());
		}
		return ids;
	}
	
	public static HashSet<String> turnStringToHashSetStringWithType(String content) {
		HashSet<String> elements = new HashSet<String>();
        if (content.equals("")) return elements;
        
        content = content.trim().substring(1, content.length() - 1);
		int index = 0;
		int nextIndex = content.indexOf(":");
		String element;
		while (true) {
			if (nextIndex < 0) 
				break;
			element = content.substring(index, nextIndex);
			content = content.substring(nextIndex);
			index = 0;
			nextIndex = content.indexOf(",");
			if (nextIndex < 0) {
				element += content.substring(index);
				elements.add(element.trim());
				break;
			} else {
				element += content.substring(index, nextIndex);
				elements.add(element.trim());
				content = content.substring(nextIndex);
				index = 1;
				nextIndex = content.indexOf(":");
			}
			
		}
		
		return elements;
	}
	
	public static HashSet<String> turnStringToHashSetString(String content) {
		HashSet<String> elements = new HashSet<String>();
		content = content.trim().substring(1, content.length() - 1);
		if (!content.contains(",")) {
			if (!content.equals(""))
				elements.add(content.trim());
			return elements;
		}
		String[] tmp = content.split(",");
		
//		System.out.println(content);
		int index = 0;
		while (index < tmp.length) {
			if (tmp[index].contains("<") && !tmp[index].contains(":")) {
				elements.add(tmp[index] + "," + tmp[index+1]);
				index = index + 2;
			}
			else {
				elements.add(tmp[index]);
				index++;
			}
		}
		return elements;
	}

	public static List<String> turnStringToListStringTab(String content) {
		List<String> elements = new ArrayList<String>();
		String[] split = content.split("\t");
		for (String tmp : split) {
			elements.add(tmp.trim());
		}
		return elements;
	}

	public static List<Integer> turnStringToListIntegerTab(String content) {
		List<Integer> elements = new ArrayList<Integer>();
		String[] split = content.split("\t");
		for (String tmp : split) {
			elements.add(Integer.parseInt(tmp.trim()));
		}
		return elements;
	}

	public static List<String> turnStringToListString(String content) {
		List<String> elements = new ArrayList<String>();
		if (content.equals("")) return elements;
        content = content.trim().substring(1, content.length() - 1);
		if (!content.contains(",")) {
			if (!content.equals(""))
				elements.add(content);
			return elements;
		}
		String[] tmp = content.split(",");
		
//		System.out.println(content);
		for (String element : tmp)
			elements.add(element.trim());
		return elements;
	}

	public static List<Double> normalize(List<Double> data) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (double d : data) {
			if (d > max)
				max = d;
			if (d < min)
				min = d;
		}

		List<Double> newData = new ArrayList<>();
		for (double d : data) {
			newData.add((d - min) / (max - min));
		}
		return newData;
	}


}
