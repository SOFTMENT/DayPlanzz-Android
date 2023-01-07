package in.softment.dayplanzz;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import in.softment.dayplanzz.Adapter.FavoriteOrganiserAdapter;
import in.softment.dayplanzz.Interface.AllFavOrganiserListener;
import in.softment.dayplanzz.Model.FavoriteModel;
import in.softment.dayplanzz.Utils.ProgressHud;
import in.softment.dayplanzz.Utils.Services;

public class FavCategoriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_categories);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView no_fav_organiser = findViewById(R.id.no_fav_categories);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<FavoriteModel> favoriteModels = new ArrayList<>();
        FavoriteOrganiserAdapter favoriteOrganiserAdapter = new FavoriteOrganiserAdapter(this, favoriteModels);
        recyclerView.setAdapter(favoriteOrganiserAdapter);

        ProgressHud.show(this,"");
        Services.getAllCatFavOrganiser(new AllFavOrganiserListener() {
            @Override
            public void onCallBack(ArrayList<FavoriteModel> favModels) {
                ProgressHud.dialog.dismiss();
                if (favModels != null) {
                    favoriteModels.clear();
                    favoriteModels.addAll(favModels);
                    if (favoriteModels.size() > 0) {
                        no_fav_organiser.setVisibility(View.GONE);
                    }
                    else {
                        no_fav_organiser.setVisibility(View.VISIBLE);
                    }

                }
                else {
                    favoriteModels.clear();
                }
                favoriteOrganiserAdapter.notifyDataSetChanged();
            }
        });
    }
}
