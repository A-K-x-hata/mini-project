package com.example.skinlesionfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText username,email,password;
    Button r,s;
    DBHelper DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username=findViewById(R.id.uname);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        r=findViewById(R.id.register);
        s=findViewById(R.id.signin);
        DB=new DBHelper(this);
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user=username.getText().toString();
                String ema=email.getText().toString();
                String pass=password.getText().toString();
                if(user.equals("")||ema.equals("")||pass.equals(""))
                    Toast.makeText(MainActivity.this,"Please enter all the fields",Toast.LENGTH_LONG).show();
                else{
                    Boolean checkuser=DB.checkusername(user);
                    if(checkuser==false) {
                        Boolean insert = DB.insertDate(user, ema, pass);
                        if (insert == true) {
                            Toast.makeText(MainActivity.this, "Data inserted successfully", Toast.LENGTH_LONG).show();
                            Intent in=new Intent(getApplicationContext(), main_page.class);
                            startActivity(in);
                        }
                    }
                    else Toast.makeText(MainActivity.this,"user already exists",Toast.LENGTH_LONG).show();

                }
            }
        });
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
            }
        });
    }
}