//  SMPSOStudy.java
//
//  Author:
//       Gian M. Fritsche
////
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.experiments.studies;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Experiment;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.MOEADDRA_MAB_Settings;
import jmetal.problems.DTLZ.DTLZ1;
import jmetal.problems.DTLZ.DTLZ2;
import jmetal.problems.DTLZ.DTLZ3;
import jmetal.problems.DTLZ.DTLZ4;
import jmetal.problems.DTLZ.DTLZ5;
import jmetal.problems.DTLZ.DTLZ6;
import jmetal.problems.DTLZ.DTLZ7;
import jmetal.problems.WFG.WFG1;
import jmetal.problems.WFG.WFG2;
import jmetal.problems.WFG.WFG3;
import jmetal.problems.WFG.WFG4;
import jmetal.problems.WFG.WFG5;
import jmetal.problems.WFG.WFG6;
import jmetal.problems.WFG.WFG7;
import jmetal.problems.WFG.WFG8;
import jmetal.problems.WFG.WFG9;
import jmetal.util.JMException;

public class MainStudy extends Experiment {

	int size;
	int iteration;
	// < Indicator < Problem < Algorithm , values > > >
	HashMap<String, HashMap<String, HashMap<String, double[]>>> data;
	// < Indicator < Problem < Algorithm < Algorithm, isDiff > > > >
	HashMap<String, HashMap<String, HashMap<String, HashMap<String, Boolean>>>> statisticalData;

	public Algorithm getAlgorithm(String option, Problem problem, String output)
			throws ClassNotFoundException, JMException {

		if (option.equals("MOEADDRAMAB")) {
			return (new MOEADDRA_MAB_Settings(problem, size, iteration)).configure();
		} 

		throw new ClassNotFoundException(option);
	}

	/**
	 * Configures the algorithms in each independent run
	 * 
	 * @param problemName
	 *            The problem to solve
	 * @param problemIndex
	 * @param algorithm
	 *            Array containing the algorithms to run
	 * @throws ClassNotFoundException
	 */
	public synchronized void algorithmSettings(String problemName, int problemIndex, Algorithm[] algorithm)
			throws ClassNotFoundException {

		int numberOfAlgorithms = algorithmNameList_.length;

		Problem problem = null;
		String type = "Real";

		if (problemName.equals("DTLZ1")) {
			problem = new DTLZ1(type, m + 4, m);
		} else if (problemName.equals("DTLZ7")) {
			problem = new DTLZ7(type, m + 19, m);
		} else if (problemName.equals("DTLZ2")) {
			problem = new DTLZ2(type, m + 9, m);
		} else if (problemName.equals("DTLZ3")) {
			problem = new DTLZ3(type, m + 9, m);
		} else if (problemName.equals("DTLZ4")) {
			problem = new DTLZ4(type, m + 9, m);
		} else if (problemName.equals("DTLZ5")) {
			problem = new DTLZ5(type, m + 9, m);
		} else if (problemName.equals("DTLZ6")) {
			problem = new DTLZ6(type, m + 9, m);
		} else if (problemName.equals("WFG1")) {
			problem = new WFG1(type, (2 * (m - 1)), 20, m);
		} else if (problemName.equals("WFG2")) {
			problem = new WFG2(type, (2 * (m - 1)), 20, m);
		} else if (problemName.equals("WFG3")) {
			problem = new WFG3(type, (2 * (m - 1)), 20, m);
		} else if (problemName.equals("WFG4")) {
			problem = new WFG4(type, (2 * (m - 1)), 20, m);
		} else if (problemName.equals("WFG5")) {
			problem = new WFG5(type, (2 * (m - 1)), 20, m);
		} else if (problemName.equals("WFG6")) {
			problem = new WFG6(type, (2 * (m - 1)), 20, m);
		} else if (problemName.equals("WFG7")) {
			problem = new WFG7(type, (2 * (m - 1)), 20, m);
		} else if (problemName.equals("WFG8")) {
			problem = new WFG8(type, (2 * (m - 1)), 20, m);
		} else if (problemName.equals("WFG9")) {
			problem = new WFG9(type, (2 * (m - 1)), 20, m);
		} else {
			throw new ClassNotFoundException(problemName);
		}

		try {
			for (int i = 0; i < numberOfAlgorithms; ++i) {
				algorithm[i] = getAlgorithm(algorithmNameList_[i], problem, algorithmOutputName_[i]);
			}
		} catch (JMException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	} // algorithmSettings

	public static void main(String[] args) throws JMException, IOException {

		int iterations[];
		int sizes[];
		int mm[];
		int runs;
		String[] problemList = null;
		String[] paretoFrontFile = null;
		String[] algorithmsToRun = null;
		String[] algorithmsOutputName = null;

		String experimentName = "SMPSOStudy";

		boolean loadFromFile = args.length > 1;
		String file = args[1];
		Properties configuration = new Properties();

		if (loadFromFile) {
			try {
				InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
				configuration.load(isr);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// iterations
			String aux = configuration.getProperty("iterations");
			iterations = new int[1];
			if (aux != null)
				iterations[0] = Integer.parseInt(aux);
			else
				iterations[0] = 100;
			System.out.println("Iterations: " + iterations[0]);
			// size
			aux = configuration.getProperty("size");
			sizes = new int[1];
			if (aux != null)
				sizes[0] = Integer.parseInt(aux);
			else
				sizes[0] = 100;
			System.out.println("Population and Archive Size: " + sizes[0]);
			// number of objectives
			aux = configuration.getProperty("m");
			if (aux != null) {
				ArrayList<Integer> mmlist = new ArrayList<Integer>();
				for (String s : aux.split(",")) {
					mmlist.add(Integer.parseInt(s));
				}
				mm = new int[mmlist.size()];
				for (int i = 0; i < mm.length; i++) {
					mm[i] = mmlist.get(i).intValue();
				}
			} else {
				mm = new int[1];
				mm[0] = 2;
			}
			System.out.println("Number of Objectives: " + mm[0]);
			// experiment name
			aux = configuration.getProperty("experiment");
			if (aux != null) {
				experimentName = aux;
			}
			// independent runs
			aux = configuration.getProperty("runs");
			runs = 30;
			if (aux != null)
				runs = Integer.parseInt(aux);
			System.out.println("Independent runs: " + runs);
			// problem list
			aux = configuration.getProperty("problems");
			problemList = new String[] { "DTLZ1" };
			if (aux != null)
				problemList = aux.split(",");
			// pareto front files
			aux = configuration.getProperty("fronts");
			paretoFrontFile = new String[] { (problemList[0] + "_" + Integer.toString(mm[0]) + ".ref") };
			if (aux != null)
				paretoFrontFile = aux.split(",");
			// algorithm to run
			aux = configuration.getProperty("run");
			algorithmsToRun = new String[] { "ACFFIXED" };
			if (aux != null)
				algorithmsToRun = aux.split(",");
			// algorithms output name
			aux = configuration.getProperty("output");
			algorithmsOutputName = algorithmsToRun;
			if (aux != null)
				algorithmsOutputName = aux.split(",");
			// algorithms to evaluate
			aux = configuration.getProperty("evaluate");
			if (aux != null) {
			}
			// indicators list
			aux = configuration.getProperty("indicators");

		} else {

			//// ACF Scale Factor (SF) configuration
			iterations = new int[] { 100, 150, 250, 400, 500, 750, 1000 };
			sizes = new int[] { 97, 91, 210, 156, 275, 135, 230 };
			mm = new int[] { 2, 3, 5, 8, 10, 15, 20 };
			runs = 10;

		}

		for (int i = 0; i < mm.length; ++i) {

			int m = mm[i];

			String sm = Integer.toString(m);

			if (!loadFromFile) {
				// BEGIN SETTINGS

				// String indicatorList_ = new String[] {};
				// String[] indicatorList_ = new String[] {"HV", "IGD",
				// "R2"};
				// String[] problemList = new String[] {"WFG1", "WFG2",
				// "WFG3", "WFG4", "WFG5", "WFG6", "WFG7", "WFG8", "WFG9"};
				// String[] paretoFrontFile = new String[]
				// {("WFG1_"+sm+".ref"), ("WFG2_"+sm+".ref"),
				// ("WFG3_"+sm+".ref"), ("WFG4_"+sm+".ref"),
				// ("WFG5_"+sm+".ref"), ("WFG6_"+sm+".ref"),
				// ("WFG7_"+sm+".ref"), ("WFG8_"+sm+".ref"),
				// ("WFG9_"+sm+".ref")} ;
				// String[] algorithmsToRun = new String[] {"ACFFIXED",
				// "MOEADDRA"} ;
				// String[] algorithmsToEvaluate = new String[] {"ACFFIXED",
				// "MOEADDRA"} ;
				// String[] algorithmsToRun = new String[] {"ACFFIXED",
				// "RANDOMFIXED", "CDCD", "MGACD", "IDEALCD", "DDCD"} ;
				// String[] algorithmsToEvaluate = new String[] {"ACFFIXED",
				// "RANDOMFIXED", "CDCD", "MGACD", "IDEALCD", "DDCD"} ;

				// ACF Scale Factor (SF) configuration
				problemList = new String[] { "DTLZ1", "DTLZ2", "DTLZ3", "DTLZ4", "DTLZ5", "DTLZ6", "DTLZ7" };
				paretoFrontFile = new String[] { ("DTLZ1_" + sm + ".ref"), ("DTLZ2-4_" + sm + ".ref"),
						("DTLZ2-4_" + sm + ".ref"), ("DTLZ2-4_" + sm + ".ref"), ("DTLZ2-4_" + sm + ".ref"),
						("DTLZ2-4_" + sm + ".ref"), ("DTLZ2-4_" + sm + ".ref") };
				algorithmsToRun = new String[] { "ACFFIXED" };
				algorithmsOutputName = new String[] { "ACFFIXED" };


			} // END SETTINGS

			MainStudy exp = new MainStudy();

			exp.size = sizes[i];
			exp.iteration = iterations[i];
			exp.m = m;

			exp.experimentName_ = experimentName;

			// exp.algorithmNameList_ = new String[] {"RANDOMRANDOM",
			// "CDCD", "CDNWSUM", "CDSIGMA", "DDCD", "DDNWSUM", "DDSIGMA" ,
			// "IDEALCD", "IDEALNWSUM", "IDEALSIGMA" , "MGACD", "MGANWSUM",
			// "MGASIGMA"} ;
			// exp.algorithmNameList_ = new String[] {"CDCD", "DDCD",
			// "IDEALCD", "MGACD"} ;
			exp.algorithmNameList_ = algorithmsToRun;
			exp.algorithmOutputName_ = algorithmsOutputName;

			//// ALL PROBLEMS
			// exp.problemList_ = new String[] {
			// "DTLZ1", "DTLZ2", "DTLZ3", "DTLZ4" //};
			// , "DTLZ5", "DTLZ6", "DTLZ7" // };
			// ,"WFG1", "WFG2", "WFG3", "WFG4", "WFG5", "WFG6", "WFG7",
			//// "WFG8", "WFG9"} ;

			//// R2 and HV
			// exp.problemList_ = new String[] {
			// "DTLZ1", "DTLZ2", "DTLZ3", "DTLZ4", "DTLZ5", "DTLZ6",
			//// "DTLZ7"};
			// exp.paretoFrontFile_ = new String[] {("DTLZ1_"+sm+".ref"),
			// ("DTLZ2-4_"+sm+".ref"), ("DTLZ2-4_"+sm+".ref"),
			//// ("DTLZ2-4_"+sm+".ref")
			// , ("DTLZ2-4_"+sm+".ref"), ("DTLZ2-4_s"+sm+".ref"),
			//// ("DTLZ2-4_"+sm+".ref") } ;

			// ACF Analysis
			exp.problemList_ = problemList;
			exp.paretoFrontFile_ = paretoFrontFile;

			//// IGD
			// exp.problemList_ = new String[] {"DTLZ1", "DTLZ2", "DTLZ3",
			//// "DTLZ4"};
			// exp.paretoFrontFile_ = new String[] {("DTLZ1_"+sm+".ref"),
			// ("DTLZ2-4_"+sm+".ref"), ("DTLZ2-4_"+sm+".ref"),
			//// ("DTLZ2-4_"+sm+".ref")} ;

			// exp.indicatorList_ = new String[] {"IGD"};
			// exp.indicatorList_ = new String[] {"HV"};
			// exp.indicatorList_ = new String[] {"R2"};

			int numberOfAlgorithms = exp.algorithmNameList_.length;

			exp.experimentBaseDirectory_ = exp.experimentName_ + "/" + sm;
			exp.paretoFrontDirectory_ = "referencePoints";

			exp.algorithmSettings_ = new Settings[numberOfAlgorithms];

			exp.independentRuns_ = runs;

			exp.initExperiment();

			// Run the experiments
			// int numberOfThreads = 1;
			int numberOfThreads = Runtime.getRuntime().availableProcessors() - 1;
			if ((args.length > 0 && args[0].contains("r")) || (args.length == 0)) { // if
																					// contains
																					// run
																					// (r)
																					// or
																					// default
				exp.runExperiment(numberOfThreads);
			}

		}
	}

}
