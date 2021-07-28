package org.capgen.algorithm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Algorithm {

	public static List<String> transfer(List<String> tmpContexts) {
		List<String> contexts = new ArrayList<String>();
		
		for (String context : tmpContexts) {
			if (context.endsWith("CtIfImpl") || context.endsWith("CtSwitchImpl") || context.endsWith("CtCaseImpl") || context.endsWith("CtDoImpl"))
				contexts.add("CtConditionImpl");
			else contexts.add(context);
		}
		return contexts;
	}

	public static double Cosine(List<Integer> A, List<Integer> B) {
		double cosine = 0;
		int sum = 0;
		int sumA = 0;
		int sumB = 0;
		for (int i = 0; i < A.size(); i++) {
			sum += A.get(i) * B.get(i);
			sumA += A.get(i) * A.get(i);
			sumB += B.get(i) * B.get(i);
		}
		if (sumA == 0 && sumB == 0) return 1;
		if (sumA == 0 || sumB == 0) return 0;
		cosine = sum * 1.0 / Math.sqrt(sumA * sumB);
		return cosine;
	}

	public static double


	Jaccard(List<String> A, List<String> B) {
		if (A.size() == 0 && B.size() == 0) return 1;
		A = transfer(A);
		B = transfer(B);

		HashMap<String, Integer> AA = new HashMap<String, Integer>();
		HashSet<String> elements = new HashSet<String>();
		for (String t : A) {
			if (!AA.containsKey(t))
				AA.put(t, 1);
			else AA.put(t, AA.get(t) + 1);
			elements.add(t);
		}
		HashMap<String, Integer> BB = new HashMap<String, Integer>();
		for (String t : B) {
			if (!BB.containsKey(t))
				BB.put(t, 1);
			else BB.put(t, BB.get(t) + 1);
			elements.add(t);
		}
		int aa = 0;



		int bb = 0;
		for (String t : elements) {
			int ta = AA.containsKey(t) ? AA.get(t) : 0;
			int tb = BB.containsKey(t) ? BB.get(t) : 0;
			aa += Math.min(ta, tb);
			bb += Math.max(ta, tb);
		}
		if (aa == 0 && bb == 0) return 1;
		if (bb == 0) return 0;
		else return aa * 1.0 / bb;
	}
	
	public static double Jaccard2(List<String> A, List<String> B) {
		A = transfer(A);
		B = transfer(B);
		HashMap<String, Integer> AA = new HashMap<String, Integer>();
		HashMap<String, Double> AFreq = new HashMap<String, Double>();
		HashSet<String> elements = new HashSet<String>();
		
		for (String t : A) {
			if (!AA.containsKey(t))
				AA.put(t, 1);
			else AA.put(t, AA.get(t) + 1);
			elements.add(t);
			
		}
		
		for (String t : AA.keySet()) {
			AFreq.put(t, AA.get(t) * 1.0 / A.size());
		}
		
		HashMap<String, Integer> BB = new HashMap<String, Integer>();
		HashMap<String, Double> BFreq = new HashMap<String, Double>();
		for (String t : B) {
			if (!BB.containsKey(t))
				BB.put(t, 1);
			else BB.put(t, BB.get(t) + 1);
			elements.add(t);
		}
		
		for (String t : BB.keySet()) {
			BFreq.put(t, BB.get(t) * 1.0 / B.size());
		}
		
		double aa = 0;
		for (String t : AA.keySet()) {
			
			double tb = BB.containsKey(t) ? BB.get(t) : 0;
			aa += AFreq.get(t) * tb;
		}
		
		return aa;
//		if (A.size() == 0)
//			return 0;
//		else return aa / A.size();
	}
	
	public static double SimpleJaccard(HashSet<String> A, HashSet<String> B) {
		HashSet<String> C = new HashSet<String>(A);
		HashSet<String> D = new HashSet<String>(A);
		C.retainAll(B);
		D.addAll(B);
		
		return C.size() * 1.0 / D.size();
	}
	
	public static double SimpleJaccard(List<String> A, List<String> B) {
		HashSet<String> C = new HashSet<String>(A);
		HashSet<String> D = new HashSet<String>(A);
		C.retainAll(B);
		D.addAll(B);
		
		return C.size() * 1.0 / D.size();
	}
	
	
	public static double Jaccard(HashSet<String> A, HashSet<String> B) {
		
		HashMap<String, Integer> AA = new HashMap<String, Integer>();
		HashSet<String> elements = new HashSet<String>();
		for (String t : A) {
			if (!AA.containsKey(t))
				AA.put(t, 1);
			else AA.put(t, AA.get(t) + 1);
			elements.add(t);
		}
		HashMap<String, Integer> BB = new HashMap<String, Integer>();
		for (String t : B) {
			if (!BB.containsKey(t))
				BB.put(t, 1);
			else BB.put(t, BB.get(t) + 1);
			elements.add(t);
		}
		int aa = 0;
		int bb = 0;
		for (String t : elements) {
			int ta = AA.containsKey(t) ? AA.get(t) : 0;
			int tb = BB.containsKey(t) ? BB.get(t) : 0;
			aa += Math.min(ta, tb);
			bb += tb;
		}
		if (bb == 0) return 0;
		else return aa * 1.0 / bb;
	}
	
	/**
	 * 
	 * @param A the target context
	 * @param B the source context
	 * @return
	 */

	public static double compareListSimi(List<Integer> A, List<Integer> B) {

		double simi;
		int sum = 0;
		int count = 0;
		for (int i = 0; i < A.size(); i++) {
			sum += Math.max(A.get(i), B.get(i));
			count += Math.min(A.get(i), B.get(i));
		}

		if (sum == 0) return 0;
		simi = count * 1.0 / sum;
		return simi;
	}

	public static int getStringEditDistance(List<String> left, List<String> right) {
		if (left.size() == 0 || right.size() == 0) return 1000;
		int[][] dp = new int[left.size() + 1][right.size() + 1];

		for (int i = 0; i <= left.size(); i++)
			dp[i][0] = i;
		for (int j = 0; j <= right.size(); j++)
			dp[0][j] = j;

		for (int i = 0; i < left.size(); i++) {
			for (int j = 0; j < right.size(); j++) {
				if (left.get(i).equals(right.get(j))) {
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;
					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;

					dp[i + 1][j + 1] = min;

				}
			}
		}

		return dp[left.size()][right.size()];
	}

	public static double countNum(List<String> A, List<String> B) {
		
		HashMap<String, Integer> AA = new HashMap<String, Integer>();
		HashSet<String> elements = new HashSet<String>();
		for (String t : A) {
			if (!AA.containsKey(t))
				AA.put(t, 1);
			else AA.put(t, AA.get(t) + 1);
			elements.add(t);
		}
		HashMap<String, Integer> BB = new HashMap<String, Integer>();
		for (String t : B) {
			if (!BB.containsKey(t))
				BB.put(t, 1);
			else BB.put(t, BB.get(t) + 1);
			elements.add(t);
		}
		int aa = 0;
		int bb = 0;
		for (String context : AA.keySet()) {
			aa += AA.get(context);
			if (BB.containsKey(context))
				bb += BB.get(context);
		}
		if (aa == 0) return 0;
		return bb * 1.0 / aa;
	}
}
