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

    private Uri mCurrentWallabyUri;

    private EditText mEditTextCard;
    private Button mTakePictureButton;
    private ImageView mImageView;
    private ImageView imageThmbNail;
    private Bitmap thmbNailBitmap;
    private byte[] thmbNailBlob;

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    static final int REQUEST_IMAGE_CAPTURE = 100;

    String currentPhotoPath;

    private Uri mPhotoUri;

    private static final String PATH = "images/";

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_wallaby);

        Intent intent = getIntent();
        mCurrentWallabyUri = intent.getData();

        mImageView = findViewById(R.id.imageView);
        imageThmbNail = findViewById(R.id.imageThmbNail);

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


    // create a new file name
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = getExternalFilesDir("images");  // get the apps local directory
        File storageDir = getFilesDir();
        Log.v("Storage Directory ", storageDir.toString() );
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = imageFile.getAbsolutePath();
        Log.v("currentPhotoPath: ", currentPhotoPath);
        return imageFile;
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the event
        Log.v("starting dispatchTake ", LOG_TAG);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){
            // Create the filew where the photo should go
            File photoFile = null;
            try{
                photoFile = createImageFile();
                Log.v("photoFile: ", photoFile.toString());
            } catch (IOException ex) {
                // Error occurred while creating file
                ex.printStackTrace();
                Log.v("IO exception: :", LOG_TAG);
            }
            // Continue if the file was succesfully created
            mPhotoUri = FileProvider.getUriForFile(this, "com.babarehner.wallaby.fileprovider", photoFile);
            Log.v("Uri:", mPhotoUri.toString());
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            // imageView.setImageURI(mPhotoUri);


        }
    }


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
        //get the thumbnail image
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(currentPhotoPath), 50, 50);
        imageThmbNail.setImageBitmap(thumbImage);
        //
    }


    // Button calls this in resource file
    public void takePicture(View v){
        Camera  myCamera = new Camera(this, this);

        mTakePictureButton = findViewById((R.id.button_image));
        mImageView = findViewById(R.id.imageView);
        // checkCameraPermission();
        Log.v("takePictureView ", LOG_TAG);
        // dispatchTakePictureIntent();
        mTakePictureButton = myCamera.checkPermission(mTakePictureButton);
        mPhotoUri = myCamera.takePicture(mImageView);
        mImageView.setImageURI(mPhotoUri);

        currentPhotoPath = myCamera.getCurrentPhotoPath();
        //get the thumbnail image
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(currentPhotoPath), 50, 50);
        imageThmbNail.setImageBitmap(thumbImage);
        //
    }



    public void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            mTakePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    0);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mTakePictureButton.setEnabled(true);
            } else {
                Toast.makeText(getApplicationContext(), "Camera permission not granted. Unable to take Picture.", Toast.LENGTH_LONG).show();
            }
        }
    }


    /*
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // sets a thumbnail into imageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            thmbNailBitmap = (Bitmap) extras.get("data"); // get the thumbnail
            imageView.setImageBitmap(thmbNailBitmap);
            // convert image to blob
            thmbNailBlob = getPictureByteOfArray(thmbNailBitmap);
        }
    }
    */


    /* Extract Thumbnail from full size picture
    Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath), THUMBSIZE, THUMBSIZE);
    imageView.setImageBitmap(thumbImage);
     */


    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Write permission granted for External Storage.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Write external storage permission not granted. Unable to back up item.", Toast.LENGTH_LONG).show();
            }
        }
    }

    */



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
