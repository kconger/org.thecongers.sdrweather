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
    
    // Return county and state from FIPS code
    public Cursor getCountyState(String fipscode) {
    	
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"county", "state"}; 
        String sqlTables = "fips";
        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, "fipscode=?", new String[] { String.valueOf(fipscode) }, null, null, null);
        if (c != null)
            c.moveToFirst();

        db.close();
        return c;
    }
    
}