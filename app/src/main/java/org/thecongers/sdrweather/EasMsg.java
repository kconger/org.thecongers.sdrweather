/*
Copyright (C) 2014 Keith Conger <keith.conger@gmail.com>

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.thecongers.sdrweather;

class EasMsg {

	    //private variables
	    int _id;
	    private String _org;
	    private String _desc;
	    private String _level;
	    private String _timereceived;
	    private String _timeissued;
	    private String _callsign;
	    private String _purgetime;
	    private String _regions;
	    private String _country;
	     
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
