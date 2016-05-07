package layout;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import jhu.voiceit.R;
import jhu.voiceit.User;

/**
 * Created by Leo on 5/7/16.
 */
public class SearchAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<User> users;

    public SearchAdapter(Activity activity, ArrayList<User> users) {
        this.activity = activity;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.search_user_list, null);

        if(convertView != null) {
            TextView username = (TextView) convertView.findViewById(R.id.textViewSearchUsername);
            username.setText(users.get(position).getUsername());
        }

        return convertView;
    }
}
