package com.creative.firebasetest.fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.creative.firebasetest.MainActivity;
import com.creative.firebasetest.R;
import com.creative.firebasetest.Utility.DeviceInfoUtils;
import com.creative.firebasetest.Utility.RunnTimePermissions;
import com.creative.firebasetest.alertbanner.AlertDialogForAnything;
import com.creative.firebasetest.appdata.GlobalAppAccess;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    // UI references.
    private Button btn_submit;
    private EditText ed_email, ed_password;
    private TextView tv_register_now, tv_reset_password;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        init(view);

        return view;
    }


    private void init(View view) {
        //gson = new Gson();
        ed_email = view.findViewById(R.id.ed_email);
        ed_password = view.findViewById(R.id.ed_password);
        btn_submit = view.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        tv_register_now = view.findViewById(R.id.tv_register_now);
        tv_register_now.setOnClickListener(this);
        tv_reset_password = view.findViewById(R.id.tv_reset_password);
        tv_reset_password.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.tv_register_now) {
            ((MainActivity) getActivity()).addRegisterFragment();
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
                sendRequestoFirebaseForLogin(ed_email.getText().toString(), ed_password.getText().toString());
            }
        }

        if (id == R.id.tv_reset_password) {
            showDialogForResetPassword();
        }
    }


    private void sendRequestoFirebaseForLogin(String email, String password) {
        showProgressDialog("Loading...", true, false);

        //authenticate user
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        dismissProgressDialog();

                        if (!task.isSuccessful()) {

                            Toast.makeText(getActivity(), "Please provide valid credentials", Toast.LENGTH_LONG).show();

                        } else {
                            ((MainActivity) getActivity()).processLogin();
                        }
                    }
                });
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private boolean isValidCredentialsProvided() {

        // Store values at the time of the login attempt.
        String email = ed_email.getText().toString();
        String password = ed_password.getText().toString();

        // Reset errors.
        ed_email.setError(null);
        ed_password.setError(null);

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


        return true;
    }


    private void showDialogForResetPassword() {
        final Dialog dialog_start = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog_start.setCancelable(true);
        dialog_start.setContentView(R.layout.dialog_reset_password);

        final EditText ed_email = dialog_start.findViewById(R.id.ed_email);
        Button btn_reset = dialog_start.findViewById(R.id.btn_reset);
        Button btn_cancel = dialog_start.findViewById(R.id.btn_cancel);


        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = ed_email.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    ed_email.setError("Required");
                    ed_email.requestFocus();
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    ed_email.setError("Invalid");
                    ed_email.requestFocus();
                    return;
                }

                // [START send_password_reset]
                FirebaseAuth auth = FirebaseAuth.getInstance();


                showProgressDialog("Loading...", true, false);
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                dismissProgressDialog();

                                dialog_start.dismiss();

                                if (task.isSuccessful()) {
                                    // Log.d(TAG, "Email sent.");

                                    Toast.makeText(getActivity(), "Please check your email for password recovery!", Toast.LENGTH_LONG).show();


                                }else{
                                    Toast.makeText(getActivity(), "Error! This email doesn't have an account.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });


            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_start.dismiss();
            }
        });

        dialog_start.show();
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
