package com.babarehner.wallaby;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentFactory;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.babarehner.wallaby.DialogFragDeleteConfirmation;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.babarehner.wallaby.Utility.getBitmap;
import static com.babarehner.wallaby.data.WallabyContract.WallabyTableConstants.C_CARD_N;
import static com.babarehner.wallaby.data.WallabyContract.WallabyTableConstants.C_IMAGE_FN;
import static com.babarehner.wallaby.data.WallabyContract.WallabyTableConstants.C_THMB_NAIL;
import static com.babarehner.wallaby.data.WallabyContract.WallabyTableConstants.WALLABY_URI;
import static com.babarehner.wallaby.data.WallabyContract.WallabyTableConstants._ID;

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

public class AddEditWallabyActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        DialogFragDeleteConfirmation.DialogDeleteListener, DialogFragUnsavedChanges.DialogClickListener {

    static final String LOG_TAG = AddEditWallabyActivity.class.getSimpleName();

    public static final int EXISTING_ADD_EDIT_WALLABY_LOADER = 0;
    public static final int THUMBSIZE = 100;

    private EditText mEditTextCard;
    private Button mTakePictureButton;
    private ImageView mImageView;
    private String mImageViewFileName;
    private ImageView mImageThmbNail;
    private Bitmap thmbNailBitmap;
    private byte[] thmbNailBlob;

    private Uri mCurrentRecordUri;

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;

    // public static final int REQUEST_IMAGE_CAPTURE = 100;

    public static final int REQUEST_CAMERA_PERMISSION = 0;

    private String mCurrentPhotoPath;
    private EditText mEditTextPhotoPath;

    private Uri mPhotoUri;
    private Uri mDBPhotoUri;

    private static final String PATH = "images/";

    private boolean mRecordChanged = false;
    private boolean mHomeChecked;

    // Obtain MotionEvent object
    long downTime = SystemClock.uptimeMillis();
    long eventTime = SystemClock.uptimeMillis() + 100;
    float x = 0.0f;
    float y = 0.0f;
    // List of meta states found here:     developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
    int metaState = 0;
    MotionEvent motionEvent = MotionEvent.obtain(
            downTime,
            eventTime,
            MotionEvent.ACTION_UP,
            x,
            y,
            metaState
    );



    // Touch Listener to check if changes made to a book
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mRecordChanged = true;
            // following line was added to suppress warning for not programming for disabled
            // v.performClick();
            // above line caused date picker to hang up- probably because a click needed to be handled
            // in an onTouch event for date picker.
            return false;
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_wallaby);

        Intent intent = getIntent();
        mCurrentRecordUri = intent.getData();

        if (mCurrentRecordUri == null) {
            // set pager header to add record
            setTitle(getString(R.string.add_record));
        } else {
            setTitle("Edit Record");
            //getLoaderManager().initLoader(EXISTING_ADD_EDIT_WALLABY_LOADER, null, this);
            LoaderManager.getInstance(AddEditWallabyActivity.this).initLoader(
                    EXISTING_ADD_EDIT_WALLABY_LOADER, null, AddEditWallabyActivity.this);
            Toast.makeText(this, "mCurrentRecordUri: " + mCurrentRecordUri, Toast.LENGTH_LONG).show();
        }

        mEditTextCard = findViewById(R.id.edit_card);
        mImageView = findViewById(R.id.imageView);
        mImageThmbNail = findViewById(R.id.imageThmbNail);
        mTakePictureButton = findViewById((R.id.button_image));


        mEditTextCard.setOnTouchListener(mTouchListener);
        mImageView.setOnTouchListener((mTouchListener));
        mImageThmbNail.setOnTouchListener(mTouchListener);
        // TODO When the button clicked remove old pics and then store new pics
        mTakePictureButton.setOnTouchListener(mTouchListener);

    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {_ID, C_CARD_N, C_IMAGE_FN, C_THMB_NAIL};
        Loader<Cursor> loader = new CursorLoader(getApplicationContext(), mCurrentRecordUri, projection, null,
                null, null);
        return loader;
    }


    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor c) {
        if (c.moveToFirst()) {
            int cardNameIndex = c.getColumnIndex(C_CARD_N);
            int fileNameIndex = c.getColumnIndex(C_IMAGE_FN);
            int thumbNailIndex = c.getColumnIndex(C_THMB_NAIL);

            String cardName = c.getString(cardNameIndex);
            String fileName = c.getString(fileNameIndex);
            byte[] thumbNail = c.getBlob(thumbNailIndex);

            mEditTextCard.setText(cardName);
            // TODO get class variables to store bitmap in
            mImageViewFileName = fileName;
            Context context = AddEditWallabyActivity.this; // Redundant but makes clear how to get context of activity
            Glide.with(context).load(fileName).into(mImageView);
            //Glide.with(context).load(fileName).override(200,200).into(mImageThmbNail);
            Bitmap bitmapThumbNail = Utility.getBitmap(thumbNail);
            mImageThmbNail.setImageBitmap(bitmapThumbNail);

        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // if invalid loader clear data from input field
        mEditTextCard.setText("");
        mImageView.setImageResource(android.R.color.transparent);
        mImageThmbNail.setImageResource(android.R.color.transparent);
        mImageView.invalidate();
        mImageThmbNail.invalidate();
    }


    @Override   // set up the menu the first time
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.menu_add_edit_wallaby_activity, m);
        return true;
    }


    @Override   // hide delete/share menu items when adding a new exercise
    public boolean onPrepareOptionsMenu(Menu m) {
        super.onPrepareOptionsMenu(m);
        // if this is add an exercise, hide "delete" menu item

        if (mCurrentRecordUri == null) {
            MenuItem deleteItem = m.findItem(R.id.action_delete);
            deleteItem.setVisible(false);
        }
        return true;
    }


    @Override        // Select from the options menu
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveRecord();
                finish();       // exit activity
                return true;
            case R.id.action_delete:
                // Alert Dialog for deleting one record
                showDeleteConfirmationDialogFrag();
                return true;
            // this is the <- button on the header
            case android.R.id.home:
                // record has not changed
                if (!mRecordChanged) {
                    NavUtils.navigateUpFromSameTask(AddEditWallabyActivity.this);
                    return true;
                }
                mHomeChecked = true;
                showUnsavedChangesDialogFrag();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Override the activity's normal back button. If book has changed create a
    // discard click listener that closed current activity.
    @Override
    public void onBackPressed() {
        if (!mRecordChanged) {
            super.onBackPressed();
            return;
        }
        //otherwise if there are unsaved changes setup a dialog to warn the  user
        //handles the user confirming that changes should be made
        mHomeChecked = false;
        //showUnsavedChangesDialogFragment();
    }


    private void saveRecord() {

        String strCardName = mEditTextCard.getText().toString().trim();
        String strFileName = mCurrentPhotoPath;
        // String strFileName = mImageViewFileName;
        //mImageView.setDrawingCacheEnabled(true);
        // get the bitmap out of the mImageThmbnail
        Bitmap bmap = ((BitmapDrawable) mImageThmbNail.getDrawable()).getBitmap();
        byte[] bThumbNail = Utility.getBytes(bmap);

        ContentValues values = new ContentValues();
        values.put(C_CARD_N, strCardName);
        values.put(C_IMAGE_FN, strFileName);
        values.put(C_THMB_NAIL, bThumbNail);

        if (mCurrentRecordUri == null) {
            Log.v(LOG_TAG, "in saveRecord " + WALLABY_URI.toString() + "\n\n\n\n\n\n\n");
            Uri newUri = getContentResolver().insert(WALLABY_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_record_failed),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_record_succeeded),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            // existing record so update with content URI and pass in ContentValues
            int rowsAffected = getContentResolver().update(mCurrentRecordUri, values, null, null);
            if (rowsAffected == 0) {
                // TODO Check db- Text Not Null does not seem to be working or entering todo from PartsRunner
                // "" does not mean NOT Null- there must be an error message closer to the db!!!
                Toast.makeText(this, getString(R.string.edit_update_record_failed),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.edit_update_record_success),
                        Toast.LENGTH_LONG).show();
            }
        }
    }





    // ButtonView calls this when clicked
    public void takePicture(View v) {
        // Remove the pictures from the edit page. This seems to work
        if (mCurrentRecordUri != null){
            // erases pictures
            mImageView.setImageResource(android.R.color.transparent);
            mImageThmbNail.setImageResource(android.R.color.transparent);
            mImageView.invalidate();
            mImageThmbNail.invalidate();
            mPhotoUri = null;
            mCurrentPhotoPath = "";
            mImageViewFileName = "";
            //Required to trigger update Fragment
            //mageThmbNail.dispatchTouchEvent(motionEvent);
            //mageView.dispatchTouchEvent(motionEvent);
        }
        Context context = AddEditWallabyActivity.this;
        Photo myPhoto = new Photo(context, this);
        mPhotoUri = myPhoto.takePicture();
        mCurrentPhotoPath = myPhoto.getCurrentPhotoPath();

        Glide.with(context).load(mCurrentPhotoPath).override(200,200).into(mImageThmbNail);
        Log.d(LOG_TAG, "1st camera load");
        //mImageViewFileName = myPhoto.getFileName();
        // Following 2 lines don't bring the image
        // Get the thumbnail image
        //Bitmap thumbImage = ThumbnailUtils.extractThumbnail(myPhoto.getImageBitmap(),THUMBSIZE,
        //        THUMBSIZE);
//        try {
//            Bitmap thumbImage =
//                    ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mCurrentPhotoPath),
//                            THUMBSIZE, THUMBSIZE);
//            mImageThmbNail.setImageBitmap(thumbImage);
//        } catch (Exception e) {
//            Log.e(LOG_TAG, "Unable to create bitmap image");
//        }

        Glide.with(context).load(mCurrentPhotoPath).into(mImageView);
        Log.d(LOG_TAG, "2nd camera load");
    }


//    // onActivityResult called in Fragment Camera
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            // mImageView.setImageURI(mPhotoUri);
//            Context context = this; // or AddEditWallabyActivity.this; - redundant way
//            //Glide.with(context).load(mPhotoUri).into(mImageView);
//            Glide.with(context).load(mCurrentPhotoPath).into(mImageView);
//        } else {
//            if (resultCode == RESULT_CANCELED) {
//                // user cancelled the image capture
//                //TODO delete file path from the DB
//            }
//        }
//
//    }
//
//
//    public boolean checkCameraPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
//                    REQUEST_CAMERA_PERMISSION);
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        Log.v("grantResults", Integer.toString(grantResults[0]));
//        Log.v("requestCode", Integer.toString(requestCode));
//        if (requestCode == REQUEST_CAMERA_PERMISSION) {
//            if (grantResults.length <= 0) {
//                Log.i(LOG_TAG, "User interaction was cancelled");
//                mTakePictureButton.setEnabled(true);
//            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.v("serendipity", Integer.toString(grantResults[0]));
//                mTakePictureButton.setEnabled(true);
//                Toast.makeText(getApplicationContext(), "Camera permission granted, click on 'Take Picture' button ", Toast.LENGTH_LONG).show();
//            } else {
//                mTakePictureButton.setEnabled(false);
//                Toast.makeText(getApplicationContext(), "Camera permission not granted. Unable to take picture unless Permission given.", Toast.LENGTH_LONG).show();
//            }
//        }
//    }


    private void showDeleteConfirmationDialogFrag() {
        DialogFragment df = new DialogFragDeleteConfirmation();
        df.show(getSupportFragmentManager(), "Delete Record");
        //deleteRecord();
    }

     public void onDeleteClick(){
         //TODO Also need to delete jpg file stored in folder
         //TODO This should be done before filename removed from DB
        deleteRecord();
    }

    // delete Record from DB
    public void deleteRecord(){
        if (mCurrentRecordUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentRecordUri, null, null);
            //TODO need to also delete the file that the filename in the db points to!!
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_record_failure),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_record_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }


    private void showUnsavedChangesDialogFrag() {
        DialogFragment dfus = new DialogFragUnsavedChanges();
        // update record
        dfus.show(getSupportFragmentManager(), "Unsaved Changes Dialog Fragement");
    }

    public void onDiscardClick() {
        //user click discard. Navigate up to parent activity
        if (mHomeChecked){  // at Home Checkmark
            NavUtils.navigateUpFromSameTask(AddEditWallabyActivity.this);
        }
        if (!mHomeChecked) { // at BackPressed
            finish();
        }
    }





}
