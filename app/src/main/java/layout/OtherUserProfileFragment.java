package layout;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;

import jhu.voiceit.Byte64EncodeAndDecoder;
import jhu.voiceit.Post;
import jhu.voiceit.R;
import jhu.voiceit.User;

/***
 * Exactly the same as ProfileFragment, except you can't see the trash icon.
 */
public class OtherUserProfileFragment extends BaseFragment {

    public final static String FRAGMENTNAME = "OtherUserProfileFragment";
    private final String fragmentName = FRAGMENTNAME;

    private static User owner;
    private MediaPlayer mediaPlayer;

    private boolean isPlaying = false;

    public OtherUserProfileFragment() {
        // Required empty public constructor
    }

    public String getFragmentName(){
        return this.fragmentName;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ProfileFragment.
     */
    public static OtherUserProfileFragment newInstance(User user) {
        OtherUserProfileFragment fragment = new OtherUserProfileFragment();
        owner = user;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        final TextView numPostText = (TextView) view.findViewById(R.id.post_num);

        Firebase mRef = new Firebase(getResources().getString(R.string.firebaseurl)).child("posts");
        Query user = mRef.orderByChild("owner/userId").equalTo(owner.getUserId());

        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numPosts = dataSnapshot.getChildrenCount();
                Log.i("OtherUserProfileFrag", "onDataChange: numPosts "+numPosts);
                numPostText.setText("" + numPosts);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_profile);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(
                new FirebaseRecyclerAdapter<Post, PostViewHolder>(Post.class, R.layout.post_layout, PostViewHolder.class, user) {
                    @Override
                    protected void populateViewHolder(final PostViewHolder postViewHolder, Post post, int i) {
                        final Post post1 = post;
                        final Firebase postRef = getRef(i);
                        postViewHolder.username.setText(post.getOwner().getUsername());
                        postViewHolder.description.setText(post.getDescription());
                        postViewHolder.numLikes.setText(""+post.getLikes());
                        //TODO: set postViewHolder.imageView to retrieve image;
                        postViewHolder.timeStamp.setText(post.calculateElapsedTime());

                        postViewHolder.btnPlay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(isPlaying) {
                                    mediaPlayer.stop();
                                    mediaPlayer.release();
                                    isPlaying = false;
                                    postViewHolder.btnPlay.setImageResource(R.drawable.ic_action_play);

                                } else {
                                    String defaultFilePath = Environment.getExternalStorageDirectory() + "/defaultRecording";

                                    //Decodes the string and outputs in path of defaultFileName
                                    Byte64EncodeAndDecoder.decode(defaultFilePath, post1.getAudioEncoded());

                                    //Instantiate new mediaPlayer
                                    mediaPlayer = new MediaPlayer();

                                    try {
                                        mediaPlayer.setDataSource(defaultFilePath);
                                        //mediaPlayer.setDataSource(post1.getAudioEncoded());
                                    } catch(Exception e ){
                                        e.printStackTrace();
                                    }

                                    try {
                                        mediaPlayer.prepare();
                                    } catch(Exception e) {
                                        e.printStackTrace();
                                    }

                                    mediaPlayer.start();

                                    postViewHolder.btnPlay.setImageResource(R.drawable.ic_action_pause);
                                    isPlaying = true;

                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            postViewHolder.btnPlay.setImageResource(R.drawable.ic_action_play);
                                            isPlaying = false;
                                            mp.stop();
                                            mp.release();
                                        }
                                    });
                                }
                            }
                        });

                        postViewHolder.btnLikes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                post1.likePost(owner.getUserId());
                                postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Object priority = dataSnapshot.getPriority();
                                        postRef.setValue(post1);
                                        postRef.setPriority(priority);
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });

                            }
                        });

                        postViewHolder.numLikes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                post1.likePost(owner.getUserId());
                                postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Object priority = dataSnapshot.getPriority();
                                        postRef.setValue(post1);
                                        postRef.setPriority(priority);
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        });

                        postViewHolder.postsetting.setVisibility(View.GONE);

                    }
                });

        final TextView topUsername = (TextView) view.findViewById(R.id.username_view);
        topUsername.setText(owner.getUsername());
        return view;
    }
}