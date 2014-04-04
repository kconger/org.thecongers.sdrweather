package org.thecongers.sdrweather;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
//For debugging
//import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

@SuppressLint("SimpleDateFormat")
public class EasDatabase extends SQLiteAssetHelper {

	public static final String DEBUG_TAG = "EASMSGDB";
	private static final String DATABASE_NAME = "easmsg.db";
    private static final int DATABASE_VERSION = 1;
    // For debugging
	//private static final String TAG = "EASDB";

    public EasDatabase(Context context) {
    	
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
        
    }
    
    //Get current active event
	public Cursor getActiveEvent() {

        // Get current time in UTC
        Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String curdate = formatter.format(date);
		
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"org", "desc", "level", "timereceived", "timeissued", "callsign", "purgetime", "regions", "country"}; 
        String sqlTables = "easMsg";
        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, "purgetime>=?", new String[] { String.valueOf(curdate) }, null, null, "datetime(purgetime) DESC");
        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
              
    }
    
	// Add event to database
    void addEasMsg(EasMsg easmsg) {
    	
    	//Log.d(TAG, "Adding event to database");
    	SQLiteDatabase db = this.getWritableDatabase();
    	String sqlTables = "easMsg";
    	
        ContentValues values = new ContentValues();
        values.put("org", easmsg.getOrg());
        values.put("desc", easmsg.getDesc());
        values.put("level", easmsg.getLevel());
        values.put("timereceived", easmsg.getTimeReceived());
        values.put("timeissued", easmsg.getTimeIssued());
        values.put("callsign", easmsg.getCallSign());
        values.put("purgetime", easmsg.getPurgeTime());
        values.put("regions", easmsg.getRegions());
        values.put("country", easmsg.getCountry());
 
        // Inserting Row
        db.insert(sqlTables, null, values);
        db.close();
        
    }
    
    // Purge expired events
    void purgeExpiredMsg() {
    	//Log.d(TAG, "Purging old events from DB");
    	SQLiteDatabase db = this.getWritableDatabase();
    	String sqlTables = "easMsg";
    	
    	// Get current time in UTC
    	Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String curdate = formatter.format(date);
		
		// Delete rows
    	db.delete(sqlTables, "purgetime<?", new String[] { String.valueOf(curdate) });
    	db.execSQL("VACUUM");
    	// Closing database connection
    	db.close();
    	
    }

}
