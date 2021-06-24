package com.opsc19003852.monufind;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText mUsername, mPassword;
    Button mLoginbtn;
    FirebaseAuth fAuth;
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPassword=findViewById(R.id.edtPassword);
        mUsername=findViewById(R.id.edtUsername);
        mPassword=findViewById(R.id.edtPassword);
        mLoginbtn=findViewById(R.id.btnLogin);
        fAuth=FirebaseAuth.getInstance();
         register= findViewById(R.id.lnkRegister);

        mLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username =mUsername.getText().toString().trim();
                String password=mPassword.getText().toString().trim();
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

                //Authenticate USer
                fAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Logged in successfully, UserID: " + password, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                            //intent.putExtra("UserID", String.valueOf(password));
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Error!!!"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

        });
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

    }

}