package layout;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Calendar;

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
                cleanUpOnStop();
            }
        }.start();
    }

    public void cleanUpOnStop() {

        mediaRecorder.stop();
        mediaRecorder.release();

        recordButton.setImageResource(R.drawable.record_button);
        recordTime.setText("00:30");
        isRecording = false;
        Toast.makeText(getActivity(), R.string.add_record_feedback, Toast.LENGTH_SHORT).show();
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

        mRef = new Firebase(getResources().getString(R.string.firebaseurl)).child("posts");

        recordButton = (ImageView) view.findViewById(R.id.imageViewRecordButton);
        playButton = (ImageView) view.findViewById(R.id.imageViewPlayButton);
        recordTime = (TextView) view.findViewById(R.id.textViewRecordLength);
        recordDescription = (EditText) view.findViewById(R.id.editTextRecordDescription);
        recordList = (ListView) view.findViewById(R.id.listViewRecording);
        submitButton = (RelativeLayout) view.findViewById(R.id.submitBar);

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

        /*mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);*/

        return view;
    }

    public void setListListener() {
        recordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeBackgroundColors(position);
                selected = recordings.get(position);
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

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording " + fileNumber + ".3gp";

        Post newPost = new Post(owner, outputFile, "abc");

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
                        //Changes image, stops the sound, reverts the Boolean
                        playButton.setImageResource(R.drawable.ic_action_play);
                        mediaPlayer.stop();
                        isPlaying = false;
                    } else {
                        //Instantiate new mediaPlayer
                        mediaPlayer = new MediaPlayer();

                        try {
                            mediaPlayer.setDataSource(selected.getAudioFilename());
                        } catch(Exception e ){
                            e.printStackTrace();
                        }

                        try {
                            mediaPlayer.prepare();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                        //Play, change image and reverts the Boolean
                        mediaPlayer.start();
                        playButton.setImageResource(R.drawable.ic_action_pause);
                        isPlaying = true;
                    }

                    //Gives appropriate feedback to the user
                } else if(isRecording) {
                    Toast.makeText(getActivity(), R.string.play_while_recording_feedback, Toast.LENGTH_SHORT).show();

                    //Gives appropriate feedback to the user
                } else {
                    Toast.makeText(getActivity(), R.string.play_record_feedback, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setRecordButtonListener() {
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRecording) {
                    //Change image, change state, start recording
                    recordButton.setImageResource(R.drawable.stop_button);
                    isRecording = true;
                    controlTimer();

                    try {
                        insertNewRecording();
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    //Change image, change state, store recording on list
                    countDownTimer.cancel();
                    cleanUpOnStop();
                }
            }
        });
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
                        //Store the description in the post, submit to firebase
                        selected.setDescription(recordDescription.getText().toString());
                        Toast.makeText(getActivity(), selected.getDescription(), Toast.LENGTH_SHORT).show();

                        //Push onto firebase
                        Firebase post = mRef.push();
                        post.setValue(selected);
                        post.setPriority(0- Calendar.getInstance().getTimeInMillis());

                        baseFragment = HomeFeedFragment.newInstance(owner);
                        inflateAndCommitBaseFragment();
                    }
                } else if(isRecording){
                    Toast.makeText(getActivity(), R.string.play_while_recording_feedback, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.submit_error_feedback, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
