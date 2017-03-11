package com.franklyn.mobileswitch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.franklyn.mobileswitch.R;
import com.franklyn.mobileswitch.helper.pojo.SwitchContent;

import java.util.List;

/**
 * Created by AGBOMA franklyn on 1/31/17.
 */

public class SwitchAdapter extends ArrayAdapter<SwitchContent> {

    private List<SwitchContent> switchList;
    private Context context;
    private int lastPosition = -1;

    public SwitchAdapter(Context context, List<SwitchContent> switchList) {
        super(context, R.layout.switch_cotent_item, switchList);
        this.context = context;
        this.switchList = switchList;
    }

    private static class ViewHolder {
        TextView deviceName, switchState, timeStamp;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final SwitchContent items = switchList.get(position);
        final ViewHolder holder;

        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.switch_cotent_item, null, false);

            holder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
            holder.switchState = (TextView) convertView.findViewById(R.id.switch_state);
            holder.timeStamp = (TextView) convertView.findViewById(R.id.time_stamp);

            convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.deviceName.setText(items.getDeviceName());
        holder.switchState.setText(items.getState());
        holder.timeStamp.setText(items.getLastTimeSet());

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up : R.anim.down);
        convertView.setAnimation(animation);
        lastPosition = position;

        return convertView;
    }
}
