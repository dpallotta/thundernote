package com.thundernoteapp.android.supportclasses;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.thundernoteapp.android.R;


public class NearbyAdapter extends BaseAdapter {
	private ArrayList<FsqVenue> mVenueList;
	private LayoutInflater mInflater;
	Context context;
	ImageLoader imageLoader;

	public NearbyAdapter(Context c , ImageLoader image) {
		mInflater 			= LayoutInflater.from(c);
		context = c;
		imageLoader = image;
	}

	public void setData(ArrayList<FsqVenue> poolList) {
		mVenueList = poolList;
	}

	public int getCount() {
		return mVenueList.size();
	}

	public Object getItem(int position) {
		return mVenueList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;

		if (convertView == null) {
			convertView	=  mInflater.inflate(R.layout.nearby_list, null);

			holder = new ViewHolder();

			holder.mNameTxt = (TextView) convertView.findViewById(R.id.tv_name);
			holder.mAddressTxt = (TextView) convertView.findViewById(R.id.tv_address);
			holder.mCheckinsTxt = (TextView) convertView.findViewById(R.id.tv_checkins);
			holder.mDistanceTxt = (TextView) convertView.findViewById(R.id.tv_distance);
			holder.icona = (ImageView) convertView.findViewById(R.id.icona);
			holder.icona.setScaleType(ImageView.ScaleType.CENTER_CROP);
			holder.icona.setMaxHeight(40);
			holder.icona.setMaxWidth(40);				

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		FsqVenue venue 	= mVenueList.get(position);

		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.stub_image)
		.cacheInMemory()
		.cacheOnDisc()
		.build();
		imageLoader.displayImage(venue.url, holder.icona, options);

		holder.mNameTxt.setText(venue.name);
		holder.mAddressTxt.setText(venue.address);
		holder.mCheckinsTxt.setText("(" + String.valueOf(venue.checkins) + " tot checkins)");
		holder.mDistanceTxt.setText(formatDistance(venue.distance));

		return convertView;
	}

	private String formatDistance(double distance) {
		String result = "";

		DecimalFormat dF = new DecimalFormat("00");

		dF.applyPattern("0.#");

		if (distance < 1000)
			result = dF.format(distance) + " m";
		else {
			distance = distance / 1000.0;
			result   = dF.format(distance) + " km";
		}

		return result;
	}
	class ViewHolder {
		TextView mNameTxt;
		TextView mAddressTxt;
		TextView mCheckinsTxt;
		TextView mDistanceTxt;
		ImageView icona;
	}
}

