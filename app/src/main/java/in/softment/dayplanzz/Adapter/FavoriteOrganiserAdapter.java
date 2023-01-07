package in.softment.dayplanzz.Adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.softment.dayplanzz.Model.FavoriteModel;
import in.softment.dayplanzz.R;
import in.softment.dayplanzz.Utils.Services;

public class FavoriteOrganiserAdapter extends RecyclerView.Adapter<FavoriteOrganiserAdapter.ViewHolder> {

    Context context;
    ArrayList<FavoriteModel> favoriteModels;

    public FavoriteOrganiserAdapter(Context context, ArrayList<FavoriteModel> favoriteModels) {
        this.context = context;
        this.favoriteModels = favoriteModels;
    }
    @NonNull
    @Override
    public FavoriteOrganiserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_organiser_layout_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteOrganiserAdapter.ViewHolder holder, int position) {

        FavoriteModel favoriteModel = favoriteModels.get(position);
        holder.title.setText(favoriteModel.getOrganiserName());
        holder.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Services.removeFavorites(favoriteModel.getUid());
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView favBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            favBtn = itemView.findViewById(R.id.favBtn);
        }
    }
}
