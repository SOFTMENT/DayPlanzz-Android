package in.softment.dayplanzz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import in.softment.dayplanzz.EventDetailsActivity;
import in.softment.dayplanzz.Model.EventModel;
import in.softment.dayplanzz.R;
import in.softment.dayplanzz.Utils.Services;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private Context context;
    private ArrayList<EventModel> eventModels;
    public EventAdapter(Context context, ArrayList<EventModel> eventModels) {
        this.context = context;
        this.eventModels = eventModels;
    }
    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_layout_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        EventModel eventModel = eventModels.get(position);
        holder.eventName.setText(eventModel.getTitle());
        if (!eventModel.eventImage.isEmpty()){
            Glide.with(context).load(eventModel.getEventImage()).placeholder(R.drawable.placeholder).into(holder.imageView);
        }
        holder.eventDescription.setText(eventModel.getDescription());
         holder.eventDate.setText(Services.convertDateToEventDate(eventModel.getStartDateAndTime()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("eventModel",eventModel);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return eventModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventDescription;
        RoundedImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventDescription = itemView.findViewById(R.id.eventDescription);
            imageView = itemView.findViewById(R.id.imageView);

        }
    }
}
