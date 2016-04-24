package layout;

import android.content.Context;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import jhu.voiceit.R;
import jhu.voiceit.User;
import settings_dialogs.ChangeNameDialog;
import settings_dialogs.ChangePasswordDialog;
import settings_dialogs.ChangePhotoDialog;
import settings_dialogs.DeleteAccountDialog;

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
        TextView currentName = (TextView) view.findViewById(R.id.changeText);

        currentName.setText(owner.getUsername());

        changeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show popup to edit log item
                ChangeNameDialog popUp = new ChangeNameDialog(getActivity(), SettingsFragment.this, owner);
                popUp.show();
            }
        });

        changePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePhotoDialog popUp = new ChangePhotoDialog(getActivity(), SettingsFragment.this, owner.getUserId());
                popUp.show();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordDialog popUp = new ChangePasswordDialog(getActivity(), SettingsFragment.this, owner.getUserId());
                popUp.show();
            }
        });


        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteAccountDialog popUp = new DeleteAccountDialog(getActivity(), SettingsFragment.this, owner.getUserId());
                popUp.show();
            }
        });

        return view;
    }

    public void update() {
        //TODO: the textview to show new username
    }

    public void makeToast(CharSequence text) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getActivity(), text, duration);
        toast.show();
    }
}