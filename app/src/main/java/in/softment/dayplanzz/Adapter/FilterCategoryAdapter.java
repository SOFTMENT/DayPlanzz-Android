package in.softment.dayplanzz.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.softment.dayplanzz.Fragment.HomeFragment;
import in.softment.dayplanzz.Model.CategoryModel;
import in.softment.dayplanzz.R;

public class FilterCategoryAdapter extends RecyclerView.Adapter<FilterCategoryAdapter.ViewHolder> {

    public ArrayList<CategoryModel> categoryModels;
    public HomeFragment homeFragment;

    public FilterCategoryAdapter(HomeFragment homeFragment, ArrayList<CategoryModel> categoryModels){
        this.categoryModels = categoryModels;
        this.homeFragment = homeFragment;
    }
    @NonNull
    @Override
    public FilterCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_category_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilterCategoryAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        CategoryModel categoryModel = categoryModels.get(position);
        holder.title.setText(categoryModel.getName());
        holder.checkBox.setChecked(categoryModel.isEnabled());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                homeFragment.categoryModels.get(holder.getAdapterPosition()).setEnabled(isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            checkBox = itemView.findViewById(R.id.check);
        }
    }
}
