package layout;

import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jhu.voiceit.Byte64EncodeAndDecoder;
import jhu.voiceit.ChangePhotoActivity;
import jhu.voiceit.Post;
import jhu.voiceit.R;
import jhu.voiceit.User;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.client.utilities.Utilities;
import com.firebase.ui.FirebaseRecyclerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
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
    private MediaPlayer mediaPlayer;
    private Firebase mRef;
    private OnListFragmentInteractionListener mListener;
    private boolean isPlaying = false;

    private static User owner;

    private PostViewHolder currentPlaying = null;

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


                //Inflates the another user's profile, but not your own
                if(!post1.getOwner().getUserId().equals(owner.getUserId())) {
                    postViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OtherUserProfileFragment otherUser = OtherUserProfileFragment.newInstance(post1.getOwner(), owner);
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame_main, otherUser);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    });

                    postViewHolder.username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OtherUserProfileFragment otherUser = OtherUserProfileFragment.newInstance(post1.getOwner(), owner);
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame_main, otherUser);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    });
                }

                postViewHolder.timeStamp.setText(post.calculateElapsedTime());

                postViewHolder.btnPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isPlaying) {
                            currentPlaying.btnPlay.setImageResource(R.drawable.ic_action_play);

                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.release();
                            isPlaying = false;
                            postViewHolder.btnPlay.setImageResource(R.drawable.ic_action_play);
                        } else {
                            currentPlaying = postViewHolder;
                            String defaultFilePath = Environment.getExternalStorageDirectory() + "/defaultRecording";

                            //Decodes the string and outputs in path of defaultFileName
                            Byte64EncodeAndDecoder.decode(defaultFilePath, post1.getAudioEncoded());

                            //Instantiate new mediaPlayer
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                            try {
                                mediaPlayer.setDataSource(defaultFilePath);
                                mediaPlayer.prepare();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        if(mp==mediaPlayer) {
                                            mediaPlayer.start();

                                            postViewHolder.btnPlay.setImageResource(R.drawable.ic_action_pause);
                                            isPlaying = true;

                                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                @Override
                                                public void onCompletion(MediaPlayer mp) {
                                                    if(mp==mediaPlayer) {
                                                        currentPlaying = null;
                                                        postViewHolder.btnPlay.setImageResource(R.drawable.ic_action_play);
                                                        isPlaying = false;
                                                        mp.stop();
                                                        mp.reset();
                                                        mp.release();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            } catch(Exception e ){
                                e.printStackTrace();
                            }
                        }
                    }
                });

                //Checks what the method did to update the image
                if(post1.inLikerSet(owner.getUserId())) {
                    postViewHolder.btnLikes.setImageResource(R.drawable.ic_action_favorite_selected);
                } else {
                    postViewHolder.btnLikes.setImageResource(R.drawable.ic_action_favorite);
                }

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

                //Decode profile string into file and turn into bitmap
                String encodedImageString = post1.getOwner().getProfilePicName();
                Byte64EncodeAndDecoder.decode(ChangePhotoActivity.DEFAULT_IMAGE_PATH,encodedImageString);
                Bitmap bitmap;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(ChangePhotoActivity.DEFAULT_IMAGE_PATH, bitmapOptions);
                postViewHolder.imageView.setImageBitmap(bitmap);
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
