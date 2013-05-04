package net.wildmansden.NDS;

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
 
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class NewsrackActivity extends ListActivity{
	// Connection detector
	ConnectionDetector cd;
	
	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
	// Create JSON Parser object
	JSONParser jsonParser = new JSONParser();
	
	ArrayList<HashMap<String, String>> papersList;
	
	// stories JSONArray
	JSONArray papers = null;
	
	// Newsrack id
	String newsrack_id, newsrack_name;
	
	// feeds JSON url
	// id - should be posted as GET params to get feeds list (ex: id = 5)
	private static final String URL_PAPERS = "http://android.wildmansden.net/resources/papers.php";
	
	// All JSON node names
	private static final String TAG_PAPERS = "papers";
	private static final String TAG_ID = "id";
	private static final String TAG_TITLE = "name";
	private static final String TAG_DESCRIPTION = "tagline";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		cd = new ConnectionDetector(getApplicationContext());
		
		// Check for internet connection
		if (!cd.isConnectingToInternet()) {
			//Internet Connection is not present
			alert.showAlertDialog(NewsrackActivity.this, "Internet Connection Error", "Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
				
		// Get newsrack id
		Intent i = getIntent();
		newsrack_id = i.getStringExtra("newsrack_id");
		
		// Hashmap for ListView
		papersList = new ArrayList<HashMap<String, String>>();
		
		// Load feeds in Background Thread
		new LoadPapers().execute();
		
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
				Intent i = new Intent(getApplicationContext(), PaperActivity.class);
				
				// to get paper information both newsrack id and paper id are needed
				String newsrack_id = ((TextView) view.findViewById(R.id.newsrack_id)).getText().toString();
				String paper_id = ((TextView) view.findViewById(R.id.paper_id)).getText().toString();
				
				Toast.makeText(getApplicationContext(), "Newsrack ID: " + newsrack_id + ", Paper ID: " + paper_id, Toast.LENGTH_SHORT).show();
				
				i.putExtra("newsrack_id", newsrack_id);
				i.putExtra("paper_id", paper_id);
				
				startActivity(i);
			}
		});
		
	}

	/**
	 * Background Async Task to Load all papers under one Newsrack
	 */
	class LoadPapers extends AsyncTask<String, String, String> {
		
		/**
		 * Before starting background thread Show Progress Dialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NewsrackActivity.this);
			pDialog.setMessage("Loading Papers...");
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
			params.add(new BasicNameValuePair(TAG_ID, newsrack_id));
			
			//getting JSON string from URL
			String json = jsonParser.makeHttpRequest(URL_PAPERS, "GET", params);
			
			// Check your log cat for JSON response
			Log.d("Paper List JSON: ", json);
			
			try {
				JSONObject jObj = new JSONObject(json);
				if (jObj != null) {
					String newsrack_id = jObj.getString(TAG_ID);
					newsrack_name = jObj.getString("newsrack");
					papers = jObj.getJSONArray(TAG_PAPERS);
					
					if (papers != null) {
						// loop through all papers
						for (int i = 0; i < papers.length(); i++) {
							JSONObject c = papers.getJSONObject(i);
							
							// Store each json item in variable
							String paper_id = c.getString(TAG_ID);
							String paper_title = c.getString(TAG_TITLE);
							String paper_description = c.getString(TAG_DESCRIPTION);
							
							// create new HashMap
							HashMap<String, String> map = new HashMap<String, String>();
							
							// add each child node to HashMap key => value
							map.put("newsrack_id", newsrack_id);
							map.put(TAG_ID, paper_id);
							map.put(TAG_TITLE, paper_title);
							map.put(TAG_DESCRIPTION, paper_description);
							
							// add HashList to ArrayList
							papersList.add(map);
						}
					} else {
						Log.d("Papers: ", "null");
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
					ListAdapter adapter = new SimpleAdapter(NewsrackActivity.this, papersList, R.layout.list_papers, 
							new String[] { "newsrack_id", TAG_ID, TAG_TITLE, TAG_DESCRIPTION },
							new int[] {R.id.newsrack_id, R.id.paper_id, R.id.paper_title, R.id.paper_description });
					// update listview
					setListAdapter(adapter);
					// Change activity Title with Newsrack name
					setTitle(newsrack_name);
				}
			});
		}
	}
}