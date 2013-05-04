package net.wildmansden.NDS;

import net.wildmansden.NDS.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class StoryActivity extends Activity {
	
	WebView storyView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_story);
		
		Intent in = getIntent();
		String page_url = in.getStringExtra("story_link");
		
		storyView = (WebView) findViewById(R.id.storyview);
		storyView.getSettings().setJavaScriptEnabled(true);
		storyView.loadUrl(page_url);
		
		storyView.setWebViewClient(new DisplayWebPageActivityClient());
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
	
	
