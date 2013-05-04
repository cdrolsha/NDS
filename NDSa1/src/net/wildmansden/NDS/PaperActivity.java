package net.wildmansden.NDS;

//import net.wildmansden.NDS.PaperActivity.LoadStories;
import net.wildmansden.NDS.helper.AlertDialogManager;
import net.wildmansden.NDS.helper.ConnectionDetector;
import net.wildmansden.NDS.helper.JSONParser;
import net.wildmansden.NDS.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;



public class PaperActivity extends ListActivity{
	
	// Connection detector
	ConnectionDetector cd;
	
	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
	// Create JSON Parser object
	JSONParser jsonParser = new JSONParser();
	
	ArrayList<HashMap<String, String>> storiesList;
	
	// feeds JSONArray
	JSONArray stories = null;
	
	// Newsrack and paper id
	String newsrack_id = null;
	String paper_id = null;
	String paper_name = null;
	
	String story_id, story_title, story_date, story_summary, story_link;
	
	// feeds JSON url
	// id - should be posted as GET params to get feeds list (ex: id = 5)
	private static final String URL_STORIES= "http://android.wildmansden.net/resources/stories.php";
	
	// All JSON node names
	private static final String TAG_STORIES = "stories";
	private static final String TAG_ID = "id";
	private static final String TAG_TITLE = "title";
	private static final String TAG_DATE = "date";
	private static final String TAG_SUMMARY = "summary";
	private static final String TAG_LINK = "link";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		cd = new ConnectionDetector(getApplicationContext());
		
		// Check for internet connection
		if (!cd.isConnectingToInternet()) {
			//Internet Connection is not present
			alert.showAlertDialog(PaperActivity.this, "Internet Connection Error", "Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
				
		// Get newsrack and paper id's
		Intent i = getIntent();
		newsrack_id = i.getStringExtra("newsrack_id");
		paper_id = i.getStringExtra("paper_id");
		
		// Hashmap for ListView
		storiesList = new ArrayList<HashMap<String, String>>();
		
		// Load feeds in Background Thread
		new LoadStories().execute();
		
		// get listview
		ListView lv = getListView();
		
		/**
		 * ListView on item click listener
		 * SingleFeedActivity will be launched by passing channel id, feed id
		 */
		lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
				// On selecting a single feed get feed information
				Intent i = new Intent(getApplicationContext(), StoryActivity.class);
				
				// to get the story, the story url is needed
				story_link = ((TextView) view.findViewById(R.id.story_link)).getText().toString();
				
				Toast.makeText(getApplicationContext(), "Story Link: " + story_link, Toast.LENGTH_SHORT).show();
				
				i.putExtra("story_link", story_link);
				
				startActivity(i);
			}
		});
		
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

	/**
	 * Background Async Task to Load all papers under one Newsrack
	 */
	class LoadStories extends AsyncTask<String, String, String> {
		
		/**
		 * Before starting background thread Show Progress Dialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(PaperActivity.this);
			pDialog.setMessage("Loading Stories...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		/**
		 * getting papers json and parsing
		 */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair>	params = new ArrayList<NameValuePair>();
			
			//post newsrack id as GET parameter
			params.add(new BasicNameValuePair("newsrack", newsrack_id));
			params.add(new BasicNameValuePair("paper", paper_id));
			
			//getting JSON string from URL
			String json = jsonParser.makeHttpRequest(URL_STORIES, "GET", params);
			
			// Check your log cat for JSON response
			Log.d("Story List JSON: ", json);
			
			try {
				JSONObject jObj = new JSONObject(json);
				if (jObj != null) {
					String paper_id = jObj.getString(TAG_ID);
					paper_name = jObj.getString("name");
					stories = jObj.getJSONArray(TAG_STORIES);
					
					if (stories != null) {
						// loop through all papers
						for (int i = 0; i < stories.length(); i++) {
							JSONObject c = stories.getJSONObject(i);
							
							// Store each json item in variable
							String story_id = c.getString(TAG_ID);
							String story_date = c.getString(TAG_DATE);
							String story_title = c.getString(TAG_TITLE);
							String story_summary = c.getString(TAG_SUMMARY);
							String story_link = c.getString(TAG_LINK);
							
							// create new HashMap
							HashMap<String, String> map = new HashMap<String, String>();
							
							// add each child node to HashMap key => value
							map.put("newsrack_id", newsrack_id);
							map.put("paper_id", paper_id);
							map.put(TAG_ID, story_id);
							map.put(TAG_DATE, story_date);
							map.put(TAG_TITLE, story_title);
							map.put(TAG_SUMMARY, story_summary);
							map.put(TAG_LINK, story_link);
							
							// add HashList to ArrayList
							storiesList.add(map);
						}
					} else {
						Log.d("Stories: ", "null");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		/**
		 * After completing background task Dismiss the progress dialog
		 */
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all papers
			pDialog.dismiss();
			// update UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Update parsed JSON data into ListView
					 */
					ListAdapter adapter = new SimpleAdapter(PaperActivity.this, storiesList, R.layout.list_stories, 
							new String[] { "newsrack_id", "paper_id", TAG_ID, TAG_DATE, TAG_TITLE, TAG_SUMMARY, TAG_LINK},
							new int[] {R.id.newsrack_id, R.id.paper_id, R.id.story_id, R.id.story_date, R.id.story_title, R.id.story_summary, R.id.story_link });
					// update listview
					setListAdapter(adapter);
					// Change activity Title with Paper name
					setTitle(paper_name);
				}
			});
		}
	}
}