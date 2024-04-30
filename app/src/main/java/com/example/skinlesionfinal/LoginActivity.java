package com.example.skinlesionfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText ue,p;
    Button si;
    DBHelper DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ue=findViewById(R.id.username_email);
        p=findViewById(R.id.password2);
        si=findViewById(R.id.signin1);
        DB=new DBHelper(this);
        si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uem=ue.getText().toString();
                String pass=p.getText().toString();
                if(uem.equals("")||pass.equals(""))
                {
                    Toast.makeText(LoginActivity.this,"Some Fields are empty",Toast.LENGTH_LONG).show();

                }
                else {
                    Boolean checkuserpass=DB.checkep(uem,pass);
                    if(checkuserpass==true){
                        Intent i=new Intent(getApplicationContext(), main_page.class);
                        i.putExtra("username", uem);
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(LoginActivity.this,"Email and passwords don't match",Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }
}
