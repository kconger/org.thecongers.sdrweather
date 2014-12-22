package org.thecongers.sdrweather;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class EventDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 1;

    public EventDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }
    
    // Get event information from event code
    public Cursor getEventInfo(String evcode) {
    	
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"eventdesc", "eventlevel"}; 
        String sqlTables = "EASEvents";
        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, "eventcode=?", new String[] { String.valueOf(evcode) }, null, null, null);
        if (c != null)
            c.moveToFirst();

        db.close();
        return c;
        
    }
}