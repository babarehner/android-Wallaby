package com.babarehner.wallaby;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
 * <p>
 * Adds and edits items in the Wallaby database. Each item consists of a Picture and a Name
 */

public class AddEditWallabyActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String LOG_TAG = AddEditWallabyActivity.class.getSimpleName();

    public static final int EXISTING_ADD_EDIT_WALLABY_LOADER = 0;
    public static final int THUMBSIZE = 100;

    private Uri mCurrentWallabyUri;

    private EditText mEditTextCard;
    private Button mTakePictureButton;
    private ImageView mImageView;
    private ImageView imageThmbNail;
    private Bitmap thmbNailBitmap;
    private byte[] thmbNailBlob;

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;

    public static final int REQUEST_IMAGE_CAPTURE = 100;

    public static final int REQUEST_CAMERA_PERMISSION = 0;

    private String mCurrentPhotoPath;

    private Uri mPhotoUri;

    private static final String PATH = "images/";

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_wallaby);

        Intent intent = getIntent();
        mCurrentWallabyUri = intent.getData();

        mImageView = findViewById(R.id.imageView);
        imageThmbNail = findViewById(R.id.imageThmbNail);
        mTakePictureButton = findViewById((R.id.button_image));

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }








    // Button calls this in resource file
    public void takePicture(View v) {
        if (!checkCameraPermission()){
            Log.v("takePictureView ", LOG_TAG);
            mTakePictureButton.setEnabled(false);
        } else {
            mTakePictureButton.setEnabled(true);  // make sure the TakePicture Button is enabled
            // set up Camera to take picture
            Camera myCamera = new Camera(this, this);
            mPhotoUri = myCamera.takePicture(mImageView);
            mCurrentPhotoPath = myCamera.getCurrentPhotoPath();
        }
    }


    // onActivityResult called in Fragment Camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            mImageView.setImageURI(mPhotoUri);
        } else {
            if (resultCode == RESULT_CANCELED){
                // user cancelled the image capture
                //TODO delete file path from the DB
            }
        }
        // Get the thumbnail image
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mCurrentPhotoPath), THUMBSIZE, THUMBSIZE);
        imageThmbNail.setImageBitmap(thumbImage);
    }


    public boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v( "grantResults", Integer.toString(grantResults[0]));
        Log.v( "requestCode", Integer.toString(requestCode));
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length <= 0){
                Log.i (LOG_TAG, "User interaction was cancelled") ;
                mTakePictureButton.setEnabled(true);
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v( "serendipity", Integer.toString(grantResults[0]));
                mTakePictureButton.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Camera permission granted, click on 'Take Picture' button ", Toast.LENGTH_LONG).show();
            } else {
                mTakePictureButton.setEnabled(false);
                Toast.makeText(getApplicationContext(), "Camera permission not granted. Unable to take picture unless Permission given.", Toast.LENGTH_LONG).show();
            }
        }
    }


    // Convert the image to byte array in order to store a blob image
    public static byte[] getPictureByteOfArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    // Convert byte array to bitmap image
    public static Bitmap getBitmapFromByte(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

}
