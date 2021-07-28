package org.capgen.algorithm;

import util.FileToLines;
import util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SuspiciousValue {
	public static int getLineGap(HashSet<Integer> lines, int a, int b) {
		int count = 0;
		if (a > b) {
			count = b;
			b = a;
			a = count;
		}
		count = 0;
		for (int l = a; l < b; l++) {
			if (lines.contains(l)) continue;
			count++;
		}
		return count;
	}
	
	public static double getSuspiciousValue(HashSet<Integer> nums, HashSet<Integer> lines, int index, int size) {
		int min = Integer.MAX_VALUE;
		for (int num : nums) {
			int gap = getLineGap(lines, num, index);
			if (gap < min)
				min = gap;
		}
		return 1.0 - min * 1.0 / size * 10;
	}
	
	public static double getSuspiciousValue2(HashSet<Integer> nums, HashSet<Integer> lines, int index, int size) {
		int min = Integer.MAX_VALUE;
		for (int num : nums) {
			int gap = getLineGap(lines, num, index);
			if (gap < min)
				min = gap;
		}
		return 1.0 - min * 1.0 / size;
	}
	
	public static Pair<Double,Double> getFSAccuracy(String filename, HashMap<String, HashSet<Integer>> realFixes) {
		List<String> lines = FileToLines.fileToLines(filename);
		List<Integer> ranks = new ArrayList<Integer>();
		int index = 0;
		for (String line : lines) {
			String[] split = line.split("\t");
			String className = split[0];
			int num = Integer.parseInt(split[2]);
			if (realFixes.containsKey(className) && realFixes.get(className).contains(num))
				ranks.add(index);
			index++;
		}
		System.out.println(ranks.toString());
		if (ranks.size() == 0) return new Pair<Double,Double>(0.0,0.0);
		else return new Pair<Double, Double>(EvaluationMetric.AP(ranks), EvaluationMetric.RR(ranks));
	}
}
