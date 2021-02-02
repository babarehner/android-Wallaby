package com.babarehner.wallaby;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.babarehner.wallaby.data.WallabyContract;
import com.bumptech.glide.Glide;

import static com.babarehner.wallaby.data.WallabyContract.WallabyTableConstants.C_CARD_N;
import static com.babarehner.wallaby.data.WallabyContract.WallabyTableConstants.C_IMAGE_FN;
import static com.babarehner.wallaby.data.WallabyContract.WallabyTableConstants.C_THMB_NAIL;
import static com.babarehner.wallaby.data.WallabyContract.WallabyTableConstants._ID;

/**
 * Project Name: Wallaby
 * <p>
 * Copyright 10/29/19 by Mike Rehner
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

public class ScanPictureActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "ScanPictureActivity";

    private static final int WALLABY_LOADER_ID = 3;

    private Uri mCurrentRecordUri;

    private ImageView mImage;
    private TextView mTextView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_picture);
        Log.v(TAG, "in onCreate: started");

        Intent intent = getIntent();
        mCurrentRecordUri = intent.getData();
        //Toast.makeText(ScanPictureActivity.this, "Uri: " + mCurrentRecordUri, Toast.LENGTH_LONG).show();

        if (mCurrentRecordUri == null){
            setTitle("OOPS- No Data");
        } else {
            // initLoader;
            LoaderManager.getInstance(ScanPictureActivity.this).initLoader(WALLABY_LOADER_ID,
                    null, ScanPictureActivity.this);
        }

        Log.v(TAG, "in onCreate: started" + mCurrentRecordUri);
        mImage = findViewById(R.id.imageScanPicture);

    }



    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "starting in CreateLoader");
        String[] projection = {_ID, C_CARD_N, C_IMAGE_FN };
        Loader<Cursor> loader = new CursorLoader(this, mCurrentRecordUri, projection, null,
                null, null);
        return loader;
    }


    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor c) {
        // should move to the only row in the cursor
        if (c.moveToFirst()) {
            int cardNameIndex = c.getColumnIndex(C_CARD_N);
            Log.d(TAG, "card name index: " + cardNameIndex);
            int fileNameIndex = c.getColumnIndex(C_IMAGE_FN);

            String cardName = c.getString(cardNameIndex);
            String fileName = c.getString(fileNameIndex);

            // put the cardName in the title bar instead of a TextView
            setTitle(cardName);
            Glide.with(this).
                    load(fileName).
                    into(mImage);

            Log.d(TAG, "card name: " + cardName);
            Log.d(TAG, "file name: " + fileName);
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mTextView.setText("");
    }
}
