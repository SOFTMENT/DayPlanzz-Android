package in.softment.dayplanzz.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;

import in.softment.dayplanzz.Adapter.CategoryAdapter;
import in.softment.dayplanzz.Adapter.FilterCategoryAdapter;
import in.softment.dayplanzz.BulbActivity;
import in.softment.dayplanzz.IntroActivity;
import in.softment.dayplanzz.Model.CategoryModel;
import in.softment.dayplanzz.R;
import in.softment.dayplanzz.Utils.Constants;
import in.softment.dayplanzz.Utils.ProgressHud;
import in.softment.dayplanzz.Utils.Services;

public class HomeFragment extends Fragment {

    private TextView distance, age, favorites, amounts;
    public  TextView category;
    private RecyclerView recyclerView;
    public ArrayList<CategoryModel> categoryModels;
    private ArrayList<CategoryModel> categoryModels2;
    private CategoryAdapter categoryAdapter;
    private LinearLayout no_categories_available;
    private FilterCategoryAdapter filterCategoryAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        distance = view.findViewById(R.id.distance);
        age = view.findViewById(R.id.age);
        category = view.findViewById(R.id.category);
        amounts = view.findViewById(R.id.amounts);
        favorites = view.findViewById(R.id.favorites);

        no_categories_available = view.findViewById(R.id.no_cateogries_available);
        view.findViewById(R.id.intro).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), IntroActivity.class));
            }
        });

        view.findViewById(R.id.filterByBulb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), BulbActivity.class));
            }
        });

        view.findViewById(R.id.filterByLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog sheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetStyle);


                View view2 = LayoutInflater.from(getContext()).inflate(R.layout.distance_filter_layout,(LinearLayout)view.findViewById(R.id.sheet));
                RadioButton checkBox10 = view2.findViewById(R.id.checkBox10KM);
                checkBox10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        distance.setText("10");
                        Constants.distance = 10;

                    }
                });
                RadioButton checkBox25 = view2.findViewById(R.id.checkBox25KM);
                checkBox25.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        distance.setText("25");
                        Constants.distance = 25;
                    }
                });

                RadioButton checkBox50 = view2.findViewById(R.id.checkBox50KM);
                checkBox50.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        distance.setText("50");
                        Constants.distance = 50;
                    }
                });

                RadioButton checkBox100 = view2.findViewById(R.id.checkBox100KM);
                checkBox100.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        distance.setText("100");
                        Constants.distance = 100;
                    }
                });


                if (Constants.distance == 10) {
                    checkBox10.setChecked(true);
                }
                else if (Constants.distance == 25) {
                    checkBox25.setChecked(true);
                }
                else if (Constants.distance == 50) {
                    checkBox50.setChecked(true);
                }
                else if (Constants.distance == 100) {
                    checkBox100.setChecked(true);
                }
                sheetDialog.setContentView(view2);
                sheetDialog.show();
            }
        });

        view.findViewById(R.id.filterByAge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog sheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetStyle);

                View view2 = LayoutInflater.from(getContext()).inflate(R.layout.age_filter_layout,(LinearLayout)view.findViewById(R.id.sheet));

                CheckBox allCheck = view2.findViewById(R.id.allagesCheck);

                CheckBox barnCheck = view2.findViewById(R.id.barnCheck);
                CheckBox vuxenCheck = view2.findViewById(R.id.vuxenCheck);
                CheckBox pensionerCheck = view2.findViewById(R.id.pensionerCheck);
                CheckBox ungdomCheck = view2.findViewById(R.id.ungdomCheck);

                barnCheck.setChecked(Constants.ages.contains("barn"));
                vuxenCheck.setChecked(Constants.ages.contains("vuxen"));
                pensionerCheck.setChecked(Constants.ages.contains("pensioner"));
                ungdomCheck.setChecked(Constants.ages.contains("ungdom"));

                if (Constants.ages.size() == 0) {
                    barnCheck.setChecked(true);
                    vuxenCheck.setChecked(true);
                    pensionerCheck.setChecked(true);
                    ungdomCheck.setChecked(true);
                }

                allCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            barnCheck.setChecked(true);
                            vuxenCheck.setChecked(true);
                            pensionerCheck.setChecked(true);
                            ungdomCheck.setChecked(true);
                        }
                    }
                });

                view2.findViewById(R.id.filterBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Constants.ages.clear();
                        int i = 0;

                        if (barnCheck.isChecked()) {
                            i++;
                            Constants.ages.add("barn");
                        }
                        if (vuxenCheck.isChecked()) {
                            i++;
                            Constants.ages.add("vuxen");
                        }
                        if (pensionerCheck.isChecked()) {
                            i++;
                            Constants.ages.add("pensioner");
                        }
                        if (ungdomCheck.isChecked()) {
                            i++;
                            Constants.ages.add("ungdom");
                        }
                        age.setText(i+"");
                        sheetDialog.dismiss();
                    }
                });



                sheetDialog.setContentView(view2);
                sheetDialog.show();
            }
        });


        view.findViewById(R.id.filterByPayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog sheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetStyle);

                View view2 = LayoutInflater.from(getContext()).inflate(R.layout.payment_filter_layout,(LinearLayout)view.findViewById(R.id.sheet));

                CheckBox allCheck = view2.findViewById(R.id.allCheck);
                allCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        sheetDialog.dismiss();
                        Constants.payment = -1;
                        amounts.setText("Välj alla");

                    }
                });
                CheckBox free = view2.findViewById(R.id.freeCheck);
                free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        sheetDialog.dismiss();
                        Constants.payment = 0;
                        amounts.setText("Gratis");
                    }
                });
                CheckBox oneTo50Check = view2.findViewById(R.id.oneto50Check);
                oneTo50Check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        sheetDialog.dismiss();
                        Constants.payment = 50;
                        amounts.setText("1-50 kr");
                    }
                });
                CheckBox fiftyTo100Check = view2.findViewById(R.id.fiftyTo100Check);
                fiftyTo100Check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        sheetDialog.dismiss();
                        Constants.payment = 100;
                        amounts.setText("50-100 kr");
                    }
                });
                CheckBox hundredTo200Check = view2.findViewById(R.id.hundredTo200Check);
                hundredTo200Check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        sheetDialog.dismiss();
                        Constants.payment = 200;
                        amounts.setText("100-200 kr");
                    }
                });
                CheckBox twoHundredPlusCheck = view2.findViewById(R.id.twohundredPlusCheck);
                twoHundredPlusCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        sheetDialog.dismiss();
                        Constants.payment = 201;
                        amounts.setText("200+ kr");
                    }
                });

                sheetDialog.setContentView(view2);
                sheetDialog.show();
            }
        });


        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        categoryModels = new ArrayList<>();
        categoryModels2 = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryModels2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(categoryAdapter);

        filterCategoryAdapter = new FilterCategoryAdapter(this, categoryModels);
        view.findViewById(R.id.filterByCategory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog sheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetStyle);

                View view2 = LayoutInflater.from(getContext()).inflate(R.layout.categories_filter,(LinearLayout)view.findViewById(R.id.sheet));
                RecyclerView recyclerView = view2.findViewById(R.id.recyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(filterCategoryAdapter);
                view2.findViewById(R.id.filter).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        sheetDialog.dismiss();
                        categoryModels2.clear();
                        for (CategoryModel categoryModel : categoryModels) {
                            if (categoryModel.isEnabled()) {
                                categoryModels2.add(categoryModel);
                            }
                        }
                        category.setText(categoryModels2.size()+"");
                        categoryAdapter.notifyDataSetChanged();
                    }
                });
                sheetDialog.setContentView(view2);
                sheetDialog.show();

            }
        });

        view.findViewById(R.id.filterByFavorites).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //GETCATEGORY
        getCategories();
        return view;
    }




    public void getCategories(){
        ProgressHud.show(getContext(),"");
        FirebaseFirestore.getInstance().collection("Category").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ProgressHud.dialog.dismiss();
                if (task.isSuccessful()) {
                    categoryModels.clear();
                    if (task.getResult() != null && !task.getResult().isEmpty()){
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            CategoryModel categoryModel = documentSnapshot.toObject(CategoryModel.class);
                            categoryModels.add(categoryModel);
                        }
                    }
                    if (categoryModels.size() > 0) {
                        no_categories_available.setVisibility(View.GONE);
                    }
                    else {
                        no_categories_available.setVisibility(View.VISIBLE);
                    }

                    categoryModels2.clear();
                    categoryModels2.addAll(categoryModels);
                    category.setText(categoryModels2.size()+"");
                    categoryAdapter.notifyDataSetChanged();
                    filterCategoryAdapter.notifyDataSetChanged();
                }
                else {
                    Services.showDialog(getContext(),"FEL","Något gick fel");

                }
            }
        });

    }
}
