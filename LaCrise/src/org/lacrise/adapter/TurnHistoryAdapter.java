package org.lacrise.adapter;

import java.util.ArrayList;
import java.util.List;

import org.lacrise.engine.game.Turn;
import org.lacrise.view.TurnRowView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TurnHistoryAdapter extends BaseAdapter {

	private Context mContext;
	
	private List<Turn> mItems = new ArrayList<Turn>();
	
	public TurnHistoryAdapter(Context context) {
		mContext = context;
	}
	
	public void addItem(Turn turn) {
		mItems.add(turn);
	}
	
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TurnRowView view = new TurnRowView(mContext, mItems.get(position), position + 1);
		
		return view;
	}
	
}
