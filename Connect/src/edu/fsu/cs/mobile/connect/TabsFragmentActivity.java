package edu.fsu.cs.mobile.connect;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

public class TabsFragmentActivity extends FragmentActivity implements
		TabHost.OnTabChangeListener {

	private TabHost mTabHost;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabsFragmentActivity.TabInfo>();
	private TabInfo mLastTab = null;
	private static Fragment currentFrag = null;
	private final static String TAG = "result";
	static ArrayList<String> names, uids, pics;
	static Bundle forFragment;

	private class TabInfo {
		private String tag;
		private Class<?> tabClass;
		private Bundle args;
		private Fragment fragment;

		TabInfo(String tag, Class<?> clss, Bundle args) {
			this.tag = tag;
			this.tabClass = clss;
			this.args = args;
		}

	}

	class TabFactory implements TabContentFactory {

		private final Context mContext;

		/**
		 * @param context
		 */
		public TabFactory(Context context) {
			mContext = context;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
		 */
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}

	}

	private static void addTab(TabsFragmentActivity activity, TabHost tabHost,
			TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
		String tag = tabSpec.getTag();

		// Check to see if we already have a fragment for this tab, probably
		// from a previously saved state. If so, deactivate it, because our
		// initial state is that a tab isn't shown.
		tabInfo.fragment = activity.getSupportFragmentManager()
				.findFragmentByTag(tag);
		if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
			FragmentTransaction ft = activity.getSupportFragmentManager()
					.beginTransaction();

			ft.detach(tabInfo.fragment);
			currentFrag = tabInfo.fragment;
			ft.commit();
			activity.getSupportFragmentManager().executePendingTransactions();
		}

		tabHost.addTab(tabSpec);
	}

	private void initialiseTabHost(Bundle args) {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabInfo tabInfo = null;
		TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost
				.newTabSpec("Friends").setIndicator("Friends"),
				(tabInfo = new TabInfo("Friends", FriendsTabFragment.class,
						args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost
				.newTabSpec("Likes").setIndicator("Likes"),
				(tabInfo = new TabInfo("Likes", LikesTabFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		// Default to first tab
		this.onTabChanged("Friends");
		mTabHost.setOnTabChangedListener(this);
	}

	protected void onCreate(Bundle savedInstanceState) {

		uids = new ArrayList<String>();
		names = new ArrayList<String>();
		pics = new ArrayList<String>();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs_layout);
		initialiseTabHost(savedInstanceState);
		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));

		}

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();

		String friendsQuery = "SELECT name,uid,pic_square FROM user WHERE uid IN "
				+ "(SELECT uid2 FROM friend WHERE uid1=me() )";

		Bundle params = new Bundle();
		params.putString("q", friendsQuery);
		Session session = Session.getActiveSession();
		Request request = new Request(session, "/fql", params, HttpMethod.GET,
				new Request.Callback() {
					public void onCompleted(Response response) {
						Log.d(TAG, "Result: " + response.toString());
						
						FragmentTransaction ft = getSupportFragmentManager()
								.beginTransaction();
						
						ft.remove(currentFrag);
						FriendsTabFragment frag = new FriendsTabFragment();
						frag.setArguments(parseUserFromFQLResponse(response));
						ft.add(frag,"Friends");
						ft.commit();
						getSupportFragmentManager().executePendingTransactions();
					}
				});

		Request.executeBatchAsync(request);

		if (bundle != null) {

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	public void onTabChanged(String tag) {
		TabInfo newTab = this.mapTabInfo.get(tag);
		if (mLastTab != newTab) {
			FragmentTransaction ft = this.getSupportFragmentManager()
					.beginTransaction();
			if (mLastTab != null) {
				if (mLastTab.fragment != null) {
					ft.detach(mLastTab.fragment);
				}
			}
			if (newTab != null) {
				if (newTab.fragment == null) {
					newTab.fragment = Fragment.instantiate(this,
							newTab.tabClass.getName(), newTab.args);
					ft.add(R.id.realtabcontent, newTab.fragment, newTab.tag);
				} else {
					ft.attach(newTab.fragment);
				}
			}

			mLastTab = newTab;
			currentFrag = mLastTab.fragment;
			ft.commit();
			this.getSupportFragmentManager().executePendingTransactions();
		}
	}

	public final Bundle parseUserFromFQLResponse(Response response) {
		
		Bundle b = new Bundle();
		
		try {
			GraphObject go = response.getGraphObject();
			JSONObject jso = go.getInnerJSONObject();
			JSONArray arr = jso.getJSONArray("data");

			for (int i = 0; i < (arr.length()); i++) {
				JSONObject json_obj = arr.getJSONObject(i);

				String id = json_obj.getString("uid");
				uids.add(id);
				String name = json_obj.getString("name");
				names.add(name);
				String urlImg = json_obj.getString("pic_square");
				pics.add(urlImg);

				
				b.putStringArrayList("theNames", names);
				b.putStringArrayList("theIds", uids);
				b.putStringArrayList("thePics", pics);

				
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		return b;
	}
}
