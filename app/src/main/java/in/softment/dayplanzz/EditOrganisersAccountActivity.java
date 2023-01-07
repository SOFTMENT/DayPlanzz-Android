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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import in.softment.dayplanzz.Interface.LocationListener;
import in.softment.dayplanzz.Model.LocationModel;
import in.softment.dayplanzz.Model.UserModel;
import in.softment.dayplanzz.Utils.ProgressHud;
import in.softment.dayplanzz.Utils.Services;

public class EditOrganisersAccountActivity extends AppCompatActivity {
    private AutoCompleteTextView address;
    private Spinner spinnerLocations;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private LinearLayout mapLL;
    private ArrayAdapter<String> adapter;
    private LocationModel selectedLocationModel = null;
    private boolean isLocationSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_organisers_account);

        spinnerLocations = findViewById(R.id.spinnerLocation);
        address = findViewById(R.id.fullAddress);
        ArrayList<String> locationNames = new ArrayList<>();
        ArrayList<LocationModel> locationModels = new ArrayList<>();
        locationNames.add("Välj plats");
        EditText registerNumber = findViewById(R.id.companyNumber);
        if (UserModel.data.accountType.equalsIgnoreCase("company")) {
            registerNumber.setHint("org-nummer");
            registerNumber.setText(UserModel.data.getRegNumber());
            spinnerLocations.setVisibility(View.GONE);
            address.setVisibility(View.VISIBLE);
            address.setText(UserModel.data.getLocation());
        }
        else {


            spinnerLocations.setVisibility(View.VISIBLE);
            address.setVisibility(View.GONE);
            registerNumber.setHint("Förenings-nummer");
            registerNumber.setText(UserModel.data.getAssociationNumber());

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, locationNames);
            spinnerLocations.setAdapter(adapter);

            ProgressHud.show(EditOrganisersAccountActivity.this,"");
            Services.getAllLocations(UserModel.data.getUid(), new LocationListener() {
                @Override
                public void onCallback(ArrayList<LocationModel> locModels) {
                    ProgressHud.dialog.dismiss();
                    locationModels.addAll(locModels);

                    for (LocationModel locationModel : locationModels) {
                        locationNames.add(locationModel.getLocationName());
                        adapter.notifyDataSetChanged();
                    }

                }
            });

            spinnerLocations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i > 0) {
                        selectedLocationModel = locationModels.get(i - 1);
                        UserModel.data.location = selectedLocationModel.locationName;
                        UserModel.data.locationId = selectedLocationModel.getId();
                        fillInAddress1(selectedLocationModel.getLatitude(), selectedLocationModel.getLongitude());
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }



        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        EditText website = findViewById(R.id.website);
        website.setText(UserModel.data.getWebsite());
        EditText name = findViewById(R.id.name);
        name.setText(UserModel.data.getContactPerson());
        EditText email = findViewById(R.id.email);
        email.setText(UserModel.data.getContactPersonEmail());
        EditText phone = findViewById(R.id.phonenumber);
        phone.setText(UserModel.data.getContactPersonPhone());
        EditText description = findViewById(R.id.description);
       description.setText(UserModel.data.getDescription());



        PlacesClient placesClient = Places.createClient(this);



        address.setFocusable(false);
        address.setOnClickListener(v -> startAutocompleteIntent());
        mapLL = findViewById(R.id.mapLL);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);



        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String sRegiNum = registerNumber.getText().toString();
                    String sWebsite = website.getText().toString();
                    String sName = name.getText().toString();
                    String sEmail = email.getText().toString();
                    String sPhone = phone.getText().toString();
                    String sDescription = description.getText().toString();
                    String sAddress = address.getText().toString();

                    if (sRegiNum.isEmpty()) {
                        if (UserModel.data.accountType.equalsIgnoreCase("company")) {
                            Services.showCenterToast(EditOrganisersAccountActivity.this,"Ange org-nummer");
                        }
                        else {
                            Services.showCenterToast(EditOrganisersAccountActivity.this,"Ange Förenings-nummer");
                        }

                    }
                    else if (sWebsite.isEmpty()) {
                        Services.showCenterToast(EditOrganisersAccountActivity.this,"Ange webbadress");
                    }
                    else if (sName.isEmpty()) {
                        Services.showCenterToast(EditOrganisersAccountActivity.this,"Ange kontaktperson");
                    }
                    else if (sEmail.isEmpty()) {
                        Services.showCenterToast(EditOrganisersAccountActivity.this,"Skriv in epostadress");
                    }
                    else if (sPhone.isEmpty()) {
                        Services.showCenterToast(EditOrganisersAccountActivity.this,"Skriv in telefonnummer");
                    }
                    else if (sDescription.isEmpty()) {
                        Services.showCenterToast(EditOrganisersAccountActivity.this,"Ange beskrivning");
                    }
                    else if (isLocationSelected) {
                        Services.showCenterToast(EditOrganisersAccountActivity.this,"Ange plats");
                    }
                    else {
                        if (UserModel.data.accountType.equalsIgnoreCase("company")) {
                            UserModel.data.regNumber = sRegiNum;
                        }
                        else {
                            UserModel.data.associationNumber = sRegiNum;
                        }

                        UserModel.data.website = sWebsite;
                        UserModel.data.contactPerson = sName;
                        UserModel.data.contactPersonEmail = sEmail;
                        UserModel.data.contactPersonPhone = sPhone;
                        UserModel.data.description = sDescription;
                        UserModel.data.location = sAddress;

                        ProgressHud.show(EditOrganisersAccountActivity.this,"");
                        FirebaseFirestore.getInstance().collection("Users").document(UserModel.data.getUid()).set(UserModel.data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Services.showCenterToast(EditOrganisersAccountActivity.this,"Profil uppdaterad");
                                }
                                else {
                                    Services.showDialog(EditOrganisersAccountActivity.this,"ERROR",task.getException().getLocalizedMessage());
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

                }

            });

    private void fillInAddress1(Double latitude, Double longitude) {

        LatLng latLng = new LatLng(latitude, longitude);
        UserModel.data.latitude = latitude;
        UserModel.data.longitude = longitude;
        UserModel.data.geoHash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(UserModel.data.latitude, UserModel.data.longitude));
        mapLL.setVisibility(View.VISIBLE);
        isLocationSelected = true;
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;
                mMap.addMarker(new
                        MarkerOptions().position(latLng).title("Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            }
        });

    }
    private void fillInAddress(Place place) {

        UserModel.data.latitude = Objects.requireNonNull(place.getLatLng()).latitude;
        UserModel.data.longitude = place.getLatLng().longitude;
        UserModel.data.geoHash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(UserModel.data.latitude, UserModel.data.longitude));
        mapLL.setVisibility(View.VISIBLE);
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
        isLocationSelected = true;
        address.setText(place.getAddress());
        address.setText(place.getAddress());
        address.requestFocus();


    }
}
