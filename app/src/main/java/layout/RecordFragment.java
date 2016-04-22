package layout;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jhu.voiceit.Post;
import jhu.voiceit.R;
import jhu.voiceit.User;
import jhu.voiceit.layout.PostAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends BaseFragment {
    public final static String FRAGMENTNAME = "RecordFragment";
    private final String fragmentName = FRAGMENTNAME;

    /*
    ####################### Instance Variables #####################
     */
    private ImageView recordButton;
    private ImageView playButton;
    private TextView recordTime;
    private EditText recordDescription;
    private ListView recordList;
    private ImageView submitButton;
    private CountDownTimer countDownTimer;

    private boolean isRecording = false;
    private boolean isPlaying = false;
    private boolean isAddingDescription = false;

    private MediaRecorder mediaRecorder;
    private String outputFile = null;
    private MediaPlayer mediaPlayer;

    private ArrayList<Post> recordings;
    private PostAdapter postAdapter;

    private Post selected;

    private User user;

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
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();
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

        recordButton = (ImageView) view.findViewById(R.id.imageViewRecordStop);
        playButton = (ImageView) view.findViewById(R.id.imageViewPlay);
        recordTime = (TextView) view.findViewById(R.id.textViewRecordLength);
        recordDescription = (EditText) view.findViewById(R.id.editTextRecordDescription);
        recordList = (ListView) view.findViewById(R.id.listViewRecording);
        submitButton = (ImageView) view.findViewById(R.id.imageViewSubmit);

        recordings = new ArrayList<Post>();
        postAdapter = new PostAdapter(getActivity(), recordings);
        recordList.setAdapter(postAdapter);
        selected = null;

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

        Post newPost = new Post(user, outputFile, null);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);

        recordings.add(newPost);
        postAdapter.notifyDataSetChanged();
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
                            mediaPlayer.setDataSource(selected.getFilename());
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
                    }
                } else if(isRecording){
                    Toast.makeText(getActivity(), R.string.play_while_recording_feedback, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.submit_error_feedback, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
