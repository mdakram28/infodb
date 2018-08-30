package com.mdakram28.infodb.datamanager;

public interface IDataEndpoint {
	public final class TYPE {
		public static final String DATABASE = "DATABASE";
		public static final String CACHE = "CACHE";
		public static final String SCRAPER = "SCRAPER";
		public static final String API = "API";
	}
	
	public String getType();
	public String getName();
}
