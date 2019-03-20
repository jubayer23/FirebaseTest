package com.creative.firebasetest.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.creative.firebasetest.CustomClass.InfoWindowAdapter;
import com.creative.firebasetest.R;
import com.creative.firebasetest.Utility.CheckLocationEnableStatus;
import com.creative.firebasetest.Utility.GpsEnableTool;
import com.creative.firebasetest.appdata.GlobalAppAccess;
import com.creative.firebasetest.appdata.MydApplication;
import com.creative.firebasetest.model.InfoWindowData;
import com.creative.firebasetest.model.MarkPosition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private MapView mapView;

    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationClient;

    private LinearLayout ll_icon_container;

    private TextView tv_icon_details;

    private FloatingActionButton fab;

    ArrayList<ImageView> icons = new ArrayList<>();

    private ImageView selectedIcon = null;

    public MapFragment() {
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
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        init(view);

        initMapView(view, savedInstanceState);

        initializeIcon();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        return view;
    }

    private void init(View view) {

        ll_icon_container = view.findViewById(R.id.ll_icon_container);

        tv_icon_details = view.findViewById(R.id.tv_icon_details);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    private void initializeIcon() {
        ll_icon_container.removeAllViews();
        icons.clear();


        int count = 0;
        for (String icon_name : GlobalAppAccess.icon_name) {
            LinearLayout icon_container = new LinearLayout(getActivity());
            ImageView img = new ImageView(getActivity());

            LinearLayout.LayoutParams lP_container =
                    new LinearLayout.LayoutParams(MydApplication.getInstance().getPixelValue(80),
                            MydApplication.getInstance().getPixelValue(80));

            LinearLayout.LayoutParams lP =
                    new LinearLayout.LayoutParams(MydApplication.getInstance().getPixelValue(70),
                            MydApplication.getInstance().getPixelValue(70));

           // lP_container.setMargins(MydApplication.getInstance().getPixelValue(10), 0, 0, MydApplication.getInstance().getPixelValue(5));
            icon_container.setGravity(Gravity.CENTER);

            img.setTag(count);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    selectedIcon = (ImageView) view;

                    clearBackgroundOfAllIcon();

                    tv_icon_details.setText(GlobalAppAccess.icon_details[Integer.parseInt(view.getTag().toString())]);

                   // view.setBackgroundColor(getActivity().getResources().getColor(R.color.blue_for_keyboard_blur));
                    LinearLayout linearLayout = (LinearLayout) view.getParent();
                    linearLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.blue_for_keyboard_blur));
                }
            });


            icon_container.setLayoutParams(lP_container);

            img.setLayoutParams(lP);
            img.setImageResource(getResources().getIdentifier(icon_name, "drawable", getActivity().getPackageName()));

            icons.add(img);

            icon_container.addView(img);
            ll_icon_container.addView(icon_container);


            count++;
        }

    }

    private void clearBackgroundOfAllIcon() {
        for (ImageView img : icons) {
            //img.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
            LinearLayout linearLayout = (LinearLayout) img.getParent();
            linearLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        }
    }

    private void initMapView(View view, Bundle savedInstanceState) {
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        showProgressDialog("please wait...", true, false);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        dismissProgressDialog();
        this.mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        InfoWindowAdapter customInfoWindow = new InfoWindowAdapter(getActivity());
        mMap.setInfoWindowAdapter(customInfoWindow);


        //add location button click listener
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                CheckLocationEnableStatus checkLocationEnableStatus = new CheckLocationEnableStatus(getActivity());
                if (!checkLocationEnableStatus.canGetLocation()) {
                    GpsEnableTool gpsEnableTool = new GpsEnableTool(getActivity());
                    gpsEnableTool.enableGPs();
                }
                //TODO: Any custom actions
                return false;
                //Returning false will essentially call the super method. Returning true will not.
            }
        });

        mMap.setOnInfoWindowClickListener(this);


        placeAllMarkerInMap();


    }

    private void placeAllMarkerInMap() {


        showProgressDialog("Loading...", true, false);
        FirebaseDatabase.getInstance().getReference().child("mark_position")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            MarkPosition markPosition = snapshot.getValue(MarkPosition.class);
                            //Log.d("DEBUG", String.valueOf(snapshot.getKey()));

                            addMarkerOnMap(markPosition.getLat(), markPosition.getLang(), markPosition.getUserUID(), markPosition.markerDetails, markPosition.getMarkerIconDrawableName(), snapshot.getKey());

                        }


                        addPlaceChangeEventListener();

                        zoomToCurrentLocation();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void addPlaceChangeEventListener() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("DEBUG", "onChildAdded:" + dataSnapshot.getKey());

                // MarkPosition user = dataSnapshot.getValue(MarkPosition.class);
                // Log.d("DEBUG_2",String.valueOf(user.getLat()));

                // A new comment has been added, add it to the displayed list
                //Comment comment = dataSnapshot.getValue(Comment.class);

                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("DEBUG", "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                //Comment newComment = dataSnapshot.getValue(Comment.class);
                //String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("DEBUG", "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                // String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("DEBUG", "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                // Comment movedComment = dataSnapshot.getValue(Comment.class);
                // String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DEBUG", "postComments:onCancelled", databaseError.toException());
                // Toast.makeText(mContext, "Failed to load comments.",
                //         Toast.LENGTH_SHORT).show();
            }
        };
        FirebaseDatabase.getInstance().getReference().child("mark_position").addChildEventListener(childEventListener);
    }

    @SuppressLint("MissingPermission")
    private void zoomToCurrentLocation() {
        showProgressDialog("Loading...", true, false);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        dismissProgressDialog();
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            zoomToSpecificLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                        }
                    }
                });
    }

    protected void zoomToSpecificLocation(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to location user
                .zoom(20)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {


        int id = view.getId();

        if (id == R.id.fab) {

            CheckLocationEnableStatus checkLocationEnableStatus = new CheckLocationEnableStatus(getActivity());
            if (!checkLocationEnableStatus.canGetLocation()) {
                GpsEnableTool gpsEnableTool = new GpsEnableTool(getActivity());
                gpsEnableTool.enableGPs();
                return;
            }

            if (selectedIcon == null) {
                Toast.makeText(getActivity(), "Please select a icon first", Toast.LENGTH_LONG).show();
                return;
            }

            showDialogForAddMarkerConfirmation();


        }

    }

    @SuppressLint("MissingPermission")
    private void showDialogForAddMarkerConfirmation() {
        final Dialog dialog_start = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog_start.setCancelable(true);
        dialog_start.setContentView(R.layout.dialog_notify_add_marker);
        LinearLayout ll_container = (LinearLayout) dialog_start.findViewById(R.id.ll_container);
        TextView tv_msg = (TextView) dialog_start.findViewById(R.id.tv_icon_details);
        ImageView icon = dialog_start.findViewById(R.id.img_icon);
        Button btn_add = dialog_start.findViewById(R.id.btn_add);
        Button btn_cancel = dialog_start.findViewById(R.id.btn_cancel);

        tv_msg.setText(GlobalAppAccess.icon_details[(int) selectedIcon.getTag()]);
        icon.setImageResource(getResources().getIdentifier(GlobalAppAccess.icon_name[(int) selectedIcon.getTag()], "drawable", getActivity().getPackageName()));


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog("Loading...", true, false);
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    //zoomToSpecificLocation(new LatLng(location.getLatitude(), location.getLongitude()));

                                    sendLocationToFireBase(location);

                                    dialog_start.dismiss();
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


    private void sendLocationToFireBase(final Location location) {

        // store app title to 'app_title' node
        //  mFirebaseInstance.getReference("app_title").setValue("Realtime Database");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid;
        if (user != null) {
            uid = user.getUid();

        } else {
            uid = "0";
        }

        MarkPosition markPosition = new MarkPosition(uid, location.getLatitude(), location.getLongitude(), GlobalAppAccess.icon_details[(int) selectedIcon.getTag()], GlobalAppAccess.icon_name[(int) selectedIcon.getTag()]);

        // mFirebaseInstance.getReference("mark_position").setValue(markPosition);

        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();


// Creating new user node, which returns the unique key value
// new user node would be /users/$userid/
        final String markID = mDatabaseRef.child("mark_position").push().getKey();

        // pushing user to 'users' node using the markID
        mDatabaseRef.child("mark_position").child(markID).setValue(markPosition, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                //Log.d("DEBUG", "Value was set. Error = " + databaseError);
                Toast.makeText(getActivity(), "Successfully added", Toast.LENGTH_SHORT).show();
                addMarkerOnMap(location.getLatitude(), location.getLongitude(), uid, GlobalAppAccess.icon_details[(int) selectedIcon.getTag()], GlobalAppAccess.icon_name[(int) selectedIcon.getTag()], markID);
            }
        });


        //  mDatabaseRef.child("new_node").setValue("hello");
        // mDatabaseRef.child("new_node2").push();
        //mDatabaseRef.child("new_node3").child("new_node4").push();
        // mDatabaseRef.child("new_node4").child("new_node5").push().setValue("okay");


        /*So amra ai onusiddante uponito holam j push() korle new ID create hoy but push na
         * kore only setValue() dile kunu new id create hoy na just data oi node a add hoy.*/


        dismissProgressDialog();

    }


    private void addMarkerOnMap(double lat, double lang, String UID, String markerDetails, String markerIdDrawableName, String markId) {

        InfoWindowData infoWindowData = new InfoWindowData(markId, markerIdDrawableName, markerDetails, UID);


        // adding a marker on map with image from  drawable
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lang))
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(getResources().getIdentifier(markerIdDrawableName, "drawable", getActivity().getPackageName())))));
        marker.setTag(infoWindowData);
    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        final InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

        showDialogForDeleteLocation(marker, infoWindowData);
        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (infoWindowData.getUID().equalsIgnoreCase("0") || user == null || !infoWindowData.getUID().equals(user.getUid())) {

        } else {

        }*/
    }

    private void showDialogForDeleteLocation(final Marker marker, final InfoWindowData infoWindowData) {

        final Dialog dialog_start = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog_start.setCancelable(true);
        dialog_start.setContentView(R.layout.dialog_marker_info);
        LinearLayout ll_container = (LinearLayout) dialog_start.findViewById(R.id.ll_container);
        TextView tv_msg = (TextView) dialog_start.findViewById(R.id.tv_icon_details);
        ImageView icon = dialog_start.findViewById(R.id.img_icon);
        Button btn_delete = dialog_start.findViewById(R.id.btn_delete);
        Button btn_cancel = dialog_start.findViewById(R.id.btn_cancel);

        tv_msg.setText(infoWindowData.getIconDetails());
        icon.setImageResource(getResources().getIdentifier(infoWindowData.getIconName(), "drawable", getActivity().getPackageName()));


        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference().child("mark_position").child(infoWindowData.getMarkerId()).removeValue();

                marker.remove();

                Toast.makeText(getActivity(), "Successfully deleted", Toast.LENGTH_SHORT).show();

                dialog_start.dismiss();

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
