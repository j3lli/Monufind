package com.opsc19003852.monufind;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    public static final String TAG = "Tag";
    EditText mFullname, mPassword, mUsername;
       RadioGroup mUnits;
        Spinner mLandmark;

    Button RegisterBtn;
    FirebaseAuth fAuth;
    final FirebaseDatabase fbase = FirebaseDatabase.getInstance();
    DatabaseReference ref = fbase.getReference();
    DatabaseReference reff;
    String userID;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mFullname=findViewById(R.id.edtFullName);
        mPassword=findViewById(R.id.edtPassword);
        mUsername=findViewById(R.id.edtUsername);
        mUnits=findViewById(R.id.rgUnits);
        mLandmark=findViewById(R.id.spnLandmark);
        RegisterBtn=findViewById(R.id.btnLogin);
        fAuth=FirebaseAuth.getInstance();
        TextView login = (TextView)findViewById(R.id.lnkLogin);
        login.setMovementMethod(LinkMovementMethod.getInstance());
        User user =new User();
        reff= FirebaseDatabase.getInstance().getReference().child("User");
       // if(fAuth.getCurrentUser()!=null){
         //   startActivity(new Intent(getApplicationContext(), HomeActivity.class));
       //     finish();
      //  }

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String fullName = mFullname.getText().toString();
                String units = "Kms";

                if (mUnits.getCheckedRadioButtonId() == 0)
                {
                     units   = "Miles";
                }
                else
                {
                     units   = "Kms";
                }
                String landmark=mLandmark.getSelectedItem().toString();


                if (TextUtils.isEmpty(username)) {
                    mUsername.setError("Username is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    return;
                }
                if (password.length() < 6) {
                    mPassword.setError("Password Length must be greater than 6");
                    return;
                }


                String finalUnits = units;
                fAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "User Created", Toast.LENGTH_SHORT).show();

                            userID = fAuth.getCurrentUser().getUid();

                            user.writeNewUser(userID,fullName,username,finalUnits,landmark);


                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                        else{
                            Toast.makeText(RegistrationActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }

        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }
}