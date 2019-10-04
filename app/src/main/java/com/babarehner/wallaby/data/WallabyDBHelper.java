package com.babarehner.wallaby.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.babarehner.wallaby.data.WallabyContract.WallabyTableConstants.*;


/**
 * Project Name: Wallaby
 * <p>
 * Copyright 9/25/19 by Mike Rehner
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class WallabyDBHelper extends SQLiteOpenHelper {

    // To allow for changes in DB versioning and keeping user data
    private static final int DB_VERSION = 1;

    static final String DB_NAME = "wallaby.db";

    public WallabyDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    /**
     * Create the wallaby db, create the schema and empty table.
     * @param sqLiteDatabase the db
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_WALLABY_TABLE = "CREATE TABLE " +
                WALLABY_TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_CARD_N + "TEXT, " +
                C_IMAGE_FN + " TEXT, " +
                C_THMB_NAIL + " BLOB, " +
                C_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP );";     //Unix Time in Long ms

        sqLiteDatabase.execSQL(SQL_CREATE_WALLABY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WALLABY_TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
