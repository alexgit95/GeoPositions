package geopositions.services.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import geopositions.model.GeoPosition;
import geopositions.services.DaoServices;

public class DaoServicesImpl implements DaoServices {

	private AmazonDynamoDB client;
	private DynamoDBMapper mapper;

	public DaoServicesImpl() {
		super();
		client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_WEST_3).build();
		mapper = new DynamoDBMapper(client);
	}

	@Override
	public List<GeoPosition> getAll() {
		return mapper.scan(GeoPosition.class, new DynamoDBScanExpression());
	}

	@Override
	public void insert(GeoPosition toCreate) {
		mapper.save(toCreate);
	}

	@Override
	public List<GeoPosition> getPositionsByYear(int year) {

		Calendar cal = Calendar.getInstance();
		cal.set(year, 1, 1, 0, 0, 0);
		Date debut = cal.getTime();
		cal.set(year, 12, 31, 23, 59, 59);
		Date fin = cal.getTime();

		return getPositionBetweenDate(debut, fin);

	}
	
	@Override
	public List<GeoPosition> getPositionsByMonth(int year, int month) {

		Calendar cal = Calendar.getInstance();
		cal.set(year, month, 1, 0, 0, 0);
		Date debut = cal.getTime();
		cal.set(year, month, cal.getActualMaximum(Calendar.DATE), 23, 59, 59);
		Date fin = cal.getTime();

		return getPositionBetweenDate(debut, fin);

	}
	
	@Override
	public List<GeoPosition> getPositionsByPosition(double lattitude, double longitude) {

		double latmin=lattitude-0.5;
		double latmax=lattitude+0.5;
		double longmin=longitude-0.5;
		double longmax=longitude+0.5;
		
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":latmin", new AttributeValue().withS(""+latmin));
		eav.put(":latmax", new AttributeValue().withS(""+latmax));
		
		eav.put(":longmin", new AttributeValue().withS(""+longmin));
		eav.put(":longmax", new AttributeValue().withS(""+longmax));
		
		

		DynamoDBQueryExpression<GeoPosition> queryExpression = new DynamoDBQueryExpression<GeoPosition>()
				.withKeyConditionExpression("lattitude between :latmin and :latmax and longitude between :longmin and :longmax")
				.withExpressionAttributeValues(eav);

		return mapper.query(GeoPosition.class, queryExpression);

	}
	
	
	
	private List<GeoPosition> getPositionBetweenDate(Date start, Date end){
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		//dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String startDate = dateFormatter.format(start);
		String endDate = dateFormatter.format(end);

		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":val1", new AttributeValue().withS(startDate));
		eav.put(":val2", new AttributeValue().withS(endDate));

		DynamoDBQueryExpression<GeoPosition> queryExpression = new DynamoDBQueryExpression<GeoPosition>()
				.withKeyConditionExpression("date between :val1 and :val2")
				.withExpressionAttributeValues(eav);

		return mapper.query(GeoPosition.class, queryExpression);
	}

}
