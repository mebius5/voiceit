package jhu.voiceit.layout;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import jhu.voiceit.Post;
import jhu.voiceit.R;

/**
 * Created by Leo on 4/22/16.
 */
public class PostAdapter extends BaseAdapter{
    private Activity activity;
    private ArrayList<Post> posts;

    public PostAdapter(Activity activity, ArrayList<Post> posts) {
        this.activity = activity;
        this.posts = posts;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.post_list_layout, null);

        if(convertView != null) {
            TextView recordingText = (TextView) convertView.findViewById(R.id.postListLayoutName);

            int displayPosition = position + 1;

            recordingText.setText("Recording " + displayPosition);
        }

        return convertView;
    }
}
