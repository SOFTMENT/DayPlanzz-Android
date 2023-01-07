package in.softment.dayplanzz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.softment.dayplanzz.MainActivity;
import in.softment.dayplanzz.Model.SubCategoryModel;
import in.softment.dayplanzz.R;

public class SubcategoryAdapter extends RecyclerView.Adapter<SubcategoryAdapter.ViewHolder> {
    private ArrayList<SubCategoryModel> subCategoryModels;
    private Context context;
    private String catId;
    public SubcategoryAdapter(Context context, String catId,ArrayList<SubCategoryModel> subCategoryModels) {
        this.context = context;
        this.subCategoryModels = subCategoryModels;
        this.catId = catId;
    }

    @NonNull
    @Override
    public SubcategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategories_layout_view,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubcategoryAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        SubCategoryModel subCategoryModel = subCategoryModels.get(position);
        holder.title.setText(subCategoryModel.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)context).showEvents(subCategoryModel.getId(),subCategoryModel.getId(), subCategoryModel.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return subCategoryModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }
}
