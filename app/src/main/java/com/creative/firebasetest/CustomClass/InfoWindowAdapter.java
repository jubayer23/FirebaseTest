package com.creative.firebasetest.CustomClass;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creative.firebasetest.R;
import com.creative.firebasetest.model.InfoWindowData;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public InfoWindowAdapter(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.marker_info_window, null);


        ImageView img_icon = view.findViewById(R.id.img_icon);
        TextView tv_icon_details = view.findViewById(R.id.tv_icon_details);
        LinearLayout ll_alert_message_container = view.findViewById(R.id.ll_alert_message_container);

        //name_tv.setText(marker.getTitle());
       // details_tv.setText(marker.getSnippet());

        final InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();


        img_icon.setImageResource(context.getResources().getIdentifier(infoWindowData.getIconName(), "drawable", context.getPackageName()));
        tv_icon_details.setText(infoWindowData.getIconDetails());

        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //if(infoWindowData.getUID().equalsIgnoreCase("0") || user == null || !infoWindowData.getUID().equals(user.getUid())){
        //    ll_alert_message_container.setVisibility(View.GONE);
        //}else{
            ll_alert_message_container.setVisibility(View.VISIBLE);
        //}


       /* btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference().child("mark_position").child(infoWindowData.getMarkerId()).removeValue();

                marker.remove();
            }
        });
*/
        return view;
    }
}