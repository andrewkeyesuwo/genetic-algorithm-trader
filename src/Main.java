import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class Main {
	public static int sizeStudy=25;

	public static void main(String[] args) throws IOException, InvalidFormatException {
		int populationSize = 50;
		int numEvolutions = 500;
		int trainingLine = 16359;
		int popSizeTwo = 10;
		int numRuns = 20;
		float[] returns = new float[numRuns];
		String fileName = "SandPData.txt";
		
		float difference = 0;
		
		for(int k = 0;k<numRuns;k++){
			System.out.println("------------------------------------ "+k+" -----------------------------------");
			//TxtDataConnection SPData = new TxtDataConnection("CADUSDDATA.txt");
			STxtDataConnection SPData = new STxtDataConnection(fileName);
			Iterator<DataPoint> datapoints = SPData.getDataPoints();
			//LinkedList<Float> listOfProfits = new LinkedList<Float>();
			
			
			//Create the first population
			Population firstPop = new Population(populationSize, datapoints, trainingLine);
			//Train the first population
			firstPop.train();
			TradingStrategy currentFittest = firstPop.getFittest();
			//System.out.println(currentFittest.getFitness());
			
			//Each loop evolves the population once 
			for(int i=0;i<numEvolutions;i++){
				datapoints = SPData.getDataPoints();
				firstPop = GeneticAlgorithm.evolve(firstPop, datapoints, trainingLine);
				firstPop.train();
				currentFittest = firstPop.getFittest();
				/*
				float totsProts=0;
				for(int j = 0; j<populationSize; j++){
				//	System.out.println("The value of J = "+j);
					totsProts=totsProts + firstPop.getStrat(i).getFitness();
				}
				//listOfProfits.add(totsProts);
				
				
				System.out.print("{Profits!!! = " + totsProts + "}");
				
				//for(int j = 0; j<populationSize; j++){
				//	System.out.print("|");
				//	System.out.printf("%8s",(int)firstPop.getStrat(j).getFitness());	
				//}
				//System.out.print("\n");
				 */
			//	System.out.printf(" %3s \n",i);
				
			}
			
			
			Iterator<DataPoint> testingChart = SPData.getDataPoints();
			Population newPop = new Population(popSizeTwo,testingChart, trainingLine);
			
			//Gets a group of the top trading strategies to be testing 
			for(int i=0; i<popSizeTwo;i++){
				TradingStrategy toAdd = firstPop.getFittest();
				toAdd.reset();
				newPop.saveStrat(i, toAdd);
				//System.out.println(toAdd.getFitness());
			}
			
			//Checks that the new population was properly reset
			for(int i=0;i<popSizeTwo;i++){
				TradingStrategy check = newPop.getStrat(i);
				if(check.getFitness()>0){
					System.out.println("Population was not properly reset");
				}
			}
			
			//Loads the recent market data into an array
			int curIndex = 0;
			int[] currTrade = new int[sizeStudy];
			DataPoint point1 = testingChart.next();
			DataPoint point2 = testingChart.next();
			int jun = 0; 
			while(jun<trainingLine){
				if((point2.getPrice()-point1.getPrice())>=0){
					currTrade[curIndex] = 1;
				}
				else{
					currTrade[curIndex] = 0;
				}
				point1 = point2;
				point2 = testingChart.next();
				curIndex++;
				if(curIndex==sizeStudy){
					curIndex=0;
				}
				jun++;
				//System.out.println(jun);
				difference=point2.getPrice();
			}
			
			//firstPop.resetPop();
			
			//Tests the trading strategies in the real market
			firstPop = newPop;
			int startingYear = point2.getDate().getDate()[0];
			curIndex=0;
			while(testingChart.hasNext()){
				//Update currTrade
				if((point2.getPrice()-point1.getPrice())>=0){
					currTrade[curIndex] = 1;
				}
				else{
					currTrade[curIndex] = 0;
				}
				curIndex++;
				if(curIndex==sizeStudy){
					curIndex=0;
				}
				for(int i=0;i<popSizeTwo;i++){
					currentFittest=firstPop.getStrat(i);
					if(!currentFittest.holding()){
						if(currentFittest.shouldBuy(currTrade)==true){
							currentFittest.buy(point2.getPrice());
							Date dateis = point2.getDate();
							int[] dateact = dateis.getDate();
							System.out.println("Buy ("+i+") at "+point2.getPrice()+" on "+dateact[0]+" "+dateact[1]+" "+dateact[2]+" "+dateact[3]+" "+dateact[4]);
						}
					}
					else{
						if(currentFittest.shouldSell(currTrade)==true){
							currentFittest.sell(point2.getPrice());
							Date dateis = point2.getDate();
							int[] dateact = dateis.getDate();
							System.out.println("Sell ("+i+") at "+point2.getPrice()+" on "+dateact[0]+" "+dateact[1]+" "+dateact[2]+" "+dateact[3]+" "+dateact[4]);
						}
					}
				}
			point1 = point2;
			point2 = testingChart.next();
			}
			difference=((point1.getPrice()/difference)-1)*100;
			for(int b=0;b<popSizeTwo;b++){
				if(firstPop.getStrat(b).holding()){
					firstPop.getStrat(b).sell(point1.getPrice());
				}
			}
			
			
			float totalProfit=0;
			for(int i=0;i<popSizeTwo;i++){
				currentFittest = firstPop.getStrat(i);
				
			//	System.out.println(currentFittest.getFitness());
				//for(int j=0;j<)
				totalProfit+=currentFittest.getFitness();
				System.out.println(currentFittest.getProbs()+"% probability");
			}
			
			//System.out.println(totalProfit);
			//System.out.println((totalProfit/(1000000*10))*100+"% return for years "+startingYear+"-"+point1.getDate().getDate()[0]);
			returns[k] = (totalProfit/(1000000*10))*100;
			/*
			Iterator<Float> temper = listOfProfits.listIterator();
			float temperOne = temper.next();
			System.out.println("Start:");
			while(temper.hasNext()){
				System.out.print(temperOne+"\n");
				temperOne=temper.next();
			}
			*/
		}
		float totalReturns = 0;
		for(int i=0;i<numRuns;i++){
			System.out.println(returns[i]);
			totalReturns+=returns[i];
		}
		totalReturns=totalReturns/numRuns;
		System.out.println("Acheived returns  = "+totalReturns+"%");
		System.out.println("Actual difference = "+difference+"%");
	}
}		

