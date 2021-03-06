package net.wildmansden.NDS;

import net.wildmansden.NDS.R;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class StoryActivity extends Activity {
	
	WebView storyView;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_story);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		Intent in = getIntent();
		String page_url = in.getStringExtra("story_link");
		
		storyView = (WebView) findViewById(R.id.storyview);
		storyView.getSettings().setJavaScriptEnabled(true);
		storyView.loadUrl(page_url);
		
		storyView.setWebViewClient(new DisplayWebPageActivityClient());
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && storyView.canGoBack()) {
			storyView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode,  event);
	}
	
	private class DisplayWebPageActivityClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
}
	
	
