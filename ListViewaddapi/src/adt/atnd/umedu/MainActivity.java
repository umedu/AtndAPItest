package adt.atnd.umedu;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnScrollListener{
	ListView list;
	TextView ver;
	TextView name;
	TextView api;
	Button Btngetdata;
	ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
	JSONObject json;
	static Calendar calendar = Calendar.getInstance();

	// ListViewFooter
		private View mFooter;
		private TextView footerText;
		private ProgressBar footerProgressBar;
		

	// JSON Node Names
	private static final String TAG_EVENTS = "events";
	private static final String TAG_EVENT = "event";
	private static final String TAG_TITLE = "title";
	private static final String TAG_STARTED_AT = "started_at";
	private static final String TAG_ENDED_AT = "ended_at";
	private static final String TAG_PLACE = "place";

	JSONArray android = null;
	private String url;
	private String datef;
	private boolean isLast;
	
	static private DateFormat   format;
	JSONParser jParser = new JSONParser();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 年月日を取得
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		datef = sdf.format(date);
	    Log.d("date",datef);
		url = "http://api.atnd.org/eventatnd/event/?&ymd="+datef+"&count=10&order=2&format=json";

		setContentView(R.layout.activity_main);
		
		 
		oslist = new ArrayList<HashMap<String, String>>();

		Btngetdata = (Button) findViewById(R.id.getdata);
		Btngetdata.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				new JSONParse().execute();

			}
		});

	}
	String getFormatDate(String paramString)
	  {
		//TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));
	    if (paramString.equals("null")) {
	      return "-";
	    }
	    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	    Date localDate1;
		try
	    {
	      Date localDate2 = localSimpleDateFormat.parse(paramString);
	      localDate1 = localDate2;
	    }
	    catch (ParseException localParseException)
	    {
	      for (;;)
	      {
	        localParseException.printStackTrace();
	         localDate1 = null;
	      }
	    }
	    localSimpleDateFormat.applyPattern("yyyy/MM/dd(E) HH:mm");
	    return localSimpleDateFormat.format(localDate1);
	  }
	private class JSONParse extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;
		private JSONArray categories;
		private JSONArray category;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ver = (TextView) findViewById(R.id.vers);
			name = (TextView) findViewById(R.id.name);
			api = (TextView) findViewById(R.id.api);
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Getting Data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}

		@Override
		protected JSONObject doInBackground(String... args) {

			json = jParser.getJSONFromUrl(url);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			pDialog.dismiss();
			try {
				Log.d("json", json.toString());

//				  try {
//			            JSONObject rootObject = new JSONObject(json);
//			        	   JSONObject bodyObject = rootObject.getJSONObject("Body");
//			            JSONObject booksbooksearchObject = bodyObject.getJSONObject("BooksBookSearch");
//			            JSONObject itemsObject = booksbooksearchObject.getJSONObject("Items");
//			            JSONArray  itemArray = itemsObject.getJSONArray("Item");
//
//			            int count = itemArray.length();
//
//			            JSONObject[] bookObject = new JSONObject[count];
//
//			            for (int i=0; i<count; i++){
//			            	bookObject[i] = itemArray.getJSONObject(i);
//			            }
				/**
				 * add parce case1
				 */
				JSONArray localJSONArray = json.getJSONArray("events");
				JSONObject localJSONArray2 = localJSONArray.getJSONObject(0);
				JSONArray localJSONArray3 = localJSONArray2.getJSONArray("event");
				Log.d("localJSONArray3", ""+localJSONArray3);

			    int o =  json.getInt("results_available");

				JSONObject[] arrayOfJSONObject = new JSONObject[o];

				Log.d("results_available", ""+o);


				for (int j = 0; j < localJSONArray3.length(); j++) {

				arrayOfJSONObject[j] = localJSONArray3.getJSONObject(j);
		       // j++;
				//}
				Log.d("arrayOfJSONObject", ""+arrayOfJSONObject);
				HashMap<String, String> localHashMap = new HashMap<String, String>();


		         // int j = 0;
		          localHashMap.put("event_id", arrayOfJSONObject[j].getString("event_id"));
		          localHashMap.put("title", arrayOfJSONObject[j].getString("title"));
		          localHashMap.put("catch", arrayOfJSONObject[j].getString("catch"));
		          localHashMap.put("started_at", getFormatDate(arrayOfJSONObject[j].getString("started_at")));
		          localHashMap.put("ended_at", getFormatDate(arrayOfJSONObject[j].getString("ended_at")));
		          localHashMap.put("place", arrayOfJSONObject[j].getString("place"));
		          oslist.add(localHashMap);
		          j++;
		          Log.d("localHashMap", ""+localHashMap);
				}
				//ソート
				Collections.sort(oslist, new Comparator<HashMap<String, String>>() {
					@Override
					public int compare(HashMap<String, String> arg0,
							HashMap<String, String> arg1) {
						// TODO 自動生成されたメソッド・スタブ
						return ((String)arg0.toString()).compareTo((String)arg1.toString());
					}
		        });
				
				
				ListAdapter adapter = new SimpleAdapter(
						MainActivity.this, oslist, R.layout.list_v,
						new String[] { TAG_TITLE, TAG_STARTED_AT,
								TAG_PLACE }, new int[] { R.id.vers,
								R.id.name, R.id.api });
				
		          list = (ListView) findViewById(R.id.list);
		          Log.d("oslist", ""+oslist);
					list.setAdapter(adapter);
/*
				// Getting JSON Array from URL
				categories = json.getJSONArray(TAG_EVENTS);
				for (int i = 0; i < categories.length(); i++) {
					JSONObject a = categories.getJSONObject(i);
					String check = a.getString(TAG_EVENTS);
					Log.d("check", check);
					android = json.getJSONArray("");
					for (int i1 = 0; i1 < android.length(); i1++) {
						JSONObject c = android.getJSONObject(i1);

						// Storing JSON item in a Variable
						String ver = c.getString(TAG_TITLE);
						String name = c.getString(TAG_STARTED_AT);
						String api = c.getString(TAG_PLACE);

						// Adding value HashMap key => value

						HashMap<String, String> map = new HashMap<String, String>();

						map.put(TAG_TITLE, ver);
						map.put(TAG_STARTED_AT, name);
						map.put(TAG_PLACE, api);

						oslist.add(map);
						list = (ListView) findViewById(R.id.list);

						ListAdapter adapter = new SimpleAdapter(
								MainActivity.this, oslist, R.layout.list_v,
								new String[] { TAG_TITLE, TAG_STARTED_AT,
										TAG_PLACE }, new int[] { R.id.vers,
										R.id.name, R.id.api });

						list.setAdapter(adapter);*/
						list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								Toast.makeText(
										MainActivity.this,
										"ended_at "
												+ oslist.get(+position).get(
														"ended_at"),
										Toast.LENGTH_SHORT).show();

							}
						});
						list.addFooterView(getFooter());
						list.setOnScrollListener(new OnScrollListener() {
							@Override
							public void onScroll(
								AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
									boolean isLastItemVisible = totalItemCount == firstVisibleItem + visibleItemCount;
									if (isLastItemVisible && !isLoading) {
										Log.d("onScroll", ""+totalItemCount);
										readMore();
										
									}
								}

							@Override
							public void onScrollStateChanged(AbsListView view,
									int scrollState) {
								// TODO 自動生成されたメソッド・スタブ
								
							}
							

							
						});
						
						 
					//}
				//}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		private View getFooter() {
	        if (mFooter == null) {
	            mFooter = getLayoutInflater().inflate(R.layout.listview_footer,
	                    null);
	        }
	        return mFooter;
	    }
		
		// 「もっと読む」機能
		private AsyncTask<Void, String, Void> myTask;
		/* readMore()の状態 */
		private boolean isLoading = false;
		private int counter = 1;
		
		private ListAdapter adapter = new SimpleAdapter(
				MainActivity.this, oslist, R.layout.list_v,
				new String[] { TAG_TITLE, TAG_STARTED_AT,
						TAG_PLACE }, new int[] { R.id.vers,
						R.id.name, R.id.api });
		
		private void readMore() {
			// setFooter
	        footerText = (TextView) findViewById(R.id.foote_text);
	        footerProgressBar = (ProgressBar) findViewById(R.id.progressbar_small);
	       
			if (myTask != null && myTask.getStatus() == AsyncTask.Status.RUNNING) {
				return;
			}

			myTask = new AsyncTask<Void, String, Void>() {
				private boolean isLast;
				
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					footerProgressBar.setVisibility(View.VISIBLE);
					footerText.setText("読み込み中…");
				}

				@Override
				protected Void doInBackground(Void... params) {
					//RequestURIBuilder rub = new RequestURIBuilder(keyword, prefecture, period);
					//rub.setStartPosition(counter * 20 + 1);
					int co = (counter * 10 + 1);
					url = "http://api.atnd.org/eventatnd/event/?&ymd="+datef+"&count="+co+"&order=2&format=json";
					Log.d("counter "+counter, "co "+co);
					jParser.getJSONFromUrl(url);
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					Log.d("TEST", "onPostExecute()");
					super.onPostExecute(result);
					// doInBackgroundでエラーの場合.
					if (json == null) {
						Toast.makeText(MainActivity.this, "読み込めませんでした", Toast.LENGTH_SHORT).show();
					//	setFooterWaiting();
						return;
					}
					// 正常時 リストにEventオブジェクトを追加して更新
					try {
					//	JSONArray eventArray = new JSONObject(resultJSON).getJSONArray("events");
						JSONArray localJSONArray = json.getJSONArray("events");
						JSONObject localJSONObject2 = localJSONArray.getJSONObject(0);
						JSONArray eventArray = localJSONObject2.getJSONArray("event");
						// これ以上データがない場合
						if (eventArray.length() == 0) {
							Toast.makeText(MainActivity.this, "これ以上結果はありません", Toast.LENGTH_SHORT).show();
							invisibleFooter();
					        isLoading = true;
					        return;
						}
						addListData(eventArray);
						((SimpleAdapter) adapter).notifyDataSetChanged();
						getListView().invalidateViews();
						isLoading = false;
						counter++;
						//setFooterWaiting();
					} catch (Exception e) {
						e.getStackTrace();
					}
				}

				private void addListData(JSONArray array) {
		if(array == null) return;

		for(int i = 0; i < array.length(); i++) {
			JSONObject jsonObject = null;
			try {
				jsonObject = array.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			oslist.add(toEvent(jsonObject));
		}
	}

	private  HashMap<String, String> toEvent(JSONObject jsonObject) {
		 HashMap<String, String> localHashMap = new HashMap<String, String>();
		try {



	          localHashMap.put("event_id", jsonObject.getString("event_id"));
	          localHashMap.put("title", jsonObject.getString("title"));
	          localHashMap.put("catch", jsonObject.getString("catch"));
	          localHashMap.put("started_at", getFormatDate(jsonObject.getString("started_at")));
	          localHashMap.put("ended_at", getFormatDate(jsonObject.getString("ended_at")));
	          localHashMap.put("place", jsonObject.getString("place"));


	          Log.d("localHashMap", ""+localHashMap);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return localHashMap;
	}

				private void invisibleFooter() {
		getListView().removeFooterView(getFooter());
	}

				public ListView getListView() {
		if (list == null) {
			list = (ListView) findViewById(R.id.list);
		}
		return list;
	}

				private View getFooter() {
		if (mFooter == null) {
			mFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);
		}
		return mFooter;
	}

			}.execute();
		}
	
	
	}
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		Log.d("onScroll2", ""+totalItemCount);
		
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	
}
