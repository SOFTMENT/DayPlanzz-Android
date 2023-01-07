package in.softment.dayplanzz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import in.softment.dayplanzz.EventDetailsActivity;
import in.softment.dayplanzz.Interface.CheckFavoriteListener;
import in.softment.dayplanzz.MainActivity;
import in.softment.dayplanzz.Model.CategoryModel;
import in.softment.dayplanzz.Model.SubCategoryModel;
import in.softment.dayplanzz.R;
import in.softment.dayplanzz.Utils.Services;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private ArrayList<CategoryModel> categoryModels;
    private Context context;
    private ViewHolder mHolder;
    public CategoryAdapter(Context context,ArrayList<CategoryModel> categoryModels){
        this.context = context;
        this.categoryModels = categoryModels;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_layout_view,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        CategoryModel categoryModel = categoryModels.get(position);
        holder.title.setText(categoryModel.getName());
        if (categoryModel.isHasSubcategory()) {
            holder.upDown.setVisibility(View.VISIBLE);
        }
        else {
            holder.upDown.setVisibility(View.GONE);
        }

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.subCategoryModels = new ArrayList<>();
        holder.subcategoryAdapter = new SubcategoryAdapter(context,categoryModel.getId(),holder.subCategoryModels);
        holder.recyclerView.setAdapter(holder.subcategoryAdapter);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            holder.favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                        Services.showCenterToast(context,"Inloggning Krävs");
                        return;
                    }

                    if (holder.favBtn.getTag().toString().equalsIgnoreCase("fav")) {
                        Services.removeCatFavorites(categoryModel.getId());
                        holder.favBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    }
                    else {
                        Services.addCatFavorites(categoryModel.getName(),categoryModel.getId());
                        holder.favBtn.setImageResource(R.drawable.ic_baseline_favorite_red);
                    }

                }
            });

            Services.checkCatFavorites(categoryModel.getId(), new CheckFavoriteListener() {
                @Override
                public void onCallBack(Boolean isFav) {
                    if (isFav) {
                        holder.favBtn.setTag("fav");
                        holder.favBtn.setImageResource(R.drawable.ic_baseline_favorite_red);
                    }
                    else {
                        holder.favBtn.setTag("unfav");
                        holder.favBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    }
                }
            });
        }
        else {
            holder.favBtn.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.subCategoryModels.size() > 0) {
                    if ((boolean) holder.upDown.getTag()) {
                        mHolder = null;
                        holder.upDown.setTag(false);
                        holder.upDown.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                        holder.recyclerView.setVisibility(View.GONE);
                    }
                    else {


                    if (mHolder != null) {
                        mHolder.upDown.setTag(false);
                        mHolder.upDown.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                        mHolder.recyclerView.setVisibility(View.GONE);
                    }

                        mHolder = holder;
                        holder.upDown.setTag(true);
                        holder.upDown.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                        holder.recyclerView.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    ((MainActivity)context).showEvents(categoryModel.getId(),"",categoryModel.getName());
                }
            }
        });
        FirebaseFirestore.getInstance().collection("Category").document(categoryModel.id).collection("subcategories").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    holder.subCategoryModels.clear();
                    if (task.getResult() != null  && !task.getResult().isEmpty()) {
                        holder.upDown.setVisibility(View.VISIBLE);
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            SubCategoryModel subCategoryModel = documentSnapshot.toObject(SubCategoryModel.class);
                            holder.subCategoryModels.add(subCategoryModel);

                        }

                        holder.subcategoryAdapter.notifyDataSetChanged();

                    }
                    else {
                        holder.upDown.setVisibility(View.GONE);
                    }
                }
                else {
                    holder.upDown.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView title;
        ImageView upDown;
        ArrayList<SubCategoryModel> subCategoryModels;
        SubcategoryAdapter subcategoryAdapter;
        ImageView favBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerview);
            recyclerView.setHasFixedSize(true);

            title = itemView.findViewById(R.id.title);
            upDown = itemView.findViewById(R.id.upDown);
            upDown.setTag(false);

            favBtn = itemView.findViewById(R.id.favBtn);


        }
    }
}


