package layout;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import jhu.voiceit.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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
    private boolean choseRecording = false;

    private MediaRecorder mediaRecorder;
    private String outputFile = null;

    public RecordFragment() {
        // Required empty public constructor
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
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        setRecordButtonListener();
        setSubmitButtonListener();

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);

        return view;
    }

    public void setPlayButtonListener() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer m = new MediaPlayer();

                try {
                    m.setDataSource(outputFile);
                } catch(Exception e ){
                    e.printStackTrace();
                }

                try {
                    m.prepare();
                } catch(Exception e) {
                    e.printStackTrace();
                }

                m.start();
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
                if(!choseRecording) {
                    //Hide list, show description box, change state
                    recordList.setVisibility(View.INVISIBLE);
                    recordDescription.setVisibility(View.VISIBLE);
                    choseRecording = true;
                } else {
                    //Publish the recording TODO
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
