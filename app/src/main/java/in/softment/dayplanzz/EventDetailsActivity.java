package in.softment.dayplanzz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import in.softment.dayplanzz.Interface.CheckFavoriteListener;
import in.softment.dayplanzz.Model.EventModel;
import in.softment.dayplanzz.Utils.Services;

public class EventDetailsActivity extends AppCompatActivity {
    GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        EventModel eventModel = (EventModel) getIntent().getSerializableExtra("eventModel");

        if (eventModel == null) {
            finish();
        }
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView eventImage = findViewById(R.id.eventImage);
        TextView eventNameTop = findViewById(R.id.eventNameTop);
        TextView eventDate = findViewById(R.id.eventDate);
        TextView eventName = findViewById(R.id.eventName);
        TextView subcategoryName = findViewById(R.id.subcategoryName);
        TextView eventStartTime = findViewById(R.id.eventStartTime);
        TextView eventLength = findViewById(R.id.eventLength);
        TextView eventAges = findViewById(R.id.eventAges);
        TextView eventOrganiser = findViewById(R.id.eventOrganiser);
        TextView website = findViewById(R.id.website);
        TextView eventDescription = findViewById(R.id.eventDescription);
        TextView price = findViewById(R.id.price);
        ImageView favBtn = findViewById(R.id.favBtn);

        RelativeLayout favRR = findViewById(R.id.favRR);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                        Services.showCenterToast(EventDetailsActivity.this,"Inloggning Krävs");
                        return;
                    }

                    if (favBtn.getTag().toString().equalsIgnoreCase("fav")) {
                        Services.removeFavorites(eventModel.uid);
                        favBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    }
                    else {
                        Services.addFavorites(eventModel.getOrganiserName(),eventModel.uid);
                        favBtn.setImageResource(R.drawable.ic_baseline_favorite_red);
                    }

                }
            });

            Services.checkFavorites(eventModel.uid, new CheckFavoriteListener() {
                @Override
                public void onCallBack(Boolean isFav) {
                    if (isFav) {
                        favBtn.setTag("fav");
                        favBtn.setImageResource(R.drawable.ic_baseline_favorite_red);
                    }
                    else {
                        favBtn.setTag("unfav");
                        favBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    }
                }
            });
        }
        else {
            favRR.setVisibility(View.GONE);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (!eventModel.getEventImage().isEmpty()) {
            Glide.with(this).load(eventModel.getEventImage()).placeholder(R.drawable.placeholder1).into(eventImage);
        }

        eventNameTop.setText(eventModel.getTitle());
        eventDate.setText(Services.convertDateToStringWithoutDash(eventModel.getStartDateAndTime()));
        eventName.setText(eventModel.getTitle());
        subcategoryName.setText(eventModel.getSubCategoryName());
        eventStartTime.setText(Services.convertoTimeString(eventModel.getStartDateAndTime()));
        if (eventModel.getEventLength() == 0) {
            eventLength.setText("Ingen sluttid");
        }
        else {
            eventLength.setText(eventModel.eventLength+"h");
        }

        String allAges = "";
        for (String age :  eventModel.ages) {
            allAges = allAges + age+", ";
        }
        if (!allAges.isEmpty()) {
           allAges = allAges.substring(0, allAges.length() - 2);
        }
        eventAges.setText(allAges);
        eventOrganiser.setText(eventModel.getOrganiserName());
        website.setText(eventModel.getWebsiteLink());
        eventDescription.setText(eventModel.getDescription());
        if (eventModel.getEventPrice() == 0) {
            price.setText("Fri");;
        }
        else {
            price.setText(eventModel.getEventPrice()+" Kr");
            price.setVisibility(View.VISIBLE);
        }

          mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                LatLng latLng = new LatLng(eventModel.getLatitude(),eventModel.getLongitude());
                mMap = googleMap;
                mMap.addMarker(new
                        MarkerOptions().position(latLng).title(eventModel.getTitle()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            }
        });

          findViewById(R.id.openMap).setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  String uri = "http://maps.google.com/maps?q=loc:" + eventModel.getLatitude() + "," + eventModel.getLongitude() + " (" + eventModel.getTitle()+ ")";
                  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                  startActivity(intent);

              }
          });
    }
}
