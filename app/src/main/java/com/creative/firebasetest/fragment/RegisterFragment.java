package com.creative.firebasetest.fragment;


import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.creative.firebasetest.MainActivity;
import com.creative.firebasetest.R;
import com.creative.firebasetest.Utility.CommonMethods;
import com.creative.firebasetest.Utility.DeviceInfoUtils;
import com.creative.firebasetest.Utility.RunnTimePermissions;
import com.creative.firebasetest.alertbanner.AlertDialogForAnything;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private Button btn_submit;
    private EditText ed_email, ed_password, ed_name;
    private ImageView imgBtn_back;
    private FirebaseAuth firebaseAuth;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        init(view);

        //Get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        return view;
    }

    private void init(View view) {
        ed_name = view.findViewById(R.id.ed_name);
        ed_email = view.findViewById(R.id.ed_email);
        ed_password = view.findViewById(R.id.ed_password);
        btn_submit = view.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        imgBtn_back = view.findViewById(R.id.imgBtn_back);
        imgBtn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.imgBtn_back) {
            ((MainActivity) getActivity()).addLoginFragment();
        }

        if (id == R.id.btn_submit) {


            if (!RunnTimePermissions.requestForAllRuntimePermissions(getActivity())) {
                return;
            }

            if (!DeviceInfoUtils.isConnectingToInternet(getActivity())) {
                AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "Alert", "Please connect to a working internet connection!", false);
                return;
            }

            if (!DeviceInfoUtils.isGooglePlayServicesAvailable(getActivity())) {
                AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "Alert", "This app need google play service to work properly. Please install it!!", false);
                return;
            }


            if (isValidCredentialsProvided()) {


                CommonMethods.hideKeyboardForcely(getActivity(), ed_name);
                CommonMethods.hideKeyboardForcely(getActivity(), ed_email);
                CommonMethods.hideKeyboardForcely(getActivity(), ed_password);

                sendRequestForRegister(ed_email.getText().toString(), ed_password.getText().toString(),ed_name.getText().toString());

            }

        }

    }


    private void sendRequestForRegister(String email, String password, final String name) {
        showProgressDialog("Loading...", true, false);
        //create user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Toast.makeText(getActivity(), "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            dismissProgressDialog();

                            Toast.makeText(getActivity(), "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                         //  Log.d("DEBUG",task.getException() + "");
                        } else {

                            updateProfile(name);
                        }
                    }
                });

    }

    private void updateProfile(String name){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        showProgressDialog("Loading...", true, false);

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        dismissProgressDialog();

                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Signup Successful.", Toast.LENGTH_SHORT).show();

                            ((MainActivity)getActivity()).processLogin();
                        }else{
                            //dismissProgressDialog();
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        dismissProgressDialog();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private boolean isValidCredentialsProvided() {

        // Store values at the time of the login attempt.
        String name = ed_name.getText().toString();
        String email = ed_email.getText().toString();
        String password = ed_password.getText().toString();

        // Reset errors.
        ed_name.setError(null);
        ed_email.setError(null);
        ed_password.setError(null);
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(name)) {
            ed_name.setError("Required");
            ed_name.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            ed_email.setError("Required");
            ed_email.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ed_email.setError("Invalid");
            ed_email.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            ed_password.setError("Required");
            ed_password.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            ed_password.setError("Cannot be less then 6 digit");
            ed_password.requestFocus();
            return false;
        }

        return true;
    }


    private ProgressDialog progressDialog;

    public void showProgressDialog(String message, boolean isIntermidiate, boolean isCancelable) {
        /**/
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
        }
        if (progressDialog.isShowing()) {
            return;
        }
        progressDialog.setIndeterminate(isIntermidiate);
        progressDialog.setCancelable(isCancelable);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog == null) {
            return;
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
