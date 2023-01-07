package in.softment.dayplanzz;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import in.softment.dayplanzz.Fragment.AccountFragment;
import in.softment.dayplanzz.Fragment.HelpFragment;
import in.softment.dayplanzz.Fragment.HomeFragment;
import in.softment.dayplanzz.Model.FavoriteModel;
import in.softment.dayplanzz.Utils.Constants;
import in.softment.dayplanzz.Utils.NonSwipeAbleViewPager;
import in.softment.dayplanzz.Utils.ProgressHud;
import in.softment.dayplanzz.Utils.Services;

public class MainActivity extends AppCompatActivity  {
    private String apiKey = "AIzaSyAqGP3Jpq1h2_N5z5jAQbt45UDtPmYH2oU";
    private TabLayout tabLayout;
    private NonSwipeAbleViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private SharedPreferences sharedPreferences;


    private final int[] tabIcons = {
            R.drawable.ic_baseline_help_24,
            R.drawable.ic_round_home_24,
            R.drawable.ic_round_person_24

    };
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getUserLatitudeAndLongitude();
                }
                else {

                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Places.isInitialized()) {
            Places.initialize(this,apiKey);
        }


        sharedPreferences = getSharedPreferences("MyDB",MODE_PRIVATE);


        //ViewPager
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(5);


        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 2) {
                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {

                        BottomSheetDialog sheetDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetStyle);
                        sheetDialog.setCancelable(false);
                        View view2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.login_options_view,(LinearLayout)MainActivity.this.findViewById(R.id.sheet));
                        view2.findViewById(R.id.userLogin).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sheetDialog.dismiss();
                                Constants.accountType = "user";
                            }
                        });
                        view2.findViewById(R.id.organizerLogin).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sheetDialog.dismiss();
                                Constants.accountType = "organizer";
                            }
                        });
                        sheetDialog.setContentView(view2);
                        sheetDialog.show();

                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setupTabIcons();

        viewPager.setCurrentItem(1);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            getAllFavorites();
        }


        checkLocationPermission();

    }

    public void getAllFavorites(){
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Favorites").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null && !task.getResult().isEmpty()) {
                        FavoriteModel.favoriteModels.clear();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            FavoriteModel favoriteModel = documentSnapshot.toObject(FavoriteModel.class);
                            FavoriteModel.favoriteModels.add(favoriteModel);
                        }
                    }
                }
            }
        });
    }

    public void getUserLatitudeAndLongitude(){


        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (!enabled) {
            allowGPS();
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                @NonNull
                @Override
                public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                    return null;
                }

                @Override
                public boolean isCancellationRequested() {
                    return false;
                }
            }).addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Constants.latitude = task.getResult().getLatitude();
                        Constants.longitude = task.getResult().getLongitude();

                    }

                }
            });
        }
    }
    public void allowGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
        builder.setTitle("GPS");
        builder.setMessage("Please enable gps service.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setCancelable(false);
        builder.show();

    }

    public void checkLocationPermission(){

        if (ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            getUserLatitudeAndLongitude();

        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {

            explainUser();
        }
        else{
            if (sharedPreferences.getBoolean("isFirstTime",true)) {
                sharedPreferences.edit().putBoolean("isFirstTime",false).apply();
                requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            else {
                allowFromSettings();
            }

        }
    }
    public void allowFromSettings(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
        builder.setTitle("Location Permission");
        builder.setMessage("Please allow location permission from app settings.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 10);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public void explainUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
        builder.setTitle("Location Permission");
        builder.setMessage("We need your location permission to find near by organizations.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void setupTabIcons() {

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);



    }

    private void setupViewPager(ViewPager viewPager) {

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFrag(new HelpFragment());
        viewPagerAdapter.addFrag(new HomeFragment());
        viewPagerAdapter.addFrag(new AccountFragment());

        viewPager.setAdapter(viewPagerAdapter);

    }

    static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {


            return mFragmentList.get(position);
        }

        @Override
        public int getItemPosition(@NonNull @NotNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {

            return mFragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return  "Hjälp";
            }
            else if (position == 1) {
                return  "Startsida";
            }
            else if (position == 2) {
                return  "Konto";
            }
            return "";
        }

        public void addFrag(Fragment fragment) {
            mFragmentList.add(fragment);

        }



    }


    public void showEvents(String catId, String subCatId, String title) {
        Intent intent = new Intent(this, EventsActivity.class);
        intent.putExtra("catId",catId);
        intent.putExtra("subCatId",subCatId);
        intent.putExtra("title",title);
        startActivity(intent);
    }



}


