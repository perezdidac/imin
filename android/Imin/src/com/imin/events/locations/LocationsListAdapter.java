package com.imin.events.locations;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imin.Imin;
import com.imin.R;

public class LocationsListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private LocationsListListener locationsListListener;
	private List<String> locations;

	public interface LocationsListListener {
		public void remove(int position);
	}

	public LocationsListAdapter(Context context, LocationsListListener locationsListListener, List<String> locations) {
		inflater = LayoutInflater.from(context);
		this.locationsListListener = locationsListListener;
		this.locations = locations;
	}

	@Override
	public int getCount() {
		return locations.size();
	}

	@Override
	public Object getItem(int position) {
		return locations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder viewHolder;

		final int pos = position;

		// Check if the view has been already loaded
		if (view == null) {
			view = inflater.inflate(R.layout.layout_location, null);

			viewHolder = new ViewHolder();
			viewHolder.textLocationName = (TextView) view.findViewById(R.id.textLocationName);
			viewHolder.imageRemoveLocation = (ImageView) view.findViewById(R.id.imageRemoveLocation);

			viewHolder.imageRemoveLocation.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					locationsListListener.remove(pos);
				}
			});

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// Get the location name for the current position
		String location = locations.get(position);

		viewHolder.textLocationName.setText(location);

		Imin.overrideFonts(view);
		return view;
	}

	private static class ViewHolder {
		public TextView textLocationName;
		public ImageView imageRemoveLocation;
	}

}
