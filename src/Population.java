import java.util.Iterator;

public class Population {
	private int sizeStudy=Main.sizeStudy;
	private int size;
	private TradingStrategy[] strats;
	private Iterator<DataPoint> learningChart;
	private int trainingLine;
	
	//Creates a new population with trading strategies 
	public Population(int size, Iterator<DataPoint> learningChart, int trainingLine){
		this.size=size;
		this.trainingLine=trainingLine;
		this.learningChart=learningChart;
		strats = new TradingStrategy[size];
		for(int i=0;i<size;i++){
			saveStrat(i, new TradingStrategy());
		}
		
	}
	
	//Saves a trading strategy to the population
	public void saveStrat(int i, TradingStrategy strat){
		strats[i]=strat;
		return;
	}
	
	//Returns a trading strategy in the population
	public TradingStrategy getStrat(int i){
		return strats[i];
	}
	
	//Reset the entire population
	public void resetPop(){
		for(int i=0;i<size;i++){
			strats[i].reset();
		}
		return;
	}
	
	//Returns the fittest (highest profit) trading strategy in the group
	public TradingStrategy getFittest() {
		TradingStrategy fittest = strats[0];
        // Loop through individuals to find fittest (highest profit)
        for (int i = 1; i < size; i++) {
            if (fittest.getFitness() <= getStrat(i).getFitness()) {
                fittest = getStrat(i);
            }
        }
        return fittest;
    }
	
	
	//Returns the size of the trading strategy
	public int getSize(){
		return size;
	}
	
	//Trains the population to get theirfitness level
	public void train(){
		int curIndex = 0;
		int[] currTrade = new int[sizeStudy];
		DataPoint point1 = learningChart.next();
		DataPoint point2 = learningChart.next();
		while(curIndex<sizeStudy){
			if((point2.getPrice()-point1.getPrice())>=0){
				currTrade[curIndex] = 1;
			}
			point1 = point2;
			point2 = learningChart.next();
			curIndex++;
		}
		curIndex=0;
		int line = 0;
		while(line<trainingLine){
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
			
			for(int i=0;i<size;i++){
				TradingStrategy strat = strats[i];
				if(!strat.holding()){
					if(strat.shouldBuy(currTrade)==true){
						strat.buy(point2.getPrice());
					}
				}
				else{
					if(strat.shouldSell(currTrade)==true){
						strat.sell(point2.getPrice());
					}
				}
			}
			point1 = point2;
			point2 = learningChart.next();
			line++;
		}
	}
}
