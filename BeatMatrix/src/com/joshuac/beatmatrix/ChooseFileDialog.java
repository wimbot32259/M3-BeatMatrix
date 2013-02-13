package com.joshuac.beatmatrix;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import java.io.File;
import java.util.ArrayList;

public class ChooseFileDialog extends DialogFragment {
	
	private ArrayList<File> fileList = null; //files to show to the user 	(canonical paths)
	private String[] fileSelection; 	//selection of files to display (names)
	private File baseDir = new File(Environment.getExternalStorageDirectory().toString());
	private File chosenFile; 	//File the user selected
	//private static final int MAX_DIALOG_LENGTH = 1000;
	private static Context context;
	
	OnChooseFileSelectedListener mCallback;
	
    //Container Activity must implement this interface
	//fragment can deliver messages to an Activity by calling 
	//the onFileSelected() method (or other methods in this interface) 
	//using the mCallback instance of the OnChooseFileSelectedListener interface
    public interface OnChooseFileSelectedListener {
        public void onFileSelected(File f);
    }

    //creates a new instance of the dialog
	public static ChooseFileDialog newInstance(int title) {
		ChooseFileDialog frag = new ChooseFileDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }
	
	public static void setContext(Context c){
		context = c;
	}

	//recursively generate a list of mp3 files to add to the dialog fragment
	private ArrayList<File> generateFileList(final File baseDir) {

		ArrayList<File> files; //return
		if(baseDir.list() == null) {
			files = new ArrayList<File>(0);
		} else {
			files = new ArrayList<File>(baseDir.list().length);
			for(File file : baseDir.listFiles()) {
				if(file.isDirectory()) {
					files.addAll(generateFileList(file));
				} else {
					//if file, then check extension
					int periodIndex= file.toString().lastIndexOf('.');
					if(periodIndex != -1){
						String ext = file.toString().substring(periodIndex);
						if(ext.equals(".mp3")){
								files.add(file);
						}//if try-catch
					}
				}//else
			}//if (file.isDirectory())
		}
		return files;	
	}//end generateFileList
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
    	//only load fileList once
    	if(fileList == null)
    		fileList = generateFileList(baseDir);

        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Choose your file");
        
        //if file list is still null, then there are no music files
        if(fileList == null) {
            dialog = builder.create();
            return dialog;
        }
        
        //show only the names in the dialog
        fileSelection = new String[fileList.size()];
        for(int i = 0; i < fileSelection.length; i++){
        	fileSelection[i] = (String) fileList.get(i).getName();
        }
        
        //build dialog
        builder.setItems(fileSelection, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            		chosenFile = fileList.get(which);
            		mCallback.onFileSelected(chosenFile);
                //you can do stuff with the file here too
            }
        });
        
        dialog = builder.show();
        return dialog;
    }//end onCreateDialog
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnChooseFileSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnChooseFileSelectedListener");
        }
    }//end onAttach
    
}//end ChooseFileDialog
