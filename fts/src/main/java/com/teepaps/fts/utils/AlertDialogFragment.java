package com.teepaps.fts.utils;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * 
 * @author Ted Papaioannou
 * <p>
 * Modified from <a href="http://developer.android.com/reference/android/app/DialogFragment.html#AlertDialog">Alert Dialog</a>
 * <p>
 * Creates a dialog a general AlertDialog. The caller must implement the
 * positive and negative actions
 */
public class AlertDialogFragment extends DialogFragment {
	
    /**
     * Positive and negative button listener actions
     *
     */
    public interface AlertDialogOnClickListeners {
    	public void doPositiveClick();
    	public void doNegativeClick();
    }

	/**
	 * Listeners object for positive/negative selections
	 */
	private AlertDialogOnClickListeners onClickListeners;
	
    public static AlertDialogFragment newInstance(int icon, int title, 
    		int message, int posText, int negText) 
    {
        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        
        // Check if valid value supplied
        if (icon > 0) { 
        	args.putInt("icon", icon);
        }
        if (title > 0) { 
	        if (title > 0) args.putInt("title", title);
        }
        if (message > 0) { 
        	args.putInt("icon", icon);
	        if (title > 0) args.putInt("message", message);
        }
        if (posText > 0) { 
        	args.putInt("icon", icon);
        if (title > 0) args.putInt("posText", posText);
        }
        if (negText > 0) { 
	        if (title > 0) args.putInt("negText", negText);
        }

        frag.setArguments(args);
        return frag;
    }
    
    /**
     * Wrapper for no message
     * @return
     */
    public static AlertDialogFragment newInstance(int icon, int title, 
    		int posText, int negText) 
    {
        return newInstance(icon, title, -1, posText, negText);
    }
    
    /**
     * Wrapper for default negative negative
     * @return
     */
    public static AlertDialogFragment newInstance(int icon, int title, 
    		int posText) 
    {
        return newInstance(icon, title, -1, posText, -1);
    }
    
    /**
     * Wrapper for default negative negative
     * @return
     */
    public static AlertDialogFragment newInstance(int icon, int title) 
 {
		return newInstance(icon, title, -1, -1, -1);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int icon = getArguments().getInt("icon", -1);
		int title = getArguments().getInt("title", -1);
		int message = getArguments().getInt("message", -1);
		int posText = getArguments().getInt("posText", R.string.ok);
		int negText = getArguments().getInt("negText", R.string.cancel);

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
				getActivity())
				.setIcon(icon)
				.setTitle(title)
				.setPositiveButton(posText,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (onClickListeners != null) {
									onClickListeners.doPositiveClick();
								}
							}
						})
				.setNegativeButton(negText,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (onClickListeners != null) {
									onClickListeners.doNegativeClick();
								}
							}
						});

		// Check that a message was supplied
		if (message > 0) {
			dialogBuilder.setMessage(message);
		}

        return dialogBuilder.create();
    }

    /**
     * Sets the listeners for the positive and negative buttons
     * @param listeners
     * @return
     */
    public AlertDialogFragment setOnClickListners(
    		AlertDialogOnClickListeners listeners) 
    {
    	this.onClickListeners = listeners;
    	return this;
    }

}
