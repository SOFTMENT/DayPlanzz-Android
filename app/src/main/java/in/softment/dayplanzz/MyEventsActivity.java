package in.softment.dayplanzz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import in.softment.dayplanzz.Adapter.MyEventAdapter;
import in.softment.dayplanzz.Model.EventModel;
import in.softment.dayplanzz.Utils.ProgressHud;

public class MyEventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView no_event_available = findViewById(R.id.no_events_available);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        ArrayList<EventModel> eventModels = new ArrayList<>();
        MyEventAdapter myEventAdapter = new MyEventAdapter(this,eventModels);
        recyclerView.setAdapter(myEventAdapter);

        ProgressHud.show(this,"");
        FirebaseFirestore.getInstance().collection("Events").whereEqualTo("uid", FirebaseAuth.getInstance().getUid()).orderBy("eventCreateDate", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                ProgressHud.dialog.dismiss();
                if (error == null) {
                    eventModels.clear();
                    if (value != null && !value.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            EventModel eventModel = documentSnapshot.toObject(EventModel.class);
                            eventModels.add(eventModel);
                        }
                    }
                    if (eventModels.size() > 0) {
                        no_event_available.setVisibility(View.GONE);
                    }
                    else {
                        no_event_available.setVisibility(View.VISIBLE);
                    }
                    myEventAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
