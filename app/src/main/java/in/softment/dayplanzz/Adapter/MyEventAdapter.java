package in.softment.dayplanzz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import in.softment.dayplanzz.Model.EventModel;
import in.softment.dayplanzz.R;
import in.softment.dayplanzz.Utils.ProgressHud;

public class MyEventAdapter extends RecyclerView.Adapter<MyEventAdapter.ViewHolder> {
    private Context context;
    private ArrayList<EventModel> eventModels;

    public MyEventAdapter(Context context, ArrayList<EventModel> eventModels){
        this.context = context;
        this.eventModels = eventModels;
    }

    @NonNull
    @Override
    public MyEventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_event_layout_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyEventAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        EventModel eventModel = eventModels.get(position);
        holder.title.setText(eventModel.getTitle());
        holder.subcategoryName.setText(eventModel.getCategoryName());

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressHud.show(context,"");
                FirebaseFirestore.getInstance().collection("Events").document(eventModel.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ProgressHud.dialog.dismiss();

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, subcategoryName;
        private ImageView deleteBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subcategoryName = itemView.findViewById(R.id.subcategoryName);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
