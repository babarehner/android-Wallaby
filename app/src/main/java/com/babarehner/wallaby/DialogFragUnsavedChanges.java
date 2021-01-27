package com.babarehner.wallaby;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;



/**
 * Project Name: Wallaby
 * <p>
 * Copyright 1/26/21 by Mike Rehner
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


public class DialogFragUnsavedChanges extends DialogFragment {

    public DialogFragUnsavedChanges() { }

    public DialogClickListener callBack;

    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        // b.setMessage("hi");
        b.setMessage(R.string.unsaved_changes_dialog_msg);

        b.setPositiveButton(R.string.discard, (dialog, which) -> callBack.onDiscardClick());
        b.setNegativeButton(R.string.keep_editing, (dialog, which) -> {
            if (dialog != null) { dialog.dismiss();}
        });
        // Create the AlertDialog object and return it
        return b.create();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Activity a;
        // if(context instanceof Activity){
        //     a = (Activity) context;

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            callBack = (DialogClickListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement DialogClickListener");
        }
    }


    public interface DialogClickListener {
        void onDiscardClick();
    }


}


