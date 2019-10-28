package com.example.logierassig1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;


    private EditText e1Name, e2Email, e3Password, e4Phone,e5Profession,e6Weight;
    private ProgressBar progressBar;
    private Button btnlogin;
    private RadioGroup editTextGender;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        e1Name = findViewById(R.id.etNamereg);
        e2Email = findViewById(R.id.etemail);
        e3Password = findViewById(R.id.etpassword);
        e4Phone = findViewById(R.id.etContactNum);
        e5Profession=findViewById(R.id.etProfession);
        e6Weight=findViewById(R.id.etweight);

        editTextGender=findViewById(R.id.Rgender);

        progressBar = findViewById(R.id.Progress);
        progressBar.setVisibility(View.GONE);
        btnlogin=findViewById(R.id.login);


        mAuth = FirebaseAuth.getInstance();






        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.register).setOnClickListener(this);



    }
    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //handle the already login user
        }
    }
    private void registerUser() {
        final String name = e1Name.getText().toString().trim();
        final String email = e2Email.getText().toString().trim();
        final String password = e3Password.getText().toString().trim();
        final String phone = e4Phone.getText().toString().trim();
        final String profession=e5Profession.getText().toString();
        final String weight=e6Weight.getText().toString();
        final String gender= ((RadioButton)findViewById(editTextGender.getCheckedRadioButtonId()))
                .getText().toString();

        if (name.isEmpty()) {
            e1Name.setError(getString(R.string.input_error_name));
            e1Name.requestFocus();
            return;
        }

        else if (email.isEmpty()) {
            e2Email.setError(getString(R.string.input_error_email));
            e2Email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            e2Email.setError(getString(R.string.input_error_email_invalid));
            e2Email.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            e3Password.setError(getString(R.string.input_error_password));
            e3Password.requestFocus();
            return;
        }

        if (password.length() < 6) {
            e3Password.setError(getString(R.string.input_error_password_length));
            e3Password.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            e4Phone.setError(getString(R.string.input_error_phone));
            e4Phone.requestFocus();
            return;
        }

        if (phone.length() != 10) {
            e4Phone.setError(getString(R.string.input_error_phone_invalid));
            e4Phone.requestFocus();
            return;
        }

        if (profession.isEmpty()) {
            e5Profession.setError(getString(R.string.input_error_profession));
            e5Profession.requestFocus();
            return;
        }

        if (weight.isEmpty()) {
            e6Weight.setError(getString(R.string.input_error_weight));
            e6Weight.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            User user = new User(
                                    name,
                                    email,
                                    phone,
                                    password,
                                    gender,
                                    profession,
                                    weight
                                    );

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                    } else {
                                        //display a failure message
                                        Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                registerUser();
                break;
        }
    }




}
