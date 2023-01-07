package in.softment.dayplanzz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;

import in.softment.dayplanzz.Adapter.LocationAdapter;
import in.softment.dayplanzz.Interface.LocationListener;
import in.softment.dayplanzz.Model.LocationModel;
import in.softment.dayplanzz.Model.UserModel;
import in.softment.dayplanzz.Utils.ProgressHud;
import in.softment.dayplanzz.Utils.Services;

public class LocationsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText name;
    LinearLayout addLocationBtn;
    AppCompatButton saveBtn;
    private ArrayList<LocationModel> locationModels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_locations);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        name = findViewById(R.id.username);
        addLocationBtn = findViewById(R.id.addLocation);
        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocationsActivity.this,AddLocationActivity.class));
            }
        });
        saveBtn = findViewById(R.id.save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sName = name.getText().toString();
                if (sName.isEmpty()) {
                    Services.showCenterToast(LocationsActivity.this,"Ange arrangörens namn");
                }
                else {
                    UserModel.data.organiserName = sName;
                    ProgressHud.show(LocationsActivity.this,"");
                    FirebaseFirestore.getInstance().collection("Users").document(UserModel.data.getUid()).set(UserModel.data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            ProgressHud.dialog.dismiss();
                            if (task.isSuccessful()) {
                                Services.showCenterToast(LocationsActivity.this,"Sparad");
                            }
                            else {
                                Services.showDialog(LocationsActivity.this,"ERROR",task.getException().getLocalizedMessage());
                            }
                        }
                    });
                }
            }
        });



        name.setText(UserModel.data.getFullName());

        locationModels = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LocationAdapter locationAdapter = new LocationAdapter(this, locationModels);
        recyclerView.setAdapter(locationAdapter);

        ProgressHud.show(this,"");
        Services.getAllLocations(UserModel.data.getUid(), new LocationListener() {
            @Override
            public void onCallback(ArrayList<LocationModel> locModels) {
                ProgressHud.dialog.dismiss();
                locationModels.clear();
                locationModels.addAll(locModels);
                locationAdapter.notifyDataSetChanged();
            }
        });
    }


}
