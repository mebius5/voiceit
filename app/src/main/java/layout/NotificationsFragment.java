package layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jhu.voiceit.R;
import jhu.voiceit.User;


public class NotificationsFragment extends BaseFragment {
    public final static String FRAGMENTNAME = "NotificationsFragment";
    private final String fragmentName = FRAGMENTNAME;

    private static User owner;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public String getFragmentName(){
        return this.fragmentName;
    }

    public static NotificationsFragment newInstance(User user) {
        NotificationsFragment fragment = new NotificationsFragment();
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
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        return view;
    }

}
