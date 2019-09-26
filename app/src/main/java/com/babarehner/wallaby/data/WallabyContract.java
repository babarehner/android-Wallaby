package com.babarehner.wallaby.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Project Name: Wallaby
 * <p>
 * Copyright 8/23/19 by Mike Rehner
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

public class WallabyContract {

    // To prevent someone from accidentally instantiating thecontract class
    private WallabyContract() {
    }

    // use package name for convenience for the Content Authority
    static final String WALLABY_AUTHORITY = "com.babarehner.wallaby";

    // Use WALLABY_AUTHORITY to create the base of all Uri's which apps use
    // to contact the content provider
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + WALLABY_AUTHORITY);
    static final String PATH_WALLABY_TABLE_NAME = "TWallaby";

    // Inner class that defines WALLABY table and columns
    public static final class WallabyTableConstants implements BaseColumns {

        // MIME type of the (@link #CONTENT_URI for a stuff database table
        static final String WALLABYLIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + WALLABY_AUTHORITY + "/" + PATH_WALLABY_TABLE_NAME;
        // MIME type of the (@link #CONTENT_URI for a single record
        static final String WALLABY_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + WALLABY_AUTHORITY + "/" + PATH_WALLABY_TABLE_NAME;
        // Content URI to access the table data in the provider
        public static final Uri WALLABY_URI = Uri.withAppendedPath(BASE_CONTENT_URI,
                PATH_WALLABY_TABLE_NAME);

        static final String WALLABY_TABLE_NAME = "TWallaby";

        // the globals and the columns
        public static final String _ID = BaseColumns._ID;
        public static final String C_PIC = "CPic";
        public static final String C_CARD_NAME = "CCardName";
        public static final String C_DATE = "CDate";
    }
}
