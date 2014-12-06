package utils;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;




public abstract class GeneticAlg<T> {
	
	public class EvaluatedGene implements Comparable<EvaluatedGene> { 
		
		public List<T> value;
		public double fitness;
		public double selectionProbability;
		
		
		@Override
		public int compareTo(EvaluatedGene input) {
			
			return Double.compare(this.fitness, input.fitness) ; // smallest item first
		}
		
		
	}
	
	abstract public double fitnessFunction(List<T> input);
	
	public List<EvaluatedGene> selection(List<List<T>> inputList){
		
		List<EvaluatedGene> resultList = new ArrayList<EvaluatedGene>();
		List<EvaluatedGene> tempList = new ArrayList<EvaluatedGene>();
		double fitnessSum=0;
		
		for(int i=0; i < inputList.size(); i++ ) {
			
			EvaluatedGene eg = new EvaluatedGene();
			eg.value = inputList.get(i);
			eg.fitness = fitnessFunction(inputList.get(i));
			fitnessSum += eg.fitness;
			tempList.add(eg);
		}
		
		Collections.sort(tempList);
		
		
		for( EvaluatedGene eg : tempList) {
			
			eg.selectionProbability = (eg.fitness/fitnessSum);
			
		}
		
		
		Random r = new Random();
		
		for(int i=0; i<tempList.size();i++) {
			
			double cummulatedProbability=0;
			double randomValue = r.nextDouble();
			for(EvaluatedGene eg : tempList) {
				
				
				cummulatedProbability += eg.selectionProbability;
				
				if(randomValue < cummulatedProbability) {
					resultList.add(eg);
					break;
				}
		}
		
			
			
			
		}
		
		
		return resultList;
	}

	
	public abstract void onePointCrossOver(List<T> a, List<T> b);
	public abstract void mutate(T input);
	
	public List<EvaluatedGene> crossOver(List<EvaluatedGene> inputList) {
		
		
		
		for(int i=0 ; i<inputList.size(); i=i+2) {
			
			
			
			 onePointCrossOver(inputList.get(i).value, inputList.get(i+1).value);		
			
		}
		return inputList;
	}

	
	public List<EvaluatedGene> mutation(List<EvaluatedGene> inputList) {
		
		for(EvaluatedGene eg : inputList) {
			mutate(eg.value.get(RandGenerator.randInt(0, eg.value.size()-1 )));
		}
		
		return inputList;
	}
		
		
	
		
		
		
		
		
	}
	
	


