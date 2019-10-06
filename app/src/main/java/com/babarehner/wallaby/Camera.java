package com.babarehner.wallaby;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.babarehner.wallaby.AddEditWallabyActivity.REQUEST_IMAGE_CAPTURE;


/**
 * Project Name: Wallaby
 * <p>
 * Copyright 10/4/19 by Mike Rehner
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

public class Camera extends Fragment {

    static final String LOG_TAG = AddEditWallabyActivity.class.getSimpleName();

    // static final int REQUEST_IMAGE_CAPTURE = 100;



    private Button mTakePictureButton;
    private boolean booleanValue;

    private Context context;
    private Activity activity;

    private String currentPhotoPath;

    private Uri mPhotoUri;
    private ImageView imageView;
    private ImageView imageThmbNail;

    public Camera( Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public Button checkPermission(Button takePictureButton){
        mTakePictureButton = takePictureButton;
        checkCameraPermission();
        return mTakePictureButton;
    }


    public void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            mTakePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA},
                    0);
        }
    }

    public Uri takePicture(ImageView v){
        imageView = v;
        dispatchTakePictureIntent();
        return mPhotoUri;

    }

    // The onRequestPermissionsResult is called on the activity and not the fragement
    // @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mTakePictureButton.setEnabled(true);
            }
        }
    }


    // create a new file name
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = getExternalFilesDir("images");  // get the apps local directory
        File storageDir = activity.getFilesDir();
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


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the event
        Log.v("starting dispatchTake ", LOG_TAG);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the filew where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.v("photoFile: ", photoFile.toString());
            } catch (IOException ex) {
                // Error occurred while creating file
                ex.printStackTrace();
                Log.v("IO exception: :", LOG_TAG);
            }
            // Continue if the file was succesfully created
            mPhotoUri = FileProvider.getUriForFile(context, "com.babarehner.wallaby.fileprovider", photoFile);
            Log.v("Uri:", mPhotoUri.toString());
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public String getCurrentPhotoPath(){
        return currentPhotoPath;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){

            imageView.setImageURI(mPhotoUri);
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

}
