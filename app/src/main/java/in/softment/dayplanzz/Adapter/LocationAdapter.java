package in.softment.dayplanzz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.softment.dayplanzz.Model.LocationModel;
import in.softment.dayplanzz.R;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private Context context;
    private final ArrayList<LocationModel> locationModels;
    public LocationAdapter(Context context, ArrayList<LocationModel> locationModels){
        this.context = context;
        this.locationModels = locationModels;

    }
    @NonNull
    @Override
    public LocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.location_layout_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull LocationAdapter.ViewHolder holder, int position) {
        LocationModel locationModel = locationModels.get(position);
        holder.locationName.setText(locationModel.locationName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return locationModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView locationName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.locationName);
        }
    }
}
