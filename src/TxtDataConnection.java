import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.util.Iterator;
import java.util.LinkedList;


public class TxtDataConnection {
	private String fileLocation, curPrice;
	private int yearInt, monthInt, dayInt, hourInt, minuteInt, startIndexCurPrice;
	LinkedList<DataPoint> dataPoints;
	
	public TxtDataConnection(String fileLocation) throws IOException{
		dataPoints = new LinkedList<DataPoint>();
		this.fileLocation = fileLocation;
		File txtFile = new File(fileLocation);
		FileReader txtFileInput = new FileReader(txtFile);
		BufferedReader reader = new BufferedReader(txtFileInput);
		String firstLine = reader.readLine();
		while(firstLine!=null){
		//	System.out.println(firstLine);
			String year = firstLine.substring(0, 4);
			yearInt = Integer.parseInt(year);
			
			String month = firstLine.substring(6, 7);
			monthInt = Integer.parseInt(month);
			
			String day = firstLine.substring(9, 10);
			dayInt = Integer.parseInt(day);
			
			String time = firstLine.substring(11, 16);
			time = time.trim();
			if(time.length()>4){
				String hour = time.substring(0, 1);
				hourInt = Integer.parseInt(hour);
				String minutes = time.substring(3, 5);
				minuteInt = Integer.parseInt(minutes);
				startIndexCurPrice = 17;
			}
			else{
				char hour = time.charAt(1);
				hourInt = hour - '0';
				String minutes = time.substring(2, 4);
				minuteInt = Integer.parseInt(minutes);
				startIndexCurPrice = 16;
			}
			int finalIndexCurPrice = startIndexCurPrice;
			
			while((firstLine.charAt(finalIndexCurPrice)>='0'&&firstLine.charAt(finalIndexCurPrice)<='9')||firstLine.charAt(finalIndexCurPrice)=='.'){
				finalIndexCurPrice++;
			}
			curPrice = firstLine.substring(startIndexCurPrice, finalIndexCurPrice);
			
			Date dateOf = new Date(yearInt, monthInt, dayInt, hourInt, minuteInt);
			float curPriceFloat = Float.parseFloat(curPrice);
			DataPoint currentPoint = new DataPoint(curPriceFloat, dateOf);
			dataPoints.add(currentPoint);
			firstLine = reader.readLine();
			
		}
		//test();
		reader.close();
	}
	
	public Iterator<DataPoint> getDataPoints(){
		return dataPoints.listIterator();
	}
	
	
	//Test is off
	public void test(){
		Iterator<DataPoint> iterator = dataPoints.listIterator();
		DataPoint point = iterator.next();
		DataPoint pointTwo = iterator.next();
		float firstPrice = point.getPrice();
		float lastPrice=0;
		float Total=0;
		while(iterator.hasNext()){
			float priceOne=point.getPrice();
			float priceTwo=pointTwo.getPrice();
			float diff = priceTwo-priceOne;
			Total=Total+diff;
			System.out.println("Current Difference = "+diff);
			
			point = pointTwo;
			pointTwo = iterator.next();
			lastPrice=pointTwo.getPrice();
		}
		
		System.out.println("Calculated Difference = " + Total);
		System.out.println("Actual Difference = " + (lastPrice-firstPrice));
		System.out.println("Error = "+(Total-(lastPrice-firstPrice)));
	}
}
