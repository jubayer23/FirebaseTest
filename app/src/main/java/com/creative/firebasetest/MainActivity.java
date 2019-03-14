package com.creative.firebasetest;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creative.firebasetest.Utility.RunnTimePermissions;
import com.creative.firebasetest.fragment.LoginFragment;
import com.creative.firebasetest.fragment.MapFragment;
import com.creative.firebasetest.fragment.ProfileFragment;
import com.creative.firebasetest.fragment.RegisterFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout btn_map, btn_login, btn_profile;


    private Fragment fragment_map, fragment_login, fragment_register, fragment_profile;

    private static final String TAG_FRAGMENT_MAP = "map_fragment";
    private static final String TAG_FRAGMENT_LOGIN = "login_fragment";
    private static final String TAG_FRAGMENT_REGISTER = "register_fragment";
    private static final String TAG_FRAGMENT_PROFILE = "profile_fragment";

    private static final int TAB_COLOR_UNSELECTED = R.color.gray_dark;
    private static final int TAB_COLOR_SELECTED = R.color.gray_light;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        initFragment();

// [START check_current_user]



       if(RunnTimePermissions.requestForAllRuntimePermissions(this)){
           appHasNowPermissionNowProceed();
       }


    }

    private void appHasNowPermissionNowProceed(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            changeUIbasedOnLoggedIn(true);
        } else {
            changeUIbasedOnLoggedIn(false);
        }

        btnToggleColor(TAG_FRAGMENT_MAP);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager
                .beginTransaction();
        transaction.add(R.id.container, fragment_map, TAG_FRAGMENT_MAP);
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case RunnTimePermissions.PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   appHasNowPermissionNowProceed();
                } else {
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void init() {


        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        btn_profile = findViewById(R.id.btn_profile);
        btn_profile.setOnClickListener(this);

        btn_map = findViewById(R.id.btn_map);
        btn_map.setOnClickListener(this);
    }

    private void initFragment() {

        fragment_login = new LoginFragment();

        fragment_map = new MapFragment();

        fragment_register = new RegisterFragment();

        fragment_profile = new ProfileFragment();

    }

    private void btnToggleColor(String tag) {
        if (tag.equalsIgnoreCase(TAG_FRAGMENT_MAP)) {

            btn_map.setBackgroundColor(getResources().getColor(TAB_COLOR_SELECTED));
            btn_login.setBackgroundColor(getResources().getColor(TAB_COLOR_UNSELECTED));
            btn_profile.setBackgroundColor(getResources().getColor(TAB_COLOR_UNSELECTED));

        } else if (tag.equalsIgnoreCase(TAG_FRAGMENT_LOGIN)) {
            btn_map.setBackgroundColor(getResources().getColor(TAB_COLOR_UNSELECTED));
            btn_login.setBackgroundColor(getResources().getColor(TAB_COLOR_SELECTED));
            btn_profile.setBackgroundColor(getResources().getColor(TAB_COLOR_UNSELECTED));
        } else if (tag.equalsIgnoreCase(TAG_FRAGMENT_REGISTER)) {
            btn_map.setBackgroundColor(getResources().getColor(TAB_COLOR_UNSELECTED));
            btn_login.setBackgroundColor(getResources().getColor(TAB_COLOR_SELECTED));
            btn_profile.setBackgroundColor(getResources().getColor(TAB_COLOR_UNSELECTED));
        } else if (tag.equalsIgnoreCase(TAG_FRAGMENT_PROFILE)) {
            btn_map.setBackgroundColor(getResources().getColor(TAB_COLOR_UNSELECTED));
            btn_login.setBackgroundColor(getResources().getColor(TAB_COLOR_UNSELECTED));
            btn_profile.setBackgroundColor(getResources().getColor(TAB_COLOR_SELECTED));
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.btn_map) {
            addMapFragment();
        }

        if (id == R.id.btn_login) {
            addLoginFragment();
        }
        if (id == R.id.btn_profile) {
            addProfileFragment();
        }

    }

    public void addMapFragment() {
        btnToggleColor(TAG_FRAGMENT_MAP);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (fragment_map.isAdded()) { // if the fragment is already in container
            transaction.show(fragment_map);
        } else { // fragment needs to be added to frame container
            transaction.add(R.id.container, fragment_map, TAG_FRAGMENT_MAP);
        }
        // Hide fragment B
        if (fragment_login.isAdded()) {
            transaction.hide(fragment_login);
        }
        if (fragment_register.isAdded()) {
            transaction.hide(fragment_register);
        }
        if (fragment_profile.isAdded()) {
            transaction.hide(fragment_profile);
        }

        // Hide fragment C
        // Commit changes
        transaction.commit();
    }

    public void addLoginFragment() {
        btnToggleColor(TAG_FRAGMENT_LOGIN);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (fragment_login.isAdded()) { // if the fragment is already in container
            transaction.show(fragment_login);
        } else { // fragment needs to be added to frame container
            transaction.add(R.id.container, fragment_login, TAG_FRAGMENT_LOGIN);
        }
        // Hide fragment B
        if (fragment_map.isAdded()) {
            transaction.hide(fragment_map);
        }
        if (fragment_register.isAdded()) {
            transaction.hide(fragment_register);
        }
        if (fragment_profile.isAdded()) {
            transaction.hide(fragment_profile);
        }

        // Hide fragment C
        // Commit changes
        transaction.commit();
    }

    public void addRegisterFragment() {
        btnToggleColor(TAG_FRAGMENT_REGISTER);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (fragment_register.isAdded()) { // if the fragment is already in container
            transaction.show(fragment_register);
        } else { // fragment needs to be added to frame container
            transaction.add(R.id.container, fragment_register, TAG_FRAGMENT_REGISTER);
        }
        // Hide fragment B
        if (fragment_map.isAdded()) {
            transaction.hide(fragment_map);
        }
        if (fragment_login.isAdded()) {
            transaction.hide(fragment_login);
        }
        if (fragment_profile.isAdded()) {
            transaction.hide(fragment_profile);
        }

        // Hide fragment C
        // Commit changes
        transaction.commit();
    }

    public void addProfileFragment() {
        btnToggleColor(TAG_FRAGMENT_PROFILE);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (fragment_profile.isAdded()) { // if the fragment is already in container
            transaction.show(fragment_profile);
        } else { // fragment needs to be added to frame container
            transaction.add(R.id.container, fragment_profile, TAG_FRAGMENT_PROFILE);
        }
        // Hide fragment B
        if (fragment_map.isAdded()) {
            transaction.hide(fragment_map);
        }
        if (fragment_login.isAdded()) {
            transaction.hide(fragment_login);
        }
        if (fragment_register.isAdded()) {
            transaction.hide(fragment_register);
        }

        // Hide fragment C
        // Commit changes
        transaction.commit();
    }

    public void processLogin() {

        changeUIbasedOnLoggedIn(true);


        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        btnToggleColor(TAG_FRAGMENT_MAP);

        if (fragment_map.isAdded()) { // if the fragment is already in container
            transaction.show(fragment_map);
        } else { // fragment needs to be added to frame container
            transaction.add(R.id.container, fragment_map, TAG_FRAGMENT_MAP);
        }

// Hide fragment B
        if (fragment_login.isAdded()) {
            transaction.remove(fragment_login);
        }
        if (fragment_register.isAdded()) {
            transaction.remove(fragment_register);
        }

        transaction.commit();
    }

    public void processLogout() {
        changeUIbasedOnLoggedIn(false);


        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        btnToggleColor(TAG_FRAGMENT_MAP);

        if (fragment_map.isAdded()) { // if the fragment is already in container
            transaction.show(fragment_map);
        } else { // fragment needs to be added to frame container
            transaction.add(R.id.container, fragment_map, TAG_FRAGMENT_MAP);
        }

// Hide fragment B
        if (fragment_profile.isAdded()) {
            transaction.remove(fragment_profile);
        }


        transaction.commit();
    }

    private void changeUIbasedOnLoggedIn(boolean isLoggedIn) {
        if (isLoggedIn) {
            btn_login.setVisibility(View.GONE);
            btn_profile.setVisibility(View.VISIBLE);
        } else {
            btn_login.setVisibility(View.VISIBLE);
            btn_profile.setVisibility(View.GONE);
        }
    }
}
