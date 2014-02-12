package org.thecongers.sdrweather;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class FipsDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "fips.db";
    private static final int DATABASE_VERSION = 1;

    public FipsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }
    
    public Cursor getCountyState(String fipscode) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"county", "state"}; 
        String sqlTables = "fips";

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, "fipscode=?", new String[] { String.valueOf(fipscode) }, null, null, null);
        if (c != null)
            c.moveToFirst();
        
        // Closing database connection
        db.close();
        return c;
        
    }
}