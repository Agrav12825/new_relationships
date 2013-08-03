package edu.fsu.cs.mobile.connect;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

public class FriendPicker extends Activity {

	private Button toLikes;
	private ListView friendPicklist;
	private ArrayList<String> names, ids, pics;
	private FriendListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		names = new ArrayList<String>();
		ids = new ArrayList<String>();
		pics = new ArrayList<String>();
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends_picker);
		friendPicklist = (ListView)findViewById(R.id.friend_picklist);
		toLikes = (Button)findViewById(R.id.toLikes);
		
		String friendsQuery = "SELECT name,uid,pic_square FROM user WHERE uid IN "
				+ "(SELECT uid2 FROM friend WHERE uid1=me() )";
		
		//String myLikesQuery = "SELECT page_id from page_fan where uid = me()";

		Bundle params = new Bundle();
		//params.putString("q2", myLikesQuery);
		params.putString("q", friendsQuery);
		
		Session session = Session.getActiveSession();
		Request request = new Request(session, "/fql", params, HttpMethod.GET,
				new Request.Callback() {
					public void onCompleted(Response response) {
						Log.d("k", "Result: " + response.toString());
						
						parseUserFromFQLResponse(response);
						
						String[] namesArray = new String[names.size()];
					 	namesArray = names.toArray(namesArray);
					 	
					 	String[] idsArray = new String[ids.size()];
					 	idsArray = ids.toArray(idsArray);
					 	
					 	String[] picsArray = new String[pics.size()];
					 	picsArray = pics.toArray(picsArray);
					 	
					 	
					 	adapter = new FriendListAdapter(getApplicationContext(), namesArray, picsArray, idsArray, 0);
					 	friendPicklist.setAdapter(adapter);
					 	
					}
				});

		Request.executeBatchAsync(request);
	}

	public final void parseUserFromFQLResponse(Response response) {

		Bundle b = new Bundle();

		try {
			GraphObject go = response.getGraphObject();
			JSONObject jso = go.getInnerJSONObject();
			JSONArray arr = jso.getJSONArray("data");

			for (int i = 0; i < (arr.length()); i++) {
				JSONObject json_obj = arr.getJSONObject(i);
				String id = json_obj.getString("uid");
				ids.add(id);
				String name = json_obj.getString("name");
				names.add(name);
				String pic = json_obj.getString("pic_square");
				pics.add(pic);
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	public void switchActivities(View v) {
		Intent intent = new Intent(this, PagePicker.class);
		startActivity(intent);
	}
	
}
