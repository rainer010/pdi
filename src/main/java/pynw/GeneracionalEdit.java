package pynw;


import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by rainer on 31/01/2017.
 */


public class GeneracionalEdit<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, S> {
    private Comparator<S> comparator;
    private int maxEvaluations;
    private int populationSize;
    private int evaluations;
    private Problem<S> problem;
    private SolutionListEvaluator<S> evaluator;

    public GeneracionalEdit(Problem<S> problem, int maxEvaluations, int populationSize, CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator) {
        this.problem = problem;
        this.maxEvaluations = maxEvaluations;
        this.populationSize = populationSize;
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.selectionOperator = selectionOperator;
        this.evaluator = evaluator;
        this.comparator = new ObjectiveComparator(0, ObjectiveComparator.Ordering.DESCENDING);

    }

    protected boolean isStoppingConditionReached() {
        return this.evaluations >= this.maxEvaluations;
    }

    protected List<S> createInitialPopulation() {
        ArrayList population = new ArrayList(this.populationSize);

        for (int i = 0; i < this.populationSize; ++i) {
            Solution newIndividual = this.problem.createSolution();
            population.add(newIndividual);
        }

        return population;
    }

    protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
        Collections.sort(population, this.comparator);
        offspringPopulation.add(population.get(0));
        offspringPopulation.add(population.get(1));
        Collections.sort(offspringPopulation, this.comparator);
        offspringPopulation.remove(offspringPopulation.size() - 1);
        offspringPopulation.remove(offspringPopulation.size() - 1);
        return offspringPopulation;
    }

    protected List<S> reproduction(List<S> matingPopulation) {
        ArrayList offspringPopulation = new ArrayList(matingPopulation.size() + 2);

        for (int i = 0; i < this.populationSize; i += 2) {
            ArrayList parents = new ArrayList(2);
            parents.add(matingPopulation.get(i));
            parents.add(matingPopulation.get(i + 1));
            List offspring = (List) this.crossoverOperator.execute(parents);
            this.mutationOperator.execute((S) offspring.get(0));
            this.mutationOperator.execute((S) offspring.get(1));
            offspringPopulation.add(offspring.get(0));
            offspringPopulation.add(offspring.get(1));
        }

        return offspringPopulation;
    }

    protected List<S> selection(List<S> population) {
        ArrayList matingPopulation = new ArrayList(population.size());

        for (int i = 0; i < this.populationSize; ++i) {
            Solution solution = (Solution) this.selectionOperator.execute(population);
            matingPopulation.add(solution);
        }

        return matingPopulation;
    }

    protected List<S> evaluatePopulation(List<S> population) {
        population = this.evaluator.evaluate(population, this.problem);
        return population;
    }

    public S getResult() {
        Collections.sort(this.getPopulation(), this.comparator);
        return (S) this.getPopulation().get(0);
    }

    public void initProgress() {
        this.evaluations = this.populationSize;
    }

    public void updateProgress() {
        this.evaluations += this.populationSize;
    }
}
