package com.csmckelvey.statitician;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FeedbackListAdapter extends ArrayAdapter<StatObject> {

	private StatObject item;
	private Context myContext;
	private ViewHolder holder;
	private int splitPosition;
	private String displayText;
	private LayoutInflater inflater;
	private final String DELIMITER = "\t";
	private final String background1 = "#E5E4E2";
	private final String background2 = "#FFFFFF";
	
	public FeedbackListAdapter(Context context, int resourceId, List<StatObject> data) {
		super(context, resourceId, data);
		this.myContext = context;
		this.inflater = (LayoutInflater) myContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	}
 
    public View getView(int position, View convertView, ViewGroup parent) {
    	holder = null;
        item = getItem(position);
        displayText = item.getDisplayText();
        splitPosition = displayText.indexOf(DELIMITER);
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.feedback_listitem_layout, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.statIcon);
            holder.statText = (TextView) convertView.findViewById(R.id.statText);
            holder.statPosition = (TextView) convertView.findViewById(R.id.statPosition);
            holder.extraStatText = (TextView) convertView.findViewById(R.id.extraStatText);
            convertView.setTag(holder);
        } else { holder = (ViewHolder) convertView.getTag(); }
        
        if (splitPosition != -1) {
        	holder.statText.setText(displayText.substring(0, splitPosition));
            holder.extraStatText.setText(displayText.substring(splitPosition));
        }
        else { holder.statText.setText(displayText); }
        
        holder.imageView.setImageResource(item.getIconId());
        holder.statPosition.setText((position+1) + ": ");
        if ((position == 0) || (position%2 == 0)) { convertView.setBackgroundColor(Color.parseColor(background1)); }
        else { convertView.setBackgroundColor(Color.parseColor(background2)); }
        
        return convertView;
    }
		
    private class ViewHolder {
        ImageView imageView;
        TextView statText;
        TextView statPosition;
        TextView extraStatText;
    }
}

