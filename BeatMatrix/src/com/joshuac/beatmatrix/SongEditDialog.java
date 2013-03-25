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

import java.io.File;
import java.util.ArrayList;

import com.joshuac.beatmatrix.SongEditDialog.OnSongEditSelectedListener;

public class SongEditDialog extends DialogFragment {

	private static Context context;
	
	private float start_time, end_time;
	
	OnSongEditSelectedListener mCallback;
	
    //Container Activity must implement this interface
	//fragment can deliver messages to an Activity by calling 
	//the onFileSelected() method (or other methods in this interface) 
	//using the mCallback instance of the OnChooseFileSelectedListener interface
    public interface OnSongEditSelectedListener {
        public void onEditInfoSelected(float start_time, float end_time);
    }
/*
    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
    {

        public void onStopTrackingTouch(SeekBar bar)
        {
            int value = bar.getProgress(); // the value of the seekBar progress
        }

        public void onStartTrackingTouch(SeekBar bar)
        {

        }

        public void onProgressChanged(SeekBar bar,
                int paramInt, boolean paramBoolean)
        {
            textView.setText("" + paramInt + "%"); // here in textView the percent will be shown
        }
    });*/
    
    //creates a new instance of the dialog
	public static SongEditDialog newInstance(int title) {
		SongEditDialog frag = new SongEditDialog();
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
    	LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle(R.string.songEditDialogTitle)
    		.setView(inflater.inflate(R.layout.song_edit_layout, null))
    	    // Add action buttons
    		.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
                   // sign in the user ...
            	   LayoutInflater inflater = (LayoutInflater) context
            	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            	   View v = inflater.inflate(R.layout.song_edit_layout, null);
            	   SeekBar StartSeek = (SeekBar)v.findViewById(R.id.StartSeek);
            	   SeekBar EndSeek = (SeekBar)v.findViewById(R.id.EndSeek);
            	   start_time = StartSeek.getProgress();
            	   end_time = EndSeek.getProgress();
            	   mCallback.onEditInfoSelected(start_time, end_time);
               }
           })
           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   SongEditDialog.this.getDialog().cancel();
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
            mCallback = (OnSongEditSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSongEditSelectedListener");
        }
    }//end onAttach
    
}//end ChooseFileDialog
