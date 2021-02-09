package com.babarehner.wallaby;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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


/**
 * Project Name: Wallaby
 * <p>
 * Copyright 2/2/21 by Mike Rehner
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


public class Photo extends Fragment {
//
//    static final String LOG_TAG = Photo.class.getSimpleName();
//
//    //static final int REQUEST_IMAGE_CAPTURE = 101;
//    public static final int REQUEST_CAMERA_PERMISSION = 99;
//
//    private Button mTakePictureButton;
//
//    private Context context;
//    private Activity activity;
//
//    public String currentPhotoPath;
//    private Uri mPhotoUri;
//    private File photoFile;
//    private Bitmap mImageBitmap;
//
//    public Photo( Context context, Activity activity){
//        this.context = context;
//        this.activity = activity;
//    }
//
//    // return the photo absolute path
//    public String getCurrentPhotoPath(){
//        return currentPhotoPath;
//    }
//
//    public String getFileName() { return photoFile.toString();}
//
//    public Bitmap getImageBitmap() {return mImageBitmap; }
//
//
//    /** Check if this device has a camera */
//    private boolean checkCameraHardware(Context context) {
//        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
//            // this device has a camera
//            return true;
//        } else {
//            // no camera on this device
//            return false;
//        }
//    }
//
//
//
//
//    private boolean checkCameraPermission() {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA},
//                    REQUEST_CAMERA_PERMISSION);
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//
//    public Uri takePicture(){
//        dispatchTakePictureIntent();
//        return mPhotoUri;
//    }
//
//
//    // actally take the picture
//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the event
//        Log.d("starting dispatchTake ", LOG_TAG);
//        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
//            // Create the filew where the photo should go
//            photoFile = null;
//            // File photoFile = null;
//            try {
//                photoFile = createImageFile();
//                Log.v("photoFile: ", photoFile.toString());
//            } catch (IOException ex) {
//                // Error occurred while creating file
//                ex.printStackTrace();
//                Log.v("IO exception: :", LOG_TAG);
//            }
//            // Continue if the file was succesfully created
//            //mPhotoUri can be local
//            mPhotoUri = null;
//            mPhotoUri = FileProvider.getUriForFile( context,
//                    "com.babarehner.wallaby.fileprovider", photoFile);
//            Log.v("Uri:", mPhotoUri.toString());
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
//            // launch camera activity to take the photo
//            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }
//
//
//    // create a new file name with a place to store teh file
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        currentPhotoPath="";
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        //File storageDir = getExternalFilesDir("images");
//        // get the apps local directory
//        File storageDir = activity.getFilesDir();
//        Log.v("Storage Directory ", storageDir.toString() );
//        File imageFile = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        currentPhotoPath = imageFile.getAbsolutePath();
//        Log.v("currentPhotoPath: ", currentPhotoPath);
//        return imageFile;
//    }
//
//
//    // @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 0) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                mTakePictureButton.setEnabled(true);
//            } else {
//                Toast.makeText(context, "Camera permission not granted. Unable to take Picture.", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        //super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
//            // imageView.setImageURI(mPhotoUri);
//            Bundle extras = data.getExtras();
//            mImageBitmap = (Bitmap) extras.get("data");
//        } else {
//            if (resultCode == RESULT_CANCELED){
//                // user cancelled the image capture
//                //TODO delete file path from the DB
//            }
//        }
//    }

}
