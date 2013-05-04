package net.wildmansden.NDS;

import net.wildmansden.NDS.helper.AlertDialogManager;
import net.wildmansden.NDS.helper.ConnectionDetector;
import net.wildmansden.NDS.helper.JSONParser;
import net.wildmansden.NDS.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import android.os.Bundle;

public class NewsstandActivity extends ListActivity {
	//Connection Detector
	ConnectionDetector cd;
	
	//Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();
	
	//Progress Dialog
	private ProgressDialog pDialog;
	
	//JSON Parser object
	JSONParser jsonParser = new JSONParser();
	
	ArrayList<HashMap<String, String>> newsrackList;
	
	//newsracks JSONArray
	JSONArray newsracks = null;
	
	//newsracks JSON url
	private static final String URL_NEWSRACKS = "http://android.wildmansden.net/resources/newsracks.php";
	
	// All JSON node names
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "newsrack";
	private static final String TAG_DESCRIPTION = "description";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		cd = new ConnectionDetector(getApplicationContext());
		
		// Check for internet connection
		if (!cd.isConnectingToInternet()) {
			//Internet Connection is not present
			alert.showAlertDialog(NewsstandActivity.this, "Internet Connection Error", "Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		
		// Hashmap for ListView
		newsrackList = new ArrayList<HashMap<String, String>>();
		
		// Load Channels JSON in background thread
		new LoadNewsracks().execute();
		
		// get ListView
		ListView lv = getListView();
		
		/**
		 * Listview item click listener
		 * FeedListActivity will be launched by passing channel id
		 **/
		lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
				// on selecting a single channel, FeedListActivity will be launched to show feeds inside the album
				Intent i = new Intent(getApplicationContext(), NewsrackActivity.class);
				
				// send the channel id to Newsrack activity to get list of papers on that newsrack
				String newsrack_id = ((TextView) view.findViewById(R.id.newsrack_id)).getText().toString();
				i.putExtra("newsrack_id", newsrack_id);
				
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
			case R.id.newsstand:
				newsstandMenuItem();
				break;
			case R.id.settings:
				settingsMenuItem();
				break;
			case R.id.about:
				aboutMenuItem();
				break;				
		}
		
		return true;
		
	}
	
	private void newsstandMenuItem() {
		Intent i = new Intent(getApplicationContext(), NewsstandActivity.class);
		
		startActivity(i);
	}
	
	private void settingsMenuItem() {
		Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
		
		startActivity(i);
	}
	
	private void aboutMenuItem() {
		new AlertDialog.Builder(this)
		.setTitle("About")
		.setMessage("This is an About AlertDialog")
		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		}).show();
		
	}
	
	/**
	 * Background Async Task to Load all Channels by making http request
	 */
	class LoadNewsracks extends AsyncTask<String, String, String> {
		
		/**
		 * Before starting background thread Show Progress Dialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NewsstandActivity.this);
			pDialog.setMessage("Listing Newsracks...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		/**
		 * get Newsracks JSON
		 */
		protected String doInBackground(String... args) {
			// Build Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			// Get JSON string from URL
			String json = jsonParser.makeHttpRequest(URL_NEWSRACKS, "GET", params);
			
			// Check your log cat for JSON response
			Log.d("Newsracks JSON: ", "> " + json);
			
			try {
				newsracks = new JSONArray(json);
				
				if (newsracks != null) {
					// loop through all channels
					for (int i=0; i < newsracks.length(); i++) {
						JSONObject c = newsracks.getJSONObject(i);
						
						// store each json item values in variable
						String id = c.getString(TAG_ID);
						String newsrack = c.getString(TAG_NAME);
						String description = c.getString(TAG_DESCRIPTION);
						
						// create new HashMap
						HashMap<String, String> map = new HashMap<String, String>();
						
						// add each child node to HashMap key => value
						map.put(TAG_ID, id);
						map.put(TAG_NAME, newsrack);
						map.put(TAG_DESCRIPTION, description);						
						
						// add HashList to ArrayList
						newsrackList.add(map);
					}
				}else {
					Log.d("Newsracks: ", "null");
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		/**
		 * After completing background task dismiss the progress dialog
		 */
		protected void onPostExecute(String file_url) {
			//dismiss the dialog after getting all channels
			pDialog.dismiss();
			//update UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Update parsed JSON data into ListView
					 */
					ListAdapter adapter = new SimpleAdapter(NewsstandActivity.this, newsrackList, R.layout.list_newsracks, 
							new String[] { TAG_ID, TAG_NAME, TAG_DESCRIPTION }, 
							new int[] { R.id.newsrack_id, R.id.newsrack_name, R.id.newsrack_description });
					
					// update listview
					setListAdapter(adapter);
					
					setTitle("Newsstand");
				}
			});
		}
	}
}
