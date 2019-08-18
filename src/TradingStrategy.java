import java.util.Iterator;

public class TradingStrategy {
	private int sizeStudy=Main.sizeStudy;
	private int[] buyer = new int[sizeStudy];
	private int[] seller = new int[sizeStudy];
	private double probability;
	private float profit;
	private float startingPrice;
	private boolean holding;
	private float capital=1000000;
	private float numholding;
	private boolean crashed;
	
	//Initializes the trading strategy with random numbers
	public TradingStrategy(){
		for(int i=0;i<sizeStudy;i++){
			double randomNum = Math.random();
			if(randomNum>0.5){
				buyer[i] = 1;
			}
			randomNum=Math.random();
			if(randomNum>0.5){
				seller[i] = 1;
			}
		}
		profit = 0;
		crashed=false;
		probability=Math.random();
	}
	
	//Return the probability 
	public double getProbs(){
		return probability;
	}
	
	//Sets the probability
	public void setProbs(double probs){
		probability=probs;
	}
	
	//Returns the gene of the trading strategy
	public int getGene(int i){
		if(i<sizeStudy){
			return buyer[i];
		}
		return seller[i-sizeStudy];
	}
	
	//Sets the gene of the trading strategy
	public void setGene(int i, int zeroOrOne){
		if(i<sizeStudy){
			buyer[i] = zeroOrOne;
		}
		else{
			seller[i-sizeStudy] = zeroOrOne;
		}
	}
	
	//Returns true if the trading strategy should buy
	public boolean shouldBuy(int[] checker){
		if(holding==true||crashed==true){
			return false;
		}
		
		double total=0.0;
		double divide=0;
		//Check the provided list with the genes in the trading strategy
		for(int i=0; i<sizeStudy;i++){
			if(checker[i]==buyer[i]){
				//prioritizes the more current genes
				total=total+(i*i);
			}
			divide=divide+(i*i);
		}
		double probs = total/divide;
		if(probs>=probability){
			return true;
		}
		return false;
	}
	
	//Buys the stock
	public void buy(float startingPrice){
		this.startingPrice=startingPrice;
		holding = true;
		numholding = capital/startingPrice;
		
		//Adds the 20 basis point fee for each buy
		float fee = -((float)(numholding*startingPrice*0.002));
		addProfit(fee);
		//System.out.println("Fees: " + fee); 
	}
	
	//Returns true if the trading strategy should buy
	public boolean shouldSell(int[] checker){
		if(holding==false){
			return false;
		}
		double divide=0.0;
		double total=0.0;
		//Check the provided list with the genes in the trading strategy
		for(int i=0; i<sizeStudy;i++){
			if(checker[i]==seller[i]){
				//prioritizes the more current genes
				total=total+(i*i);
			}
			divide=divide+(i*i);
		}
		double probs = total/divide;
		if(probs>=probability){
			return true;
		}
		return false;
	}
	
	//Sells the stock
	public void sell(float endingPrice){
		float tradeProfit = numholding*(endingPrice-startingPrice);
		addProfit(tradeProfit);
		//Adds profit back into the capital
		capital+=tradeProfit;
		holding = false;
		if(capital<=0){
			crashed=true;
		}
	//	probability+=0.01;
	}
	
	//Returns the profit of the trading strategy
	public float getFitness(){
		return profit;
	}
	
	//Adds profit to the trading strategy
	public void addProfit(float profit){
		this.profit+=profit;
	}
	
	//Returns true if holding
	public boolean holding(){
		return holding;
	}
	
	//Resets the trading strategy
	public void reset(){
		profit=0;
		capital=1000000;
		holding = false;
		numholding=0;
		crashed=false;
		//System.out.println("reset");
	}
}
