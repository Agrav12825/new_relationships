package edu.fsu.cs.mobile.connect;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FriendsTabFragment extends Fragment {

	private ListView listView;
	private FriendListAdapter adapter;

	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);
	    listView = (ListView)getActivity().findViewById(R.id.selection_list);
	    Bundle args = getArguments();
	   
	    if( args != null) {
	    	 ArrayList<String> list = args.getStringArrayList("theNames");
	 	    String[] array = new String[list.size()];
	 	    array = list.toArray(array);
	 	    //adapter = new CustomAdapter(getActivity(),array,3);
	 	    listView.setAdapter(adapter);
	 	    
	 	    
	    }
	    
	    View view = inflater.inflate(R.layout.friends, 
	            container, false);
	    
	  
	    
	    return view;
	}
	
	
	

	  
}
