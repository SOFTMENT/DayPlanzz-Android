package in.softment.dayplanzz;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import in.softment.dayplanzz.Interface.LocationListener;
import in.softment.dayplanzz.Model.CategoryModel;
import in.softment.dayplanzz.Model.EventModel;
import in.softment.dayplanzz.Model.LocationModel;
import in.softment.dayplanzz.Model.SubCategoryModel;
import in.softment.dayplanzz.Model.UserModel;
import in.softment.dayplanzz.Utils.ProgressHud;
import in.softment.dayplanzz.Utils.Services;

public class AddEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private EditText title, description,  startDate, startTime, website,eventPriceET;
    private Spinner spinnerLength, spinnerPrice;
    private Spinner spinnerLocations;
    private ArrayAdapter<String> durationAdapter, priceAdapter;
    private AppCompatSpinner categorySpinner, subCategorySpinner;
    private AutoCompleteTextView address;
    private LinearLayout mapLL;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private String catId = "";
    private String subCatId = "";
    private ArrayList<String>  categoryName;
    private ArrayList<CategoryModel>  categoryModels;
    private ArrayList<String> subCategoryName;
    private ArrayList<SubCategoryModel>  subCategoryModels;
    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;
    private Calendar myCalendar;
    private LinearLayout subCategoryLL;
    private EventModel eventModel;
    private CheckBox allAgesCheck, barnCheck, ungdomCheck, vuxenCheck, pensionerCheck;
    private RoundedImageView eventImage;
    private ImageView plusImage;
    private final int PICK_IMAGE_REQUEST = 1;
    private Uri resultUri = null;
    private int eventLength = -1;
    private int eventPrice = -1;
    private ArrayAdapter<String> adapter;
    private LocationModel selectedLocationModel = null;
    private boolean isLocationSelected = false;
    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), new ActivityResultCallback<CropImageView.CropResult>() {
        @Override
        public void onActivityResult(CropImageView.CropResult result) {
            if (result.isSuccessful()) {
                Uri uri = result.getUriContent();
                Bitmap bitmap = null;
                try {
                    plusImage.setVisibility(View.GONE);
                    resultUri = uri;
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    eventImage.setImageBitmap(bitmap);


                } catch (IOException e) {

                }
            }
            else {
                Services.showDialog(AddEventActivity.this, "ERROR",result.getError().getLocalizedMessage());
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        eventImage = findViewById(R.id.eventImage);
        plusImage =  findViewById(R.id.plusImage);

        eventModel = new EventModel();
        eventModel.id = FirebaseFirestore.getInstance().collection("Events").document().getId();

        //BACK
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //CheckBox
        allAgesCheck = findViewById(R.id.chooseAllCheck);

        barnCheck = findViewById(R.id.barnCheck);
        ungdomCheck = findViewById(R.id.ungdomCheck);
        vuxenCheck = findViewById(R.id.vuxenCheck);
        pensionerCheck = findViewById(R.id.pensionerCheck);

        allAgesCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if (b) {

                   barnCheck.setChecked(true);
                   ungdomCheck.setChecked(true);
                   vuxenCheck.setChecked(true);
                   pensionerCheck.setChecked(true);
               }
            }
        });

        barnCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    allAgesCheck.setChecked(false);
                }
            }
        });

        vuxenCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    allAgesCheck.setChecked(false);
                }
            }
        });

        ungdomCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    allAgesCheck.setChecked(false);
                }
            }
        });

       pensionerCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    allAgesCheck.setChecked(false);
                }
            }
        });


        mapLL = findViewById(R.id.mapLL);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        PlacesClient placesClient = Places.createClient(this);

        spinnerLocations = findViewById(R.id.spinnerLocation);


        address = findViewById(R.id.fullAddress);
        address.setFocusable(false);
        address.setOnClickListener(v -> startAutocompleteIntent());

        spinnerLength = findViewById(R.id.spinnerDuration);
        spinnerPrice = findViewById(R.id.spinnerPrice);
        ArrayList<String> locationNames = new ArrayList<>();
        ArrayList<LocationModel> locationModels = new ArrayList<>();
        locationNames.add("Välj plats");
        if (UserModel.data.accountType.equalsIgnoreCase("company")) {

            spinnerLocations.setVisibility(View.GONE);
            address.setVisibility(View.VISIBLE);
            address.setText(UserModel.data.getLocation());
        }
        else {


            spinnerLocations.setVisibility(View.VISIBLE);
            address.setVisibility(View.GONE);

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, locationNames);
            spinnerLocations.setAdapter(adapter);


            Services.getAllLocations(UserModel.data.getUid(), new LocationListener() {
                @Override
                public void onCallback(ArrayList<LocationModel> locModels) {

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


        ArrayList<String> durations = new ArrayList<>();
        ArrayList<String> prices = new ArrayList<>();

        durations.add("Välj Längd");
        durations.add("0 = Ingen sluttid");
        durations.add("1 hour");
        durations.add("2 hours");
        durations.add("4 hours");
        durations.add("6 hours");
        durations.add("8 hours");

        prices.add("Välj Pris");
        prices.add("Fri");
        prices.add("5 kr");
        prices.add("10 kr");
        prices.add("15 kr");
        prices.add("20 kr");
        prices.add("25 kr");
        prices.add("30 kr");
        prices.add("35 kr");
        prices.add("40 kr");
        prices.add("45 kr");
        prices.add("50 kr");
        prices.add("ANNAT");

        durationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, durations);
        spinnerLength.setAdapter(durationAdapter);

        priceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, prices);
        spinnerPrice.setAdapter(priceAdapter);

        spinnerPrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    switch (position) {
                        case 1 : eventPrice = 0;
                            eventPriceET.setVisibility(View.GONE);
                            break;
                        case 2 : eventPrice = 5;
                            eventPriceET.setVisibility(View.GONE);
                            break;
                        case 3 : eventPrice = 10;
                            eventPriceET.setVisibility(View.GONE);
                            break;
                        case 4 : eventPrice = 15;
                            eventPriceET.setVisibility(View.GONE);
                            break;
                        case 5 : eventPrice = 20;
                            eventPriceET.setVisibility(View.GONE);
                            break;
                        case 6 : eventPrice = 25;
                            eventPriceET.setVisibility(View.GONE);
                            break;
                        case 7 : eventPrice = 30;
                            eventPriceET.setVisibility(View.GONE);
                            break;
                        case 8 : eventPrice = 35;
                            eventPriceET.setVisibility(View.GONE);
                            break;
                        case 9 : eventPrice = 40;
                            eventPriceET.setVisibility(View.GONE);
                            break;
                        case 10 : eventPrice = 45;
                            eventPriceET.setVisibility(View.GONE);
                            break;
                        case 11 : eventPrice = 50;
                            eventPriceET.setVisibility(View.GONE);
                            break;
                        case 12 :
                            eventPrice = -2;
                            eventPriceET.setVisibility(View.VISIBLE);
                        break;
                        default: eventPrice = -1;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerLength.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {
                        switch (position) {
                            case 1 : eventLength = 0;
                            break;
                            case 2 : eventLength = 1;
                                break;
                            case 3: eventLength = 2;
                                break;
                            case 4 : eventLength = 4;
                                break;
                            case 5 : eventLength = 6;
                                break;
                            case 6 : eventLength = 8;
                                break;
                            default: eventLength = -1;
                        }
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        startDate = findViewById(R.id.startDate);
        startDate.setFocusable(false);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this, AddEventActivity.this,year, month,day);
                datePickerDialog.show();
            }
        });
        startTime = findViewById(R.id.startTime);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                     Calendar c = Calendar.getInstance();
                      hour = c.get(Calendar.HOUR);
                      minute = c.get(Calendar.MINUTE);
                     TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this, AddEventActivity.this, hour, minute, DateFormat.is24HourFormat(AddEventActivity.this));
                     timePickerDialog.show();
            }
        });

        startTime.setFocusable(false);
        website = findViewById(R.id.website);
        categorySpinner = findViewById(R.id.categorySpinner);
        subCategorySpinner = findViewById(R.id.subcategorySpinner);
        subCategoryLL = findViewById(R.id.subCategoryLL);

        eventPriceET = findViewById(R.id.eventPrice);



        categoryName = new ArrayList<String>();
        categoryModels = new ArrayList<>();

        subCategoryName = new ArrayList<>();
        subCategoryModels = new ArrayList<>();

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    catId = categoryModels.get(position - 1).getId();
                    eventModel.categoryId = catId;
                    eventModel.categoryName = categoryModels.get(position - 1).getName();

                    if (categoryModels.get(position - 1).isHasSubcategory()) {
                        eventModel.hasSubCategory = true;
                        getSubcategory(catId);
                    }
                    else {

                        eventModel.subcategoryId = "";
                        eventModel.subCategoryName = "";
                        eventModel.hasSubCategory = false;
                    }


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    subCatId = subCategoryModels.get(position - 1).getId();

                    eventModel.subcategoryId = subCatId;
                    eventModel.subCategoryName = subCategoryModels.get(position - 1).getName();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getCategories();

        //AddEvent
        findViewById(R.id.addEvent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sTitle = title.getText().toString().trim();
                String sDescription = description.getText().toString().trim();
                String sLocation = address.getText().toString();
                String sStartDate = startDate.getText().toString();
                String sStartTime = startTime.getText().toString();
                String websiteLink = website.getText().toString();


                if (resultUri == null) {
                    Services.showCenterToast(AddEventActivity.this,"Ladda upp bild");
                    return;
                }
                if (sTitle.isEmpty()) {
                    Services.showCenterToast(AddEventActivity.this,"Ange titel");
                    return;
                }
                if (sDescription.isEmpty()) {
                    Services.showCenterToast(AddEventActivity.this,"Ange beskrivning");
                    return;
                }
                if (eventModel.categoryId.isEmpty()) {
                    Services.showCenterToast(AddEventActivity.this,"Välj kategori");
                    return;
                }
                if ((eventModel.hasSubCategory && eventModel.subcategoryId.isEmpty())) {
                    Services.showCenterToast(AddEventActivity.this,"Välj underkategori");
                    return;
                }
                if (!(barnCheck.isChecked() || ungdomCheck.isChecked() || vuxenCheck.isChecked() || pensionerCheck.isChecked())) {
                    Services.showCenterToast(AddEventActivity.this,"Välj Ålder");
                    return;
                }
                if (!isLocationSelected) {
                    Services.showCenterToast(AddEventActivity.this,"Välj Adress");
                    return;
                }
                if (sStartDate.isEmpty()) {
                    Services.showCenterToast(AddEventActivity.this,"Välj Startdatum");
                    return;
                }
                if (sStartTime.isEmpty()) {
                    Services.showCenterToast(AddEventActivity.this,"Välj Starttid");
                    return;
                }
                if (eventLength == -1) {
                    Services.showCenterToast(AddEventActivity.this,"Välj Längd");
                    return;
                }
                if (websiteLink.isEmpty()) {
                    Services.showCenterToast(AddEventActivity.this,"Ange webbadress");
                    return;
                }
                if (eventPrice == -1) {
                    Services.showCenterToast(AddEventActivity.this,"Välj Pris");
                }

                else {

                    ProgressHud.show(AddEventActivity.this,"");
                    ArrayList<String> list = new ArrayList<>();
                    if (barnCheck.isChecked()) {
                        list.add("barn");
                    }
                    if (ungdomCheck.isChecked()) {
                        list.add("ungdom");
                    }
                    if (vuxenCheck.isChecked()) {
                        list.add("vuxen");
                    }
                    if (pensionerCheck.isChecked()) {
                        list.add("pensioner");
                    }

                    eventModel.title = sTitle;
                    eventModel.description = sDescription;
                    eventModel.setAges(list);
                    eventModel.location = sLocation;
                    eventModel.startDateAndTime = myCalendar.getTime();
                    eventModel.websiteLink = websiteLink;
                    eventModel.eventLength = eventLength;
                    eventModel.eventCreateDate = new Date();
                    eventModel.organiserName = UserModel.data.getFullName();
                    eventModel.uid = UserModel.data.getUid();
                    if (eventPrice == -2) {
                        String sEventPrice = eventPriceET.getText().toString();
                        if (sEventPrice.isEmpty()) {
                            Services.showCenterToast(AddEventActivity.this,"Ange pris");
                        }
                        else {
                            eventModel.eventPrice = Integer.parseInt(sEventPrice);
                        }
                    }
                    else {
                        eventModel.eventPrice = eventPrice;
                    }


                    uploadImageOnFirebase(eventModel);


                }


            }
        });
        //TapToChangeImage
        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermissionForReadExtertalStorage()) {
                    ShowFileChooser();
                }
                else {
                    requestStoragePermission();
                }

            }
        });

        if (!checkPermissionForReadExtertalStorage()) {
            requestStoragePermission();
        }
    }


    private void fillInAddress1(Double latitude, Double longitude) {

        LatLng latLng = new LatLng(latitude, longitude);
        eventModel.latitude = latitude;
        eventModel.longitude = longitude;
        eventModel.hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(eventModel.latitude, eventModel.longitude));
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

    public void getSubcategory(String catId){
        ProgressHud.show(this,"");

        FirebaseFirestore.getInstance().collection("Category").document(catId).collection("subcategories").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ProgressHud.dialog.dismiss();
                subCategoryModels.clear();
                subCategoryName.clear();
                subCategoryName.add("Välj underkategori");
                if (task.getResult() != null && !task.getResult().isEmpty()){

                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                         SubCategoryModel subCategoryModel = documentSnapshot.toObject(SubCategoryModel.class);
                        subCategoryModels.add(subCategoryModel);
                        subCategoryName.add(subCategoryModel.getName());
                    }
                }

                if (subCategoryModels.size() > 0) {
                    subCategoryLL.setVisibility(View.VISIBLE);
                    String[] arr = subCategoryName.toArray(new String[subCategoryName.size()]);
                    ArrayAdapter<String> subCategoryAdapter = new ArrayAdapter<String>(AddEventActivity.this,
                            R.layout.myfont,arr) {

                        public View getView(int position, View convertView, ViewGroup parent) {
                            View v = super.getView(position, convertView, parent);

                            Typeface externalFont= ResourcesCompat.getFont(AddEventActivity.this, R.font.montmedium);
                            ((TextView) v).setTypeface(externalFont);

                            return v;
                        }


                        public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                            View v =super.getDropDownView(position, convertView, parent);

                            Typeface externalFont = ResourcesCompat.getFont(AddEventActivity.this, R.font.montmedium);
                            ((TextView) v).setTypeface(externalFont);
                            return v;
                        }
                    };


                    subCategoryAdapter.setDropDownViewResource(
                            android.R.layout
                                    .simple_spinner_dropdown_item);

                    subCategorySpinner.setAdapter(subCategoryAdapter);

                }
                else {
                    subCategoryLL.setVisibility(View.GONE);
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

        eventModel.latitude = Objects.requireNonNull(place.getLatLng()).latitude;
        eventModel.longitude = place.getLatLng().longitude;
        eventModel.hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(eventModel.latitude, eventModel.longitude));
        mapLL.setVisibility(View.VISIBLE);
        isLocationSelected = true;
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
        address.setText(place.getAddress());

        address.requestFocus();


    }


    public void getCategories(){
        ProgressHud.show(AddEventActivity.this,"");
        FirebaseFirestore.getInstance().collection("Category").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ProgressHud.dialog.dismiss();
                if (task.isSuccessful()) {
                    categoryModels.clear();
                    categoryName.clear();
                    categoryName.add("Välj kategori");
                    if (task.getResult() != null && !task.getResult().isEmpty()){
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            CategoryModel categoryModel = documentSnapshot.toObject(CategoryModel.class);
                            categoryModels.add(categoryModel);
                            categoryName.add(categoryModel.getName());
                        }
                    }

                    String[] arr = categoryName.toArray(new String[categoryName.size()]);
                    ArrayAdapter<String> categoryArrayAdapter = new ArrayAdapter<String>(AddEventActivity.this,
                            R.layout.myfont,arr ) {

                        public View getView(int position, View convertView, ViewGroup parent) {
                            View v = super.getView(position, convertView, parent);

                            Typeface externalFont= ResourcesCompat.getFont(AddEventActivity.this, R.font.montmedium);
                            ((TextView) v).setTypeface(externalFont);

                            return v;
                        }


                        public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                            View v =super.getDropDownView(position, convertView, parent);

                            Typeface externalFont = ResourcesCompat.getFont(AddEventActivity.this, R.font.montmedium);
                            ((TextView) v).setTypeface(externalFont);
                            return v;
                        }
                    };


                    categoryArrayAdapter.setDropDownViewResource(
                            android.R.layout
                                    .simple_spinner_dropdown_item);

                    categorySpinner.setAdapter(categoryArrayAdapter);

                }
                else {
                    Services.showDialog(AddEventActivity.this,"ERROR",task.getException().getLocalizedMessage());
                }
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = dayOfMonth;
        myMonth = month + 1;
        myCalendar = Calendar.getInstance();

        myCalendar.set(Calendar.DAY_OF_MONTH, myday);
        myCalendar.set(Calendar.MONTH, month);
        myCalendar.set(Calendar.YEAR,year);
        startDate.setText(String.format("%02d",myday)+"-"+Services.getMonthName(myMonth)+"-"+String.format("%02d",myYear));

    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;

        myCalendar.set(Calendar.HOUR,myHour);
        myCalendar.set(Calendar.MINUTE, myMinute);


        startTime.setText(String.format("%02d",myHour)+" : "+String.format("%02d",myMinute));
    }

    public void requestStoragePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return;
        }


        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);//If the user has denied the permission previously your code will come to this block

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
    }

    public void ShowFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {

            Uri filepath = data.getData();

            CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(filepath, new CropImageOptions());
            cropImageContractOptions.setAspectRatio(10,7);
            cropImageContractOptions.setFixAspectRatio(true);
            cropImageContractOptions.setOutputCompressQuality(60);
            cropImage.launch(cropImageContractOptions);


        }
    }

    public boolean checkPermissionForReadExtertalStorage() {
        int result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    private void uploadImageOnFirebase(EventModel eventModel) {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("EventImages").child(eventModel.getId()).child(eventModel.getId()+ ".png");
        UploadTask uploadTask = storageReference.putFile(resultUri);
        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    ProgressHud.dialog.dismiss();
                    throw Objects.requireNonNull(task.getException());
                }
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()) {

                    eventModel.eventImage = String.valueOf(task.getResult());
                    addEventOnFirebase();

                }

                else {
                    ProgressHud.dialog.dismiss();
                    Services.showDialog(AddEventActivity.this,"ERROR",task.getException().getLocalizedMessage());
                }


            }
        });
    }


    private void addEventOnFirebase(){
        FirebaseFirestore.getInstance().collection("Events").document(eventModel.getId()).set(eventModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ProgressHud.dialog.dismiss();
                if (task.isSuccessful()) {
                    Services.showCenterToast(AddEventActivity.this,"Händelse tillagd");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },2000);
                }
                else {
                    Services.showDialog(AddEventActivity.this,"ERROR",task.getException().getLocalizedMessage());
                }
            }
        });
    }

}
