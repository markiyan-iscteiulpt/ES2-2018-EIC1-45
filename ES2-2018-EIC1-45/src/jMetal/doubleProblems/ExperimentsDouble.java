package jMetal.doubleProblems;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3Builder;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEABuilder;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder.Variant;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.paes.PAESBuilder;
import org.uma.jmetal.algorithm.multiobjective.randomsearch.RandomSearchBuilder;
import org.uma.jmetal.algorithm.multiobjective.smsemoa.SMSEMOABuilder;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.*;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

import jMetal.AbstractExperiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles Double experiments.
 * 
 * @author vbasto-iscte and pvmpa-iscteiulpt
 *
 */
public class ExperimentsDouble extends AbstractExperiment {
	private static int INDEPENDENT_RUNS = 5;
	private static int maxEvaluations = 500;

	/**
	 * Executes the experiment.
	 * 
	 * @throws IOException
	 */
	public void execute() throws IOException {
		List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
		if (isJar()) {
			INDEPENDENT_RUNS = 2;
			maxEvaluations = 250;
			problemList.add(new ExperimentProblem<>(new MyProblemDouble(getLimits_Double(), getNumber_of_objectives(),
					isJar(), getJarPath(), getProblemName(), getTimelimit())));
		} else {
			INDEPENDENT_RUNS = 5;
			maxEvaluations = 500;
			problemList.add(new ExperimentProblem<>(new MyProblemDouble(getLimits_Double(), getNumber_of_objectives(),
					isJar(), null, getProblemName(), getTimelimit())));
		}

		String experimentBaseDirectory = "experimentBaseDirectory";

		List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList = configureAlgorithmList(
				problemList, getAlgorithm());

		Experiment<DoubleSolution, List<DoubleSolution>> experiment = new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>(
				"ExperimentsDouble").setAlgorithmList(algorithmList).setProblemList(problemList)
						.setExperimentBaseDirectory(experimentBaseDirectory).setOutputParetoFrontFileName("FUN")
						.setOutputParetoSetFileName("VAR")
						.setReferenceFrontDirectory(experimentBaseDirectory + "/referenceFronts")
						.setIndicatorList(Arrays.asList(new PISAHypervolume<DoubleSolution>()))
						.setIndependentRuns(INDEPENDENT_RUNS).setNumberOfCores(8).build();

		new ExecuteAlgorithms<>(experiment).run();
		new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
		new ComputeQualityIndicators<>(experiment).run();
		new GenerateLatexTablesWithStatistics(experiment).run();
		new GenerateBoxplotsWithR<>(experiment).setRows(1).setColumns(1).run();

		generateDocuments(DEFAULT_R_PATH, DEFAULT_LATEX_PATH, this);
	}

	/**
	 * Configures the algorithm list, taking a single algorithm as its second
	 * parameter.
	 * 
	 * @param problemList
	 * @param algo
	 * @return algorithm
	 */
	private List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(
			List<ExperimentProblem<DoubleSolution>> problemList, String algo) {
		List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();

		for (int i = 0; i < problemList.size(); i++) {
			switch (algo) {
			case "NSGAII":
				Algorithm<List<DoubleSolution>> algorithm1 = new NSGAIIBuilder<>(problemList.get(i).getProblem(),
						new SBXCrossover(1.0, 5),
						new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 10.0))
								.setMaxEvaluations(maxEvaluations).setPopulationSize(100).build();
				algorithms.add(new ExperimentAlgorithm<>(algorithm1, "NSGAII", problemList.get(i).getTag()));
				break;
			case "SMSEMOA":
				Algorithm<List<DoubleSolution>> algorithm2 = new SMSEMOABuilder<>(problemList.get(i).getProblem(),
						new SBXCrossover(1.0, 5),
						new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 10.0))
								.setMaxEvaluations(maxEvaluations).build();
				algorithms.add(new ExperimentAlgorithm<>(algorithm2, "SMSEMOA", problemList.get(i).getTag()));
				break;
			case "GDE3":
				Algorithm<List<DoubleSolution>> algorithm3 = new GDE3Builder(
						(DoubleProblem) problemList.get(i).getProblem()).setMaxEvaluations(maxEvaluations).build();
				algorithms.add(new ExperimentAlgorithm<>(algorithm3, "GDE3", problemList.get(i).getTag()));
				break;
			case "IBEA":
				Algorithm<List<DoubleSolution>> algorithm4 = new IBEABuilder(problemList.get(i).getProblem())
						.setMaxEvaluations(maxEvaluations).build();
				algorithms.add(new ExperimentAlgorithm<>(algorithm4, "IBEA", problemList.get(i).getTag()));
				break;
			case "MOCell":
				Algorithm<List<DoubleSolution>> algorithm5 = new MOCellBuilder<>(problemList.get(i).getProblem(),
						new SBXCrossover(1.0, 5),
						new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 10.0))
								.setMaxEvaluations(maxEvaluations).build();
				algorithms.add(new ExperimentAlgorithm<>(algorithm5, "MOCell", problemList.get(i).getTag()));
				break;
			case "MOEAD":
				Algorithm<List<DoubleSolution>> algorithm6 = new MOEADBuilder(problemList.get(i).getProblem(),
						Variant.MOEAD).setMaxEvaluations(maxEvaluations).build();
				algorithms.add(new ExperimentAlgorithm<>(algorithm6, "MOEAD", problemList.get(i).getTag()));
				break;
			case "PAES":
				Algorithm<List<DoubleSolution>> algorithm7 = new PAESBuilder<>(problemList.get(i).getProblem())
						.setMaxEvaluations(maxEvaluations).setArchiveSize(100).setBiSections(2)
						.setMutationOperator(new PolynomialMutation(
								1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 10.0))
						.build();
				algorithms.add(new ExperimentAlgorithm<>(algorithm7, "PAES", problemList.get(i).getTag()));
				break;
			case "RandomSearch":
				Algorithm<List<DoubleSolution>> algorithm8 = new RandomSearchBuilder<>(problemList.get(i).getProblem())
						.setMaxEvaluations(maxEvaluations).build();
				algorithms.add(new ExperimentAlgorithm<>(algorithm8, "RandomSearch", problemList.get(i).getTag()));
				break;
			default:
				throw new IllegalStateException(
						"This has got to stop. How did algorithm parsing succeed until the Experiment?");
			}

		}
		return algorithms;
	}

}
