package net.wildmansden.NDS.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import net.wildmansden.NDS.R;

public class AlertDialogManager {
	/**
	 * Function to display simple Alert Dialog
	 * @param context - application context
	 * @param title - alert dialog title
	 * @param message - alert message
	 * @param status - success/failure (used to set icon)
	 * 				 - pass null if no icon
	 */
	@SuppressWarnings("deprecation")
	
	public void showAlertDialog(Context context, String title, String message, Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		
		// Set Dialog Title
		alertDialog.setTitle(title);
		
		// Set Dialog Message
		alertDialog.setMessage(message);
		
		// Set Dialog Status
		if(status != null)
			alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
		
		// Set OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		// Show Alert Message
		alertDialog.show();
	}

}
