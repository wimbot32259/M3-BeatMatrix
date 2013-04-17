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
	
	private static double start_time, end_time;
	private static double volume, speed, bass, treble;
	private static int buttonId;
	private static double songLength;
	
	//Text views
	private TextView SpeedText, VolumeText, StartText, EndText, BassText, TrebleText;
	
	OnSongEditSelectedListener mCallback;
	
    //Container Activity must implement this interface
	//fragment can deliver messages to an Activity by calling 
	//the onFileSelected() method (or other methods in this interface) 
	//using the mCallback instance of the OnChooseFileSelectedListener interface
    public interface OnSongEditSelectedListener {
        public void onEditInfoSelected(double start_time, double end_time, double volume, double speed, double bass, double treble);
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
	
	public static void initialize(int Id, double length, double curr_start, double curr_end, double curr_speed, double curr_volume, double curr_bass, double curr_treble) {
		buttonId = Id;
		songLength = length;
		start_time = curr_start;
		end_time = curr_end;
		volume = curr_volume;
		speed = curr_speed;
		bass = curr_bass;
		treble = curr_treble;
	}
	
	private OnSeekBarChangeListener startSeekBarListener = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
			if (start_time > end_time) {
				start_time = end_time;
			} else {
				start_time = (seekBar.getProgress()/100.0)*songLength;
			}
			System.out.println("Start time: " + start_time);
			StartText.setText("Start Time: " + (int)(start_time/60) + ":" + (int)(start_time%60) + "." + (int)(((start_time%60)/0.01)%60));
		}
		public void onStartTrackingTouch(SeekBar seekBar) {
		}
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (start_time > end_time) {
				start_time = end_time;
			}
			seekBar.setProgress((int)((start_time/songLength)*100));
		}
	};
	private OnSeekBarChangeListener endSeekBarListener = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
			if (end_time < start_time) {
				end_time = start_time;
			} else {
				end_time = (seekBar.getProgress()/100.0)*songLength;
			}
			System.out.println("End time: " + end_time);
			EndText.setText("End Time: " + (int)(end_time/60) + ":" + (int)(end_time%60) + "." + (int)(((end_time%60)/0.01)%60));
		}
		public void onStartTrackingTouch(SeekBar seekBar) {
		}
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (end_time < start_time) {
				end_time = start_time;
			}
			seekBar.setProgress((int)((end_time/songLength)*100));
		}
	};
	private OnSeekBarChangeListener volSeekBarListener = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
			volume = (seekBar.getProgress()/100.0);
			System.out.println("Volume: " + volume);
			VolumeText.setText("Volume: " + volume);
		}
		public void onStartTrackingTouch(SeekBar seekBar) {
		}
		public void onStopTrackingTouch(SeekBar seekBar) {
			seekBar.setProgress((int)(volume*100));
		}
	};
	private OnSeekBarChangeListener speedSeekBarListener = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
			speed = (seekBar.getProgress()/50.0);
			System.out.println("Speed: " + speed);
			SpeedText.setText("Speed: " + speed);
		}
		public void onStartTrackingTouch(SeekBar seekBar) {
		}
		public void onStopTrackingTouch(SeekBar seekBar) {
			seekBar.setProgress((int)(speed*50));
		}
	};
	
	private OnSeekBarChangeListener bassSeekBarListener = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
			bass = ((seekBar.getProgress()-50.0)/2.5);
			System.out.println("Bass: " + bass);
			BassText.setText("Bass: " + bass + " dB");
		}
		public void onStartTrackingTouch(SeekBar seekBar) {
		}
		public void onStopTrackingTouch(SeekBar seekBar) {
			seekBar.setProgress((int)((bass*2.5)+50));
		}
	};
	
	private OnSeekBarChangeListener trebleSeekBarListener = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
			treble = ((seekBar.getProgress()-50.0)/2.5);
			System.out.println("Treble: " + treble);
			TrebleText.setText("Treble: " + treble + " dB");
		}
		public void onStartTrackingTouch(SeekBar seekBar) {
		}
		public void onStopTrackingTouch(SeekBar seekBar) {
			seekBar.setProgress((int)((treble*2.5)+50));
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
 	   System.out.println("about to set progress");
 	   if (start_time == 0) {
 		   start_time = 0;
 	   }
 	   StartSeek.setProgress((int)((start_time/songLength)*100));
 	   System.out.println("set progress");
 	   StartSeek.setOnSeekBarChangeListener(startSeekBarListener);
 	   System.out.println("set listener");
 	   StartText = (TextView) v.findViewById(R.id.StartText);
 	   StartText.setText("Start Time: " + (int)(start_time/60) + ":" + (int)(start_time%60) + "." + (int)(((start_time%60)/0.01)%60));
  	 
 	   //Set end listener
 	   SeekBar EndSeek = (SeekBar)v.findViewById(R.id.EndSeek);
 	   if (end_time == 0) {
 		   end_time = songLength;
 	   }
 	   EndSeek.setProgress((int)((end_time/songLength)*100));
 	   EndSeek.setOnSeekBarChangeListener(endSeekBarListener);
 	   EndText = (TextView) v.findViewById(R.id.EndText);
 	   EndText.setText("End Time: " + (int)(end_time/60) + ":" + (int)(end_time%60) + "." + (int)(((end_time%60)/0.01)%60));

 	   //Set volume listener
 	   SeekBar VolSeek = (SeekBar)v.findViewById(R.id.VolSeek);
 	   if (volume == 0) {
 		   volume = 1;
 	   }
 	   VolSeek.setProgress((int)(volume*100));
 	   VolSeek.setOnSeekBarChangeListener(volSeekBarListener);
 	   VolumeText = (TextView) v.findViewById(R.id.VolumeText);
 	   VolumeText.setText("Volume: " + volume);
 	   
 	   //Set speed listener
 	   SeekBar SpeedSeek = (SeekBar)v.findViewById(R.id.SpeedSeek);
 	   if (speed == 0) {
 		   speed = 1;
 	   }
 	   SpeedSeek.setProgress((int)(speed*50));
 	   SpeedSeek.setOnSeekBarChangeListener(speedSeekBarListener);
 	   SpeedText = (TextView) v.findViewById(R.id.SpeedText);
 	   SpeedText.setText("Speed: " + speed);
 	   
 	   //Set treble listener
 	   SeekBar TrebleSeek = (SeekBar)v.findViewById(R.id.TrebleSeek);
 	   if (treble == 0) {
 		   treble = 0;
 	   }
 	   TrebleSeek.setProgress((int)((treble*2.5)+50));
 	   TrebleSeek.setOnSeekBarChangeListener(trebleSeekBarListener);
 	   TrebleText = (TextView) v.findViewById(R.id.TrebleText);
 	   TrebleText.setText("Treble: " + treble + " dB");
 	   
 	   //Set bass listener
 	   SeekBar BassSeek = (SeekBar)v.findViewById(R.id.BassSeek);
 	   if (bass == 0) {
 		   bass = 0;
 	   }
 	   BassSeek.setProgress((int)((bass*2.5)+50));
 	   BassSeek.setOnSeekBarChangeListener(bassSeekBarListener);
 	   BassText = (TextView) v.findViewById(R.id.BassText);
 	   BassText.setText("Bass: " + bass + " dB");
 	   
        builder.setTitle(R.string.songEditDialogTitle)
    		.setView(v/*inflater.inflate(R.layout.song_edit_layout, null)*/)
    	    // Add action buttons
    		.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
//            	   LayoutInflater inflater = (LayoutInflater) context
  //          	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            	   mCallback.onEditInfoSelected(start_time, end_time, volume, speed, bass, treble);
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
