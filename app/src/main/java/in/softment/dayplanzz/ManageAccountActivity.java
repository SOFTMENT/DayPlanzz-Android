package in.softment.dayplanzz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import in.softment.dayplanzz.Model.UserModel;
import in.softment.dayplanzz.Utils.ProgressHud;
import in.softment.dayplanzz.Utils.Services;

public class ManageAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView name = findViewById(R.id.name);
        TextView email = findViewById(R.id.emailID);

        name.setText(UserModel.data.getFullName());
        email.setText(UserModel.data.getEmail());

        EditText currentPassword = findViewById(R.id.currentPassword);
        EditText newPassword = findViewById(R.id.newPassword);
        EditText newAgainPassword = findViewById(R.id.newAgainPassword);

        findViewById(R.id.changePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sCurrentPassword = currentPassword.getText().toString();
                String sNewPassword = newPassword.getText().toString();
                String sAgainNewPassword = newAgainPassword.getText().toString();

                if (sCurrentPassword.isEmpty()) {
                    Services.showCenterToast(ManageAccountActivity.this,"Ange aktuellt lösenord");
                }
                else if (sNewPassword.isEmpty()) {
                    Services.showCenterToast(ManageAccountActivity.this,"Ange nytt lösenord");
                }
                else if (sAgainNewPassword.isEmpty()) {
                    Services.showCenterToast(ManageAccountActivity.this,"Ange Bekräfta nytt lösenord");
                }
                else if (!sAgainNewPassword.equals(sNewPassword)) {
                    Services.showCenterToast(ManageAccountActivity.this,"Nytt och Bekräfta nytt lösenord måste vara samma");
                }
                else {
                    ProgressHud.show(ManageAccountActivity.this,"");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(UserModel.data.getEmail(), sCurrentPassword);

                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            user.updatePassword(sNewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        currentPassword.setText("");
                                                        newPassword.setText("");
                                                        newAgainPassword.setText("");
                                                        Services.showCenterToast(ManageAccountActivity.this,"Lösenord uppdaterat");
                                                    } else {
                                                        Services.showCenterToast(ManageAccountActivity.this,"Fel Lösenordet har inte uppdaterats");
                                                    }
                                                }
                                            });
                                        } else {
                                            Services.showCenterToast(ManageAccountActivity.this,"Auth-fel");
                                        }
                                    }
                                });
                    }

                }
            }
        });

    }
}
