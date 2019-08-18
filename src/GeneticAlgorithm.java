import java.util.Iterator;

public class GeneticAlgorithm {
	private static double mutationRate = 0.015;
	private static int tournamentSize = 5;
	private static boolean elitism = false;
	private static Iterator<DataPoint> learningChart;
	private static int trainingLine;
	private static int sizeStudy=Main.sizeStudy;
	
	//Evolves a population by creating crossovers of the parents and mutating them
	public static Population evolve(Population pop, Iterator<DataPoint> learningChart, int trainingPine){
		trainingLine=trainingPine;
		Population newPopulation = new Population(pop.getSize(), learningChart, trainingLine);
		int elitismOffset = 0;
		if(elitism){
			newPopulation.saveStrat(0, pop.getFittest());
			pop.getFittest().reset();
			elitismOffset = 1;
		}
		
		// Crossover population
        // Loop over the new population's size and create individuals from
        // Current population
		for(int i = elitismOffset; i < newPopulation.getSize();i++){
			TradingStrategy parentOne = tournamentSelection(pop);
			TradingStrategy parentTwo = tournamentSelection(pop);
			
			TradingStrategy child = crossover(parentOne, parentTwo);
			newPopulation.saveStrat(i, child);
		}
		
		for(int i = elitismOffset; i < newPopulation.getSize();i++){
			mutate(newPopulation.getStrat(i));
		}
		
		return newPopulation;
	}
	
	//Creates a cross over child of the two parents
	public static TradingStrategy crossover(TradingStrategy parent1, TradingStrategy parent2){
		TradingStrategy child = new TradingStrategy();
		for(int i=0;i<sizeStudy;i++){
			int parent1Gene = parent1.getGene(i);
			int parent2Gene = parent2.getGene(i);
			if(parent1Gene==parent2Gene){
				child.setGene(i, parent1Gene);
			}
			else{
				double probs = Math.random();
				if(probs>=0.5){
					child.setGene(i, parent1Gene);
				}
				else{
					child.setGene(i, parent2Gene);
				}
			}
		}
		child.setProbs((parent1.getProbs()+parent2.getProbs())/2);
		return child;
	}
	
	//Mutates trading strategies based on the mutation rate
	private static void mutate(TradingStrategy strat){
		for(int i=0;i<sizeStudy;i++){
			if(Math.random()<=mutationRate){
				if(strat.getGene(i)==1){
					strat.setGene(i, 0);
				}
				else{
					strat.setGene(i, 1);
				}
				strat.setProbs(Math.random());
			}
		}
	}
	
	//Selects a group of random trading strategies from the population to return the fittest
	private static TradingStrategy tournamentSelection(Population pop){
		Population tournament = new Population(tournamentSize, learningChart, trainingLine);
		for(int i=0;i<tournamentSize;i++){
			int randomID = (int)(Math.random()*pop.getSize());
			tournament.saveStrat(i, pop.getStrat(randomID));
		}
		
		TradingStrategy fittest = tournament.getFittest();
		return fittest;
	}
}