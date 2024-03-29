package layout;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Calendar;

import jhu.voiceit.Byte64EncodeAndDecoder;
import jhu.voiceit.MainActivity;
import jhu.voiceit.Post;
import jhu.voiceit.R;
import jhu.voiceit.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends BaseFragment {
    private final String CURRENTFRAGMENT = "currentFragment";
    public final static String FRAGMENTNAME = "RecordFragment";
    private final String fragmentName = FRAGMENTNAME;

    private Firebase mRef;
    private static User owner;


    /*
    ####################### Instance Variables #####################
     */
    private ImageView recordButton;
    private ImageView playButton;
    private TextView recordTime;
    private EditText recordDescription;
    private ListView recordList;
    private RelativeLayout submitButton;
    private CountDownTimer countDownTimer;

    private boolean isRecording = false;
    private boolean isPlaying = false;
    private boolean isAddingDescription = false;

    private MediaRecorder mediaRecorder;
    private String outputFile = null;
    private MediaPlayer mediaPlayer;

    private ArrayList<Post> recordings;
    private RecordingsAdapter recordingsAdapter;

    private Post selected;

    private Toast toast;

    private BaseFragment baseFragment;
    private SharedPreferences myPrefs;
    private SharedPreferences.Editor peditor;

    public RecordFragment() {
        // Required empty public constructor
    }

    public String getFragmentName(){
        return this.fragmentName;
    }



    public void controlTimer() {
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished > 10000) {
                    recordTime.setText("00:" + String.valueOf(millisUntilFinished / 1000));
                } else {
                    recordTime.setText("00:0" + String.valueOf(millisUntilFinished / 1000));
                }

            }

            @Override
            public void onFinish() {
                cleanUpMediaRecorderOnStop();
            }
        }.start();
    }

    public void cleanUpMediaRecorderOnStop() {

        mediaRecorder.stop();
        mediaRecorder.release();

        recordButton.setImageResource(R.drawable.record_button);
        recordTime.setText("00:30");
        isRecording = false;
        toast.makeText(getActivity(), R.string.add_record_feedback, Toast.LENGTH_SHORT).show();
    }

    public void cleanUpMediaPlayerOnStop() {

        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        isPlaying = false;
        playButton.setImageResource(R.drawable.ic_action_play);
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RecordFragment.
     */
    public static RecordFragment newInstance(User user) {
        RecordFragment fragment = new RecordFragment();
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
        View view =  inflater.inflate(R.layout.fragment_record, container, false);

        mRef = new Firebase(getResources().getString(R.string.firebaseurl));

        recordButton = (ImageView) view.findViewById(R.id.imageViewRecordButton);
        playButton = (ImageView) view.findViewById(R.id.imageViewPlayButton);
        recordTime = (TextView) view.findViewById(R.id.textViewRecordLength);
        recordDescription = (EditText) view.findViewById(R.id.editTextRecordDescription);
        recordList = (ListView) view.findViewById(R.id.listViewRecording);
        submitButton = (RelativeLayout) view.findViewById(R.id.submitBar);

        recordDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    submitRecording();
                    hideKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });

        recordings = new ArrayList<Post>();
        recordingsAdapter = new RecordingsAdapter(getActivity(), recordings);
        recordList.setAdapter(recordingsAdapter);
        selected = null;

        myPrefs= PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        peditor = myPrefs.edit();

        setRecordButtonListener();
        setSubmitButtonListener();
        setPlayButtonListener();
        setListListener();

        return view;
    }

    public void setListListener() {
        recordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeBackgroundColors(position);
                selected = recordings.get(position);
                outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording"+ position+ ".3gp";
            }
        });
    }

    public void changeBackgroundColors(int position) {
        for(int i = 0; i < recordList.getChildCount(); i++) {
            if(position == i) {
                recordList.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.lightRed));
                selected = recordings.get(position);
            } else {
                recordList.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    public void insertNewRecording() {
        String fileNumber = "" + recordings.size();

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording"+ fileNumber+ ".3gp";

        Post newPost = new Post(owner);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);

        recordings.add(newPost);
        recordingsAdapter.notifyDataSetChanged();
    }

    public void setPlayButtonListener() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Checks if the user selected a file from the list and is not recording
                if(selected != null && !isRecording) {
                    //Checks if it's already playing to configure PLAY or PAUSE
                    if(isPlaying) {
                        cleanUpMediaPlayerOnStop();
                    } else {
                        try {
                            //Instantiate new mediaPlayer
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    if(mp==mediaPlayer) {
                                        mediaPlayer.start();
                                        //Change image and reverts the Boolean
                                        playButton.setImageResource(R.drawable.ic_action_pause);
                                        isPlaying = true;
                                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                cleanUpMediaPlayerOnStop();
                                            }
                                        });
                                    }
                                }
                            });
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(outputFile);
                            mediaPlayer.prepare();

                        } catch(Exception e ){
                            e.printStackTrace();
                        }
                    }

                    //Gives appropriate feedback to the user
                } else if(isRecording) {
                    toast.makeText(getActivity(), R.string.play_while_recording_feedback, Toast.LENGTH_SHORT).show();

                    //Gives appropriate feedback to the user
                } else {
                    toast.makeText(getActivity(), R.string.play_record_feedback, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setRecordButtonListener() {
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRecording && !isAddingDescription) {
                    try {
                        insertNewRecording();
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        //Change image, change state, start recording
                        recordButton.setImageResource(R.drawable.stop_button);
                        isRecording = true;
                        controlTimer();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                } else if(isAddingDescription) {
                    toast.makeText(getActivity(), "Impossible to record right now", Toast.LENGTH_SHORT).show();
                } else {
                    //Change image, change state, store recording on list
                    countDownTimer.cancel();
                    cleanUpMediaRecorderOnStop();
                }

            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void setSubmitButtonListener() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected != null && !isRecording) {
                    if(!isAddingDescription) {
                        //Hide list, show description box, change state
                        recordList.setVisibility(View.INVISIBLE);
                        recordDescription.setVisibility(View.VISIBLE);
                        isAddingDescription = true;
                    } else {
                        submitRecording();
                    }
                } else if(isRecording){
                    toast.makeText(getActivity(), R.string.play_while_recording_feedback, Toast.LENGTH_SHORT).show();
                } else {
                    toast.makeText(getActivity(), R.string.submit_error_feedback, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void submitRecording() {
        Log.i("RecordFragment", "Encoding file "+outputFile);
        //Encodes audio recording as string and set it
        selected.setAudioEncoded(Byte64EncodeAndDecoder.encode(outputFile));

        //Store the description in the post, submit to firebase
        selected.setDescription(recordDescription.getText().toString());
        toast.makeText(getActivity(), "Your recording has been posted!", Toast.LENGTH_SHORT).show();

        //Push onto firebase
        Firebase post = mRef.child("posts").push();
        post.setValue(selected);
        post.setPriority(0 - Calendar.getInstance().getTimeInMillis());

        MainActivity.setTabLayout(0);

        baseFragment = HomeFeedFragment.newInstance(owner);
        inflateAndCommitBaseFragment();
    }

    private void inflateAndCommitBaseFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_main, baseFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        peditor.putString(CURRENTFRAGMENT, baseFragment.getFragmentName());
        peditor.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("Record activity", "on pause");
    }
}
