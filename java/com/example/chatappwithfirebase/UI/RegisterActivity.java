package com.example.chatappwithfirebase.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.chatappwithfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    DatabaseReference reference;


    MaterialEditText edt_username,edt_password,edt_email;
    Button btn_regiser;
    TextView txt_login;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();

        Toolbar tool_bar=findViewById(R.id.tool_bar);
        setSupportActionBar(tool_bar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_login=findViewById(R.id.txt_signin);
        edt_username=findViewById(R.id.edt_username);
        edt_email=findViewById(R.id.edt_email);
        edt_password=findViewById(R.id.edt_passwoed);
        btn_regiser=findViewById(R.id.btn_register);


        btn_regiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd=new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please Wait..");
                pd.show();
                String username=edt_username.getText().toString();
                String password=edt_password.getText().toString();
                String email=edt_email.getText().toString();
                if (TextUtils.isEmpty(username)|TextUtils.isEmpty(password)|TextUtils.isEmpty(email))
                {
                    Toast.makeText(RegisterActivity.this,"ernter yor all data ...",Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }else if (password.length()<6)
                {Toast.makeText(RegisterActivity.this,"your password mus ba more 6",Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
                else {
                    Register(username,email,password);
                }
            }
        });



        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });






    }

    private  void Register(final String username, final String email, final String password)
    {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String uid=currentUser.getUid();
                            reference= FirebaseDatabase.getInstance().getReference("Users").child(uid);
                            HashMap<String,String>hashMap=new HashMap<String, String>();
                            hashMap.put("Id",uid);
                            hashMap.put("username",username.toLowerCase());
                            hashMap.put("ImageUrl","default");
                            hashMap.put("status","offline");
                            hashMap.put("search",username.toLowerCase());
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        pd.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    }
                                }
                            });

                        } else {
                            pd.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "you cant Register with this Email And password", Toast.LENGTH_LONG).show();

                        }

                        // ...
                    }
                });

        //--------------------------

    }



}
