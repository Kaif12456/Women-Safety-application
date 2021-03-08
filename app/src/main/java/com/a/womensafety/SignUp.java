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
import android.widget.TextView;
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

public class SignUp extends AppCompatActivity {
    private EditText name,email,pswd;
    private TextView signin;
    private FloatingActionButton signup;
    private UserDetails ud;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_sign_up);

        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        pswd=findViewById(R.id.pswd);
        signin=findViewById(R.id.signin);
        signup=findViewById(R.id.signup);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().trim().isEmpty() || email.getText().toString().trim().isEmpty()
                        || pswd.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUp.this, "Please Enter all details!!!", Toast.LENGTH_SHORT).show();
                }


                else {
                    final String s[]=email.getText().toString().trim().split("@");
                    FirebaseDatabase.getInstance().getReference("USERS").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(s[0]))
                            {
                                Toast.makeText(SignUp.this, "User Already Registered!!", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString().trim(), pswd.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        String mail=email.getText().toString().trim();
                                                        final  String x[]=mail.split("@");
                                                        ud=new UserDetails(name.getText().toString().trim(),"0"
                                                                ,email.getText().toString().trim(),pswd.getText().toString().trim());
                                                        FirebaseDatabase.getInstance().getReference("USERS").child(x[0]).setValue(ud).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    Toast.makeText(SignUp.this, "Registerd Successfully!!PLease check your email for verification!!!", Toast.LENGTH_SHORT).show();

                                                                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                                                    SharedPreferences.Editor editor = pref.edit();
                                                                    editor.putBoolean("login", false);
                                                                    editor.commit();

                                                                    SharedPreferences pref1 = getApplicationContext().getSharedPreferences("Name", 0); // 0 - for private mode
                                                                    SharedPreferences.Editor editor1 = pref1.edit();
                                                                    editor1.putString("name",email.getText().toString().trim());
                                                                    editor1.commit();
                                                                }
                                                            }
                                                        });

                                                    } else {
                                                        Toast.makeText(SignUp.this, "Error!!! " + task.getException(), Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });


                                        }
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SignIn.class));
                finish();

            }
        });
    }
}
