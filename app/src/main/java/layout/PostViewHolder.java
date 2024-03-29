package layout;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jhu.voiceit.Post;
import jhu.voiceit.R;

/**
 * Created by GradyXiao on 4/22/16.
 */
public class PostViewHolder extends RecyclerView.ViewHolder{
    TextView username;
    TextView description;
    ImageView imageView;
    ImageView btnLikes;
    ImageView btnPlay;
    TextView numLikes;
    TextView timeStamp;
    ImageView postsetting;

    public PostViewHolder(View itemView){
        super(itemView);
        username = (TextView) itemView.findViewById(R.id.textViewPostUsername);
        description = (TextView) itemView.findViewById(R.id.textViewPostDescription);
        imageView = (ImageView) itemView.findViewById(R.id.imageViewPostPicture);
        btnPlay = (ImageView) itemView.findViewById(R.id.imageViewPostPlay);
        btnLikes = (ImageView) itemView.findViewById(R.id.imageViewPostLike);
        numLikes = (TextView) itemView.findViewById(R.id.textViewPostNumberLikes);
        timeStamp = (TextView) itemView.findViewById(R.id.textViewTimestamp);
        postsetting = (ImageView) itemView.findViewById(R.id.imageViewPostSettings);
    }
}
