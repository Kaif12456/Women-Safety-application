package com.a.womensafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    private EditText email,pswd;
    private FloatingActionButton login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_sign_in);

        email=findViewById(R.id.email);
        pswd=findViewById(R.id.pswd);
        login=(FloatingActionButton)findViewById(R.id.login);


/*
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(),UserHome.class));
            finish();
        }
*/


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        boolean a = pref.getBoolean("login", false);
        if (a == true) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(email.getText().toString().trim().isEmpty()||pswd.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(SignIn.this, "Please Enter All the details!!!", Toast.LENGTH_SHORT).show();
                }
                else if(FirebaseAuth.getInstance().getCurrentUser() != null)
                {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString().trim()
                            , pswd.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String x[]=email.getText().toString().trim().split("@");
                                if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
                                {
                                    FirebaseDatabase.getInstance().getReference("USERS")
                                            .child(x[0]).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            UserDetails ud=dataSnapshot.getValue(UserDetails.class);
                                            if(ud.getPswd().equalsIgnoreCase(pswd.getText().toString().trim()))
                                            {
                                                Toast.makeText(getApplicationContext(), "Login Succesfull!!!", Toast.LENGTH_SHORT).show();
                                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                                SharedPreferences.Editor editor = pref.edit();
                                                editor.putBoolean("login", true);
                                                editor.commit();
                                                SharedPreferences pref1 = getApplicationContext().getSharedPreferences("Name", 0); // 0 - for private mode
                                                SharedPreferences.Editor editor1 = pref1.edit();
                                                editor1.putString("name",email.getText().toString().trim());
                                                editor1.commit();

                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                finish();
                                            }
                                            else
                                            {
                                                Toast.makeText(SignIn.this, "Incorrect Password!!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Please Verify Your Email!!!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "NOT A REGISTERED USER OR Incoreect credentials!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

                else
                {
                    final String s[]=email.getText().toString().trim().split("@");

                    FirebaseDatabase.getInstance().getReference("USERS").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(s[0]))
                            {
                                UserDetails d=dataSnapshot.child(s[0]).getValue(UserDetails.class);
                                if(d.getPswd().equalsIgnoreCase(pswd.getText().toString().trim()))
                                {
                                    Toast.makeText(SignIn.this, "Login Successfull!!!", Toast.LENGTH_SHORT).show();
                                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putBoolean("login", true);
                                    editor.commit();

                                    SharedPreferences pref1 = getApplicationContext().getSharedPreferences("Name", 0); // 0 - for private mode
                                    SharedPreferences.Editor editor1 = pref1.edit();
                                    editor1.putString("name",email.getText().toString().trim());
                                    editor1.commit();

                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(SignIn.this, "Incorrect password!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(SignIn.this, "Not a registered user!!", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });





                }
            }
        });
    }

    public void OpenSignupPage(View view) {
        startActivity(new Intent(SignIn.this,SignUp.class));
        finish();


    }
}
