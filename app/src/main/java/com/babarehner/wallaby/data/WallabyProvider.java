package com.babarehner.wallaby.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.babarehner.wallaby.data.WallabyContract.*;
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

public class WallabyProvider extends ContentProvider {

    private static final String LOG_TAG = WallabyProvider.class.getSimpleName();

    private static final int WALLABIES = 100;
    private static final int WALLABY_ID = 101;

    private WallabyDBHelper mDBHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        sUriMatcher.addURI(WALLABY_AUTHORITY, PATH_WALLABY_TABLE_NAME, WALLABIES);
        sUriMatcher.addURI(WALLABY_AUTHORITY, PATH_WALLABY_TABLE_NAME + "/#", WALLABIES);
    }
    @Override
    public boolean onCreate() {
        mDBHelper = new WallabyDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        Cursor c;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case WALLABIES:
                c = db.query(WALLABY_TABLE_NAME, projection, selection, selectionArgs, null,
                    null, sortOrder);
                break;
            case WALLABY_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                c = db.query(WALLABY_TABLE_NAME, projection, selection, selectionArgs, null,
                        null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI: " + uri);
        }

        // notify if the data at this URI changes, Then we need to update the cursor listener
        // attached is automatically notified with uri
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WALLABIES:
                return WALLABYLIST_TYPE;
            case WALLABY_ID:
                return WALLABY_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown Uri: " + uri + "with match: " + match);
        }
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match((uri));

        if (match == WALLABIES){
            return insertRecord(uri, values);
        } else {
            throw new IllegalArgumentException("Insertion is not supported for: " + uri);
        }
    }


    // Insert a record into the records table with the given content values. Return the new content uri
    // for that specific row in the database
    private Uri insertRecord(Uri uri, ContentValues values) {

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long id = db.insert(WALLABY_TABLE_NAME, null, values);
        Log.v(LOG_TAG, "Record not entered");
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // notify all listeners that the data has changed for the TSTUFF table
        getContext().getContentResolver().notifyChange(uri, null);
        // return the new Uri with the ID of the newly inserted row appended to the db
        return ContentUris.withAppendedId(uri, id);
    }




    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        if (match == WALLABY_ID) {
            selection = _ID + "=?";
            selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            rowsDeleted = db.delete(WALLABY_TABLE_NAME, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Deletion is not supported for: " + uri);
        }

        if (rowsDeleted != 0) {
            // Notify all listeners that the db has changed
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WALLABIES:
                return updateRecords(uri, values, selection, selectionArgs);
            case WALLABY_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateRecords(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for: " + uri);
        }
    }



    private int updateRecords(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        // if there are no values quit
        if ( values == null || values.size() == 0) {
            return 0;
        }

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int rows_updated = db.update(WALLABY_TABLE_NAME, values, selection, selectionArgs);
        if (rows_updated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows_updated;
    }
}
