package layout;

import android.content.Context;
import android.content.res.ObbInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;

import jhu.voiceit.Post;
import jhu.voiceit.R;
import jhu.voiceit.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends BaseFragment {

    public final static String FRAGMENTNAME = "ProfileFragment";
    private final String fragmentName = FRAGMENTNAME;

    private static User owner;

    public ProfileFragment() {
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
    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
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

        TextView numPostText = (TextView) view.findViewById(R.id.post_num);
        numPostText.setText("" + owner.getNumPosts());

        Firebase mRef = new Firebase(getResources().getString(R.string.firebaseurl)).child("posts");
        Query user = mRef.orderByChild("owner/userId").equalTo(owner.getUserId());

        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getChildrenCount();
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

                        //TODO: Play network instead of local recordings
                        postViewHolder.btnPlay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Instantiate new mediaPlayer
                                MediaPlayer mediaPlayer = new MediaPlayer();

                                try {
                                    mediaPlayer.setDataSource(post1.getAudioFilename());
                                } catch(Exception e ){
                                    e.printStackTrace();
                                }

                                try {
                                    mediaPlayer.prepare();
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }

                                mediaPlayer.start();

                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        mp.stop();
                                        mp.release();
                                    }
                                });
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

                        if (!owner.getUserId().equals(post1.getOwner().getUserId())) {
                            postViewHolder.postsetting.setVisibility(View.GONE);
                        } else {
                            postViewHolder.postsetting.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PostSettingDialogue popup = new PostSettingDialogue(getActivity(), ProfileFragment.this, owner, postRef);
                                    popup.show();
                                }
                            });
                        }
                    }
                });

        final TextView topUsername = (TextView) view.findViewById(R.id.username_view);
        topUsername.setText(owner.getUsername());
        return view;
    }
}
