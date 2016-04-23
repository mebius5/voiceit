package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import jhu.voiceit.R;
import jhu.voiceit.User;
import settings_dialogs.ChangeNameDialog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends BaseFragment {

    public final static String FRAGMENTNAME = "SettingsFragment";
    private final String fragmentName = FRAGMENTNAME;

    private static User owner;

    public SettingsFragment() {
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
    public static SettingsFragment newInstance(User user) {
        SettingsFragment fragment = new SettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //Retrieves buttons and sets onclicklisteners
        Button changeNameButton = (Button) view.findViewById(R.id.changeNameButton);
        Button changePictureButton = (Button) view.findViewById(R.id.changePictureButton);
        Button changePasswordButton = (Button) view.findViewById(R.id.changePasswordButton);
        Button deleteAccountButton = (Button) view.findViewById(R.id.deleteAccountButton);

        changeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show popup to edit log item
                ChangeNameDialog popUp = new ChangeNameDialog(getActivity(), SettingsFragment.this);
                popUp.show();
            }
        });

        changePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }
}