package in.softment.dayplanzz.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.RectKt;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import in.softment.dayplanzz.AddEventActivity;
import in.softment.dayplanzz.FavCategoriesActivity;
import in.softment.dayplanzz.LocationsActivity;
import in.softment.dayplanzz.EditOrganisersAccountActivity;
import in.softment.dayplanzz.FavOrganiserActivity;
import in.softment.dayplanzz.ManageAccountActivity;
import in.softment.dayplanzz.Model.UserModel;
import in.softment.dayplanzz.MyEventsActivity;
import in.softment.dayplanzz.R;
import in.softment.dayplanzz.SignUpActivity;
import in.softment.dayplanzz.Utils.ProgressHud;
import in.softment.dayplanzz.Utils.Services;


public class AccountFragment extends Fragment {



    LinearLayout signinupLL, userAccountLL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        signinupLL = view.findViewById(R.id.signinupLL);
        userAccountLL = view.findViewById(R.id.userAccountLL);

        if (FirebaseAuth.getInstance().getCurrentUser() == null || UserModel.data.fullName.isEmpty() || UserModel.data.email.isEmpty()) {
            signinupLL.setVisibility(View.VISIBLE);
            userAccountLL.setVisibility(View.GONE);
            EditText emailAddress = view.findViewById(R.id.email);
            EditText password = view.findViewById(R.id.password);

            view.findViewById(R.id.signUp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), SignUpActivity.class));
                }
            });

            view.findViewById(R.id.resetPassword).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sEmail = emailAddress.getText().toString().trim();
                    if (sEmail.isEmpty()) {
                        Services.showCenterToast(getContext(),"Skriv in epostadress");
                    }
                    else {
                        ProgressHud.show(getContext(),"");
                        FirebaseAuth.getInstance().sendPasswordResetEmail(sEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                     ProgressHud.dialog.dismiss();
                                     if (task.isSuccessful()) {
                                         Services.showDialog(getContext(),"Återställ lösenord","Vi har skickat en länk för återställning av lösenord till din e-postadress");
                                     }
                                     else {
                                         Services.showDialog(getContext(),"FEL","Något gick fel");
                                     }
                            }
                        });
                    }
                }
            });



            view.findViewById(R.id.signinbtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String sEmail = emailAddress.getText().toString().trim();
                    String sPassword = password.getText().toString().trim();

                    if (sEmail.isEmpty()) {
                        Services.showCenterToast(getContext(),"Skriv in epostadress");
                    }
                    else if (sPassword.isEmpty()) {
                        Services.showCenterToast(getContext(),"Skriv in lösenord");
                    }
                    else {
                        ProgressHud.show(getContext(),"Logga in...");
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                ProgressHud.dialog.dismiss();
                                if (task.isSuccessful()) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null) {

                                        Services.getCurrentUserData(getContext(),user.getUid(),true,true);

                                    }
                                }
                                else {
                                    Services.showDialog(getContext(),"FEL","Ogiltigt ID och lösenord");
                                }
                            }
                        });
                    }
                }
            });
        }
        else {

            LinearLayout minaAndAddEvent = view.findViewById(R.id.minaAndAddEventLL);
            RelativeLayout editAccountUser = view.findViewById(R.id.editProfileUser);
            RelativeLayout editAccountOrganiser = view.findViewById(R.id.editProfileOrganiser);
            RelativeLayout addLocations = view.findViewById(R.id.addLocations);
            RelativeLayout favCat = view.findViewById(R.id.favCat);
            if (UserModel.data.getUserType().equalsIgnoreCase("user")) {
                minaAndAddEvent.setVisibility(View.GONE);
                editAccountUser.setVisibility(View.VISIBLE);
                editAccountOrganiser.setVisibility(View.GONE);
                addLocations.setVisibility(View.GONE);
                favCat.setVisibility(View.VISIBLE);

            }
            else {
                if (UserModel.data.getAccountType().equalsIgnoreCase("company")) {
                    addLocations.setVisibility(View.GONE);
                }
                else {
                    addLocations.setVisibility(View.VISIBLE);
                }
                editAccountOrganiser.setVisibility(View.VISIBLE);
                minaAndAddEvent.setVisibility(View.VISIBLE);
                editAccountUser.setVisibility(View.GONE);
                favCat.setVisibility(View.GONE);


                view.findViewById(R.id.addEvent).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UserModel.data.isHasApproval()) {
                            startActivity(new Intent(getContext(), AddEventActivity.class));
                        }
                        else {
                            Services.showDialog(getContext(),"Under granskning","Ditt konto är under granskning.");
                        }

                    }
                });
            }

            view.findViewById(R.id.editProfileOrganiser).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), EditOrganisersAccountActivity.class));
                }
            });

            view.findViewById(R.id.editProfileUser).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), ManageAccountActivity.class));
                }
            });

            view.findViewById(R.id.favCat).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), FavCategoriesActivity.class));
                }
            });

            signinupLL.setVisibility(View.GONE);
            userAccountLL.setVisibility(View.VISIBLE);
            TextView name = view.findViewById(R.id.name);
            TextView email = view.findViewById(R.id.emailID);

            name.setText(UserModel.data.getFullName());
            email.setText(UserModel.data.getEmail());

            view.findViewById(R.id.myEvent).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (UserModel.data.isHasApproval()) {
                        startActivity(new Intent(getContext(), MyEventsActivity.class));
                    }
                    else {
                        Services.showDialog(getContext(),"Under granskning","Ditt konto är under granskning.");
                    }

                }
            });

            view.findViewById(R.id.favOrganiser).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), FavOrganiserActivity.class));
                }
            });


            addLocations.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), LocationsActivity.class));
                }
            });

            view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
                    builder.setCancelable(true);
                    builder.setTitle("Logga ut");
                    builder.setMessage("Är du säker på att du vill logga ut?");
                    builder.setNegativeButton("Logga ut", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Services.logout(getContext());
                        }
                    });

                    builder.setNeutralButton("Avbryt", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                        }
                    });

                    builder.show();
                }
            });


        }






        return view;
    }
}
