package org.thecongers.sdrweather;

public class EasMsg {

	    //private variables
	    int _id;
	    String _org;
	    String _desc;
	    String _level;
	    String _timereceived;
	    String _timeissued;
	    String _callsign;
	    String _purgetime;
	    String _regions;
	    String _country;
	     
	    // Empty constructor
	    public EasMsg(){
	         
	    }
	    // Constructor
	    public EasMsg(String org, String desc, String level, 
	    		String timereceived, String timeissued, String callsign, 
	    		String purgetime, String regions, String country) {
	    	this._org = org;
	    	this._desc = desc;
		    this._level= level;
		    this._timereceived = timereceived;
		    this._timeissued = timeissued;
		    this._callsign = callsign;
		    this._purgetime = purgetime;
		    this._regions = regions;
		    this._country = country;

	    }
	    
	    public String getOrg(){
	        return this._org;
	    }
	    
	    public String getDesc(){
	        return this._desc;
	    }
	    
	    public String getLevel(){
	        return this._level;
	    }
	    
	    public String getTimeReceived(){
	        return this._timereceived;
	    }
	    
	    public String getTimeIssued(){
	        return this._timeissued;
	    }
	    
	    public String getCallSign(){
	        return this._callsign;
	    }
	    
	    public String getPurgeTime(){
	        return this._purgetime;
	    }
	    
	    public String getRegions(){
	        return this._regions;
	    }
	    
	    public String getCountry(){
	        return this._country;
	    }

}
