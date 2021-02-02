package com.babarehner.wallaby;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import java.io.ByteArrayOutputStream;

import static com.babarehner.wallaby.AddEditWallabyActivity.LOG_TAG;

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


// Utility may not be a good name for this but the class is used for image/bitmap/byte manipulation
public final class Utility {

    static final String LOG_TAG = AddEditWallabyActivity.class.getSimpleName();

    public static String filename;
    public static Uri cardUri;

    // Private constructor to preven instantiation
    private Utility() {
        throw new UnsupportedOperationException();
    }

    // Convert the image to byte array and compress in order to store a blob image
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        } catch (Exception e) {
            Log.e(LOG_TAG, " in getBytes(Bitmap bitmap");
        }
        return byteArrayOutputStream.toByteArray();
    }


    // Convert byte array to bitmap image
    public static Bitmap getBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }


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

}
