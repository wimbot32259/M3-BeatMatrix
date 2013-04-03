package com.joshuac.beatmatrix;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;

import java.io.File;
import java.util.ArrayList;

public class SongSelectDialog extends DialogFragment {
	
	private static Context context;
	
	OnSongSelectedListener mCallback;
	
    //Container Activity must implement this interface
	//fragment can deliver messages to an Activity by calling 
	//the onFileSelected() method (or other methods in this interface) 
	//using the mCallback instance of the OnChooseFileSelectedListener interface
    public interface OnSongSelectedListener {
        public void onSongSelected(int buttonId);
    }

    //creates a new instance of the dialog
	public static SongSelectDialog newInstance(int title) {
		SongSelectDialog frag = new SongSelectDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }
	
	public static void setContext(Context c){
		context = c;
	}
		
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_buttonId)
               .setItems(R.array.buttonIds, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                	   mCallback.onSongSelected(which);
                   // The 'which' argument contains the index position
                   // of the selected item
               }
        });
        return builder.create();
    }//end onCreateDialog
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnSongSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSongSelectedListener");
        }
    }//end onAttach
    
}//end ChooseFileDialog
