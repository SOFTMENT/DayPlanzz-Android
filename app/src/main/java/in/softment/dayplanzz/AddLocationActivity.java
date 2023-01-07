package in.softment.dayplanzz;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import in.softment.dayplanzz.Model.LocationModel;
import in.softment.dayplanzz.Model.UserModel;
import in.softment.dayplanzz.Utils.ProgressHud;
import in.softment.dayplanzz.Utils.Services;

public class AddLocationActivity extends AppCompatActivity {
    private SupportMapFragment mapFragment;
    private LocationModel locationModel = null;
    private GoogleMap mMap;
    private TextView latitudeAndLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        EditText name = findViewById(R.id.username);
        latitudeAndLongitude = findViewById(R.id.latitudeandlongitude);


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        findViewById(R.id.addLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAutocompleteIntent();
            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sName = name.getText().toString();
                if (sName.isEmpty()) {
                    Services.showCenterToast(AddLocationActivity.this,"Ange platsnamn");
                }
                else {
                    ProgressHud.show(AddLocationActivity.this,"");
                    locationModel.locationName = sName;
                   locationModel.id = FirebaseFirestore.getInstance().collection("Users").document(UserModel.data.getUid()).collection("Locations").document().getId();
                    FirebaseFirestore.getInstance().collection("Users").document(UserModel.data.getUid()).collection("Locations")
                            .document(locationModel.getId()).set(locationModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                 if (task.isSuccessful()) {
                                     Services.showCenterToast(AddLocationActivity.this,"Sparad");
                                     new Handler().postDelayed(new Runnable() {
                                         @Override
                                         public void run() {
                                             finish();
                                         }
                                     },1200);
                                 }
                                 else {
                                     Services.showCenterToast(AddLocationActivity.this,task.getException().getLocalizedMessage());
                                 }
                        }
                    });
                }
            }
        });
    }

    private void startAutocompleteIntent() {

        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS_COMPONENTS,
                Place.Field.LAT_LNG, Place.Field.VIEWPORT,Place.Field.ADDRESS);

        // Build the autocomplete intent with field, country, and type filters applied
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.ADDRESS)
                .build(this);
        startAutocomplete.launch(intent);
    }


    private final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            (ActivityResultCallback<ActivityResult>) result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        fillInAddress(place);
                    }

                }  // The user canceled the operation.

            });

    private void fillInAddress(Place place) {
        locationModel = new LocationModel();
        locationModel.latitude = Objects.requireNonNull(place.getLatLng()).latitude;
        locationModel.longitude  = place.getLatLng().longitude;

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;
                mMap.addMarker(new
                        MarkerOptions().position(place.getLatLng()).title("Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            }
        });
        latitudeAndLongitude.setText(locationModel.getLatitude()+" , "+ locationModel.getLongitude());

    }


}
