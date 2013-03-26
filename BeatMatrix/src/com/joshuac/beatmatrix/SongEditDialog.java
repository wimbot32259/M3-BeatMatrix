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

import com.joshuac.beatmatrix.SongEditDialog.OnSongEditSelectedListener;

public class SongEditDialog extends DialogFragment {

	private static Context context;
	
	private double start_time, end_time, speed;
	private float volume;
	private static int buttonId;
	
	//Text views
	private TextView SpeedText, VolumeText, StartText, EndText;
	
	OnSongEditSelectedListener mCallback;
	
    //Container Activity must implement this interface
	//fragment can deliver messages to an Activity by calling 
	//the onFileSelected() method (or other methods in this interface) 
	//using the mCallback instance of the OnChooseFileSelectedListener interface
    public interface OnSongEditSelectedListener {
        public void onEditInfoSelected(double start_time, double end_time, float volume, double speed);
    }
    
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
	
	public static void setButtonId(int Id) {
		buttonId = Id;
	}
	
	private OnSeekBarChangeListener startSeekBarListener = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
			start_time = seekBar.getProgress();
			System.out.println("Start time: " + start_time);
			StartText.setText("Start Time: " + start_time);

		}
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}
		public void onStopTrackingTouch(SeekBar seekBar) {
			
		}
	};
	private OnSeekBarChangeListener endSeekBarListener = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
			end_time = seekBar.getProgress();
			System.out.println("End time: " + end_time);
			EndText.setText("End Time: " + end_time);			
		}
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}
		public void onStopTrackingTouch(SeekBar seekBar) {
			
		}
	};
	private OnSeekBarChangeListener volSeekBarListener = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
			volume = seekBar.getProgress();
			System.out.println("Volume: " + volume);
			VolumeText.setText("Volume: " + volume);
		}
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}
		public void onStopTrackingTouch(SeekBar seekBar) {
			
		}
	};
	private OnSeekBarChangeListener speedSeekBarListener = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
			speed = seekBar.getProgress();
			System.out.println("Speed: " + speed);
			SpeedText.setText("Speed: " + speed);
		}
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}
		public void onStopTrackingTouch(SeekBar seekBar) {
			
		}
	};
	
		
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
    	
    	System.out.println("Called onCreateDialog");
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	LayoutInflater inflater = getActivity().getLayoutInflater();
    	 View v = inflater.inflate(R.layout.song_edit_layout, null);   	 
	  
       //Set start listener
 	   SeekBar StartSeek = (SeekBar)v.findViewById(R.id.StartSeek);
 	   StartSeek.setOnSeekBarChangeListener(startSeekBarListener);
 	   StartText = (TextView) v.findViewById(R.id.StartText);
  	 
 	   //Set end listener
 	   SeekBar EndSeek = (SeekBar)v.findViewById(R.id.EndSeek);
 	   EndSeek.setOnSeekBarChangeListener(endSeekBarListener);
 	   EndText = (TextView) v.findViewById(R.id.EndText);

 	   //Set volume listener
 	   SeekBar VolSeek = (SeekBar)v.findViewById(R.id.VolSeek);
 	   VolSeek.setOnSeekBarChangeListener(volSeekBarListener);
 	   VolumeText = (TextView) v.findViewById(R.id.VolumeText);
 	   
 	   //Set speed listener
 	   SeekBar SpeedSeek = (SeekBar)v.findViewById(R.id.SpeedSeek);
 	   SpeedSeek.setOnSeekBarChangeListener(speedSeekBarListener);
 	   SpeedText = (TextView) v.findViewById(R.id.SpeedText);
 	   
        builder.setTitle(R.string.songEditDialogTitle)
    		.setView(v/*inflater.inflate(R.layout.song_edit_layout, null)*/)
    	    // Add action buttons
    		.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
                   // sign in the user ...
//            	   LayoutInflater inflater = (LayoutInflater) context
  //          	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            	   mCallback.onEditInfoSelected(start_time, end_time, volume, speed);
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
