package com.example.dairyapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Settings extends AppCompatActivity {
    Toolbar toolbar;
    EditText editTextUsername;
    Button btnSaveUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //app bar title
        setTitle("Settings");

        //UI Components
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        editTextUsername = findViewById(R.id.editTextUsername);
        btnSaveUsername = findViewById(R.id.btnSaveUsername);

        //toolbar
        setSupportActionBar(toolbar);

        //back navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Retrieving the value using its keys the file name
        SharedPreferences sh = getSharedPreferences("SharedPref", MODE_PRIVATE);

        // The value will be default as empty string
        String username = sh.getString("username", "Ramith");

        editTextUsername.setText(username);

        btnSaveUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating an Editor object to edit(write to the file)
                SharedPreferences.Editor myEdit = sh.edit();

                // Storing the key and its value as the data fetched from edittext
                myEdit.putString("username", editTextUsername.getText().toString());

                // commit to apply those changes
                myEdit.commit();
                Toast.makeText(getApplicationContext(), "Username saved successfully", Toast.LENGTH_LONG).show();

            }
        });

    }
}