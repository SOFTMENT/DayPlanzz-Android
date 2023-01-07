package in.softment.dayplanzz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import in.softment.dayplanzz.Adapter.EventAdapter;
import in.softment.dayplanzz.Interface.EventListeners;
import in.softment.dayplanzz.Model.EventModel;
import in.softment.dayplanzz.Model.FavoriteModel;
import in.softment.dayplanzz.Utils.ProgressHud;
import in.softment.dayplanzz.Utils.Services;

public class BulbActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulb);

        //BACK
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        TextView no_events_available = findViewById(R.id.no_events_available);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<EventModel> eventModels = new ArrayList<>();
        EventAdapter eventAdapter = new EventAdapter(this, eventModels);
        recyclerView.setAdapter(eventAdapter);

        ProgressHud.show(this,"");

        Services.getAllEventForBulb(new EventListeners() {
            @Override
            public void onCallback(ArrayList<EventModel> eModels) {
                ProgressHud.dialog.dismiss();
                eventModels.clear();
                eventModels.addAll(eModels);
                for(EventModel eventModel : eventModels) {
                    for(FavoriteModel favoriteModel : FavoriteModel.favoriteModels) {
                        if (eventModel.getUid().equalsIgnoreCase(favoriteModel.getUid())) {
                            eventModels.add(0,eventModel);
                        }
                    }
                }
                if (eventModels.size() > 0) {
                    no_events_available.setVisibility(View.GONE);

                    eventModels.sort(new Comparator<EventModel>() {
                        @Override
                        public int compare(EventModel eventModel, EventModel t1) {
                            return eventModel.startDateAndTime.compareTo(t1.startDateAndTime);
                        }
                    });
                }
                else {
                    no_events_available.setVisibility(View.VISIBLE);
                }
                eventAdapter.notifyDataSetChanged();

            }
        });
    }
}
