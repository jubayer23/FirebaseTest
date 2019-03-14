package com.creative.firebasetest.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.creative.firebasetest.MainActivity;
import com.creative.firebasetest.R;
import com.creative.firebasetest.Utility.DeviceInfoUtils;
import com.creative.firebasetest.alertbanner.AlertDialogForAnything;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {


    private TextView tv_name, tv_email;
    private Button btn_logout;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        init(view);



        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

       displayInformation();

    }

    private void init(View view){

        tv_email = view.findViewById(R.id.tv_email);
        tv_name = view.findViewById(R.id.tv_name);
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);
    }

    private void displayInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();


            tv_email.setText(email);
            tv_name.setText(name);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.btn_logout){
            if (!DeviceInfoUtils.isConnectingToInternet(getActivity())) {
                AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "Alert", "Please connect to a working internet connection!", false);
                return;
            }

            FirebaseAuth.getInstance().signOut();

            ((MainActivity)getActivity()).processLogout();

        }
    }
}
