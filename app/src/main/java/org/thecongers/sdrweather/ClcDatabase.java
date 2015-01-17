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

class ClcDatabase extends SQLiteAssetHelper {

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