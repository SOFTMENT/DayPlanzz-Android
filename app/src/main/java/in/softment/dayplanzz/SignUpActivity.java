package in.softment.dayplanzz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

import in.softment.dayplanzz.Model.UserModel;
import in.softment.dayplanzz.Utils.Constants;
import in.softment.dayplanzz.Utils.ProgressHud;
import in.softment.dayplanzz.Utils.Services;

public class SignUpActivity extends AppCompatActivity {

    private String accountType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        EditText name = findViewById(R.id.username);
        EditText emailAddress = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        CheckBox gdprCheckbox = findViewById(R.id.gdprCheckbox);
        LinearLayout areYouLL = findViewById(R.id.areYouLL);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.company) {
                    accountType = "company";
                }
                else {
                    accountType = "association";
                }
            }
        });
        if (Constants.accountType.equalsIgnoreCase("user")) {
            areYouLL.setVisibility(View.GONE);
        }
        else {
            areYouLL.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.signUpbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sName = name.getText().toString().trim();
                String sEmail = emailAddress.getText().toString().trim();
                String sPassword = password.getText().toString().trim();

                if (sName.isEmpty()) {
                    Services.showCenterToast(SignUpActivity.this,"Ange fullständigt namn");
                }
                else if (sEmail.isEmpty()) {
                    Services.showCenterToast(SignUpActivity.this,"Skriv in epostadress");
                }
                else if (sPassword.isEmpty()) {
                    Services.showCenterToast(SignUpActivity.this,"Skriv in lösenord");
                }
                else if (!gdprCheckbox.isChecked()) {
                    Services.showCenterToast(SignUpActivity.this,"Vänligen acceptera integritetspolicyn");
                }
                else {
                    ProgressHud.show(SignUpActivity.this,"Skapar konto...");
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            ProgressHud.dialog.dismiss();
                            if (task.isSuccessful()) {
                                UserModel userModel = new UserModel();
                                userModel.email = sEmail;
                                userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                userModel.fullName = sName;
                                userModel.accountType = accountType;
                                userModel.registredAt = new Date();
                                userModel.userType = Constants.accountType;

                                Services.addUserDataOnServer(SignUpActivity.this,userModel);
                            }
                            else {
                                Services.showDialog(SignUpActivity.this,"FEL","Något gick fel");
                            }
                        }
                    });
                }
            }
        });



    }
}
