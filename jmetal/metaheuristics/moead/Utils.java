/**
 * Utils.java
 * @author Antonio J. Nebro
 * @version 1.0
 * 
 * Description: utilities functions
 */
package jmetal.metaheuristics.moead;

import jmetal.core.SolutionSet;

/**
 *
 */
public class Utils {

	/**
	 * Calculate the distance between two vectors
	 * 
	 * @param vector1
	 * @param vector2
	 * @return distance of two vectors
	 */
	static double distVector(double[] vector1, double[] vector2) {
		int dim = vector1.length;
		double sum = 0;
		for (int n = 0; n < dim; n++) {
			sum += (vector1[n] - vector2[n]) * (vector1[n] - vector2[n]);
		}
		return Math.sqrt(sum);
	} // distVector

	/**
	 * Sort the array
	 * 
	 * @param x
	 * @param idx
	 * @param n
	 * @param m
	 */
	static void minFastSort(double x[], int idx[], int n, int m) {
		for (int i = 0; i < m; i++) {
			for (int j = i + 1; j < n; j++) {
				if (x[i] > x[j]) {
					double temp = x[i];
					x[i] = x[j];
					x[j] = temp;
					int id = idx[i];
					idx[i] = idx[j];
					idx[j] = id;
				} // if
			}
		} // for

	} // minFastSort

	/**
	 * Using a random sequence to permutation the array
	 * 
	 * @param perm
	 * @param size
	 */
	static void randomPermutation(int[] perm, int size) {
		int[] index = new int[size];
		boolean[] flag = new boolean[size];

		for (int n = 0; n < size; n++) {
			index[n] = n;
			flag[n] = true;
		}

		int num = 0;
		while (num < size) {
			int start = jmetal.util.PseudoRandom.randInt(0, size - 1);
			while (true) {
				if (flag[start]) {
					perm[num] = index[start];
					flag[start] = false;
					num++;
					break;
				}
				if (start == (size - 1)) {
					start = 0;
				} else {
					start++;
				}
			}
		} // while
	} // randomPermutation

	/**
	 * Using the bubble sort to rearrange the probability array in ascending
	 * order
	 * 
	 * @param selection_array
	 * @param num_strategies
	 */
	static void b_sort(double[] selection_array, int num_strategies) {
		int i, j;

		double temp;

		for (i = 0; i < (num_strategies - 1); i++) {
			for (j = 0; j < (num_strategies - (i + 1)); j++) {
				if (selection_array[j] < selection_array[j + 1]) {
					temp = selection_array[i];
					selection_array[i] = selection_array[j];
					selection_array[j] = temp;
				} // if
			} // for
		} // for
	} // b_sort

	/**
	 * Find the individual with the best fitness value
	 * 
	 * @param cur_pop
	 * @param size
	 * @return index of the best individual
	 */
	static int indexBest(SolutionSet cur_pop, int size) {
		int i;
		int best_index;

		best_index = 0;
		for (i = 1; i < size; i++) {
			if ((cur_pop.get(i)).getFitness() < (cur_pop.get(best_index))
					.getFitness()) {
				best_index = i;
			} // if
		} // for

		return best_index;
	} // indexBest

	/**
	 * Find the individual with the worst fitness value
	 * 
	 * @param cur_pop
	 * @param size
	 * @return index of the best individual
	 */
	static int indexWorst(SolutionSet cur_pop, int size) {
		int i;
		int worst_index;

		worst_index = 0;
		for (i = 1; i < size; i++) {
			if ((cur_pop.get(i)).getFitness() > (cur_pop.get(worst_index))
					.getFitness()) {
				worst_index = i;
			} // if
		} // for

		return worst_index;
	} // indexWorst

	/**
	 * Calculate the Arithmetic mean of the array's members
	 * 
	 * @param x
	 * @param size
	 * @return the arithmetic mean of them
	 */
	static double arithmeticMean(double[] x, int size) {
		int i;
		double median_sum, mean;

		median_sum = 0.0;
		for (i = 0; i < size; i++) {
			median_sum += x[i];
		}

		mean = median_sum / size;

		return mean;
	}

	/**
	 * Calculate the Root-Mean-Square of the array's members
	 * 
	 * @param x
	 * @param size
	 * @return the Root-Mean-Square of them
	 */
	static double rootMeanSquare(double[] x, int size) {
		int i;
		double median_sum, mean;

		median_sum = 0.0;
		for (i = 0; i < size; i++) {
			median_sum += x[i] * x[i];
		}
		mean = Math.sqrt(median_sum / size);

		return mean;
	}

	/**
	 * Calculate the Lehmer Mean of the array's members
	 * 
	 * @param x
	 * @param size
	 * @return the Lehmer Mean of them
	 */
	static double lehmerMean(double[] x, int size) {
		int i;
		double median_sum, median_squared_sum, mean;

		median_sum = 0.0;
		median_squared_sum = 0;
		for (i = 0; i < size; i++) {
			median_sum += x[i];
		}
		for (i = 0; i < size; i++) {
			median_squared_sum += x[i] * x[i];
		}
		mean = median_squared_sum / median_sum;

		return mean;
	}
}
