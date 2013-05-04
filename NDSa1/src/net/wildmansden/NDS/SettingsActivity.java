package net.wildmansden.NDS;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		setTitle("Settings");
			
		}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.nav_menu, menu);
		return true;
	}		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()){
			case android.R.id.home:
				Intent i = new Intent(this, NewsstandActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				return true;
			case R.id.newsstand:
				i = new Intent(getApplicationContext(), NewsstandActivity.class);
				startActivity(i);
				return true;
			case R.id.settings:
				i = new Intent(getApplicationContext(), SettingsActivity.class);
				startActivity(i);
				return true;
			case R.id.about:
				new AlertDialog.Builder(this)
				.setTitle("About")
				.setMessage("This is an About AlertDialog")
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					}
				}).show();
				break;				
		}
		
		return true;
	
	}
}
