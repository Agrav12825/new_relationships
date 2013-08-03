package edu.fsu.cs.mobile.connect;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.BaseAdapter;

public abstract class FriendsListItem {
	private Drawable profile_pic;
	private String name;
	private String likes;
	private int requestCode;
	private BaseAdapter adapter;

	
	public FriendsListItem(Drawable profile_pic, String name, String likes,
			int requestCode) {
		super();
		this.profile_pic = profile_pic;
		this.name = name;
		this.likes = likes;
		this.requestCode = requestCode;
	}
	
	protected abstract View.OnClickListener getOnClickListener();

	public Drawable getProfile_pic() {
		return profile_pic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLikes() {
		return likes;
	}

	public void setLikes(String likes) {
		this.likes = likes;
	}

	public int getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}

	public BaseAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
	}
	
}
