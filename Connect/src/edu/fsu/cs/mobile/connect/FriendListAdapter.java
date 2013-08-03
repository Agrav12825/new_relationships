package edu.fsu.cs.mobile.connect;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class FriendListAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] names;
	private final String[] picUrls;
	private final int count;
	private final String[] ids;
	String URL = null;

	public FriendListAdapter(Context context, String[] names, String[] picUrls, String[] ids,
			int count) {

		super(context, R.layout.list_item, R.id.name, names);
		this.context = context;
		this.names = names;
		this.picUrls = picUrls;
		this.count = count;
		this.ids = ids;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View listItem = super.getView(position, convertView, parent);
		ImageView profilePic = (ImageView) listItem
				.findViewById(R.id.profilepic);
		URL = picUrls[position];
		profilePic.setTag(URL);
		new DownloadsImagesTask().execute(profilePic);

		return listItem;

	}

	public class DownloadsImagesTask extends AsyncTask<ImageView, Void, Bitmap> {

		ImageView imageView = null;

		@Override
		protected Bitmap doInBackground(ImageView... imageViews) {
			this.imageView = imageViews[0];
			return download_Image((String) imageView.getTag());
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			imageView.setImageBitmap(result);

		}

		private Bitmap download_Image(String url) {
			Bitmap bmp = null;
			try {
				URL ulrn = new URL(url);
				HttpURLConnection con = (HttpURLConnection) ulrn
						.openConnection();
				InputStream is = con.getInputStream();
				bmp = BitmapFactory.decodeStream(is);
				if (null != bmp)
					return bmp;

			} catch (Exception e) {
			}
			return bmp;
		}
	}
}
