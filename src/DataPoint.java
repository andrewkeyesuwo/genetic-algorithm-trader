
public class DataPoint {
	private Date currentDate;
	private float price;
	
	public DataPoint(float price, Date currentDate){
		this.price=price;
		this.currentDate=currentDate;
	}
	
	public Date getDate(){
		return currentDate;
	}
	
	public float getPrice(){
		return price;
	}
}
