package layout;

import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;

import jhu.voiceit.Byte64EncodeAndDecoder;
import jhu.voiceit.ChangePhotoActivity;
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
    private MediaPlayer mediaPlayer;

    private boolean isPlaying = false;

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

        final TextView topUsername = (TextView) view.findViewById(R.id.username_view);
        topUsername.setText(owner.getUsername());

        final TextView numPostText = (TextView) view.findViewById(R.id.post_num);
        final ImageView profileImageView = (ImageView) view.findViewById(R.id.profileImageView);

        Firebase mRef = new Firebase(getResources().getString(R.string.firebaseurl));
        Firebase postRef = mRef.child("posts");
        final Query postOfUser = postRef.orderByChild("owner/userId").equalTo(owner.getUserId());

        postOfUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numPosts = dataSnapshot.getChildrenCount();
                //Log.i("ProfileFragment", "onDataChange: numPosts "+numPosts);
                numPostText.setText("" + numPosts);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

        Firebase userRef = mRef.child("users").child(owner.getUserId());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = (String) dataSnapshot.child("username").getValue();
                owner.setUsername(username);
                topUsername.setText(username);

                String encodedImageString = (String) dataSnapshot.child("profilePicName").getValue();
                owner.setProfilePicName(encodedImageString);
                Byte64EncodeAndDecoder.decode(ChangePhotoActivity.DEFAULT_IMAGE_PATH,encodedImageString);

                Bitmap bitmap;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(ChangePhotoActivity.DEFAULT_IMAGE_PATH, bitmapOptions);
                profileImageView.setImageBitmap(bitmap);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_profile);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(
                new FirebaseRecyclerAdapter<Post, PostViewHolder>(Post.class, R.layout.post_layout, PostViewHolder.class, postOfUser) {
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
                                    mediaPlayer.reset();
                                    mediaPlayer.release();
                                    isPlaying = false;
                                    postViewHolder.btnPlay.setImageResource(R.drawable.ic_action_play);
                                } else {
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
                                    PostSettingDialogue popup = new PostSettingDialogue(getActivity(), ProfileFragment.this, owner, postRef);
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
}
