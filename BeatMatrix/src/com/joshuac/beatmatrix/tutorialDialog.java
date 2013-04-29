package com.joshuac.beatmatrix;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.*;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import com.joshuac.beatmatrix.tutorialDialog.OnTutorialNextSelectedListener;

public class tutorialDialog extends DialogFragment {

	private static Context context;
	
	OnTutorialNextSelectedListener mCallback;
	
    //Container Activity must implement this interface
	//fragment can deliver messages to an Activity by calling 
	//the onFileSelected() method (or other methods in this interface) 
	//using the mCallback instance of the OnChooseFileSelectedListener interface
    public interface OnTutorialNextSelectedListener {
        public void onTutorialNextSelected();
    }
    
    //creates a new instance of the dialog
	public static tutorialDialog newInstance(int title) {
		tutorialDialog frag = new tutorialDialog();
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
    	
    	
    	System.out.println("Called onCreateDialog");
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	LayoutInflater inflater = getActivity().getLayoutInflater();
    	 View v = inflater.inflate(R.layout.tutorial_layout, null);   	 
	  
 	   
        builder.setTitle(R.string.tutorialTitle)
    		.setView(v/*inflater.inflate(R.layout.song_edit_layout, null)*/)
    	    // Add action buttons
    		.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
//            	   LayoutInflater inflater = (LayoutInflater) context
  //          	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            	   mCallback.onTutorialNextSelected();
               }
           })
           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   tutorialDialog.this.getDialog().cancel();
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
            mCallback = (OnTutorialNextSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTutorialNextSelectedListener");
        }
    }//end onAttach
    
}//end ChooseFileDialog
