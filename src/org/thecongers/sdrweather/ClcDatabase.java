package org.thecongers.sdrweather;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class ClcDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "clc.db";
    private static final int DATABASE_VERSION = 1;

    public ClcDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }
    
    // Return region and province/territory from CLC code
    public Cursor getCountyState(String clccode) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"provinceterritory", "region"}; 
        String sqlTables = "clc";
        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, "clccode=?", new String[] { String.valueOf(clccode) }, null, null, null);
        if (c != null)
            c.moveToFirst();
        
        db.close();
        return c;
        
    }
    
}