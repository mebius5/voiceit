package layout;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jhu.voiceit.Post;
import jhu.voiceit.R;
import jhu.voiceit.User;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class HomeFeedFragment extends BaseFragment {

    public final static String FRAGMENTNAME = "HomeFeedFragment";
    private final String fragmentName = FRAGMENTNAME;

    private Firebase mRef;
    private OnListFragmentInteractionListener mListener;

    private static User owner;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeFeedFragment() {
    }

    public String getFragmentName(){
        return this.fragmentName;
    }


    public static HomeFeedFragment newInstance(User user) {
        HomeFeedFragment fragment = new HomeFeedFragment();
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
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);

        mRef = new Firebase(getResources().getString(R.string.firebaseurl)).child("posts");
        Query user = mRef.orderByPriority();

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(
                new FirebaseRecyclerAdapter<Post, PostViewHolder>(Post.class, R.layout.post_layout, PostViewHolder.class, user) {
            @Override
            protected void populateViewHolder(final PostViewHolder postViewHolder, Post post, int i) {
                final Post post1 = post;
                final Firebase postRef = getRef(i);
                postViewHolder.username.setText(post.getOwner().getUsername());
                postViewHolder.description.setText(post.getDescription());
                postViewHolder.numLikes.setText("" + post.getLikes());

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
                            PostSettingDialogue popup = new PostSettingDialogue(getActivity(), HomeFeedFragment.this, owner, postRef);
                            popup.show();
                        }
                    });
                }
            }
        });
        return view;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {

        //TODO: Add onclick lister to each post

        void onListFragmentInteraction(Post post);
    }
}
