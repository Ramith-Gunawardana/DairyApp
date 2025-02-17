package com.example.dairyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    Button btnViewDiary;
    Button btnAddNew;
    Button btnSettings;
    TextView txtWelcome;

    ImageView imgViewBackground;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //UI components
        btnViewDiary=(Button) findViewById(R.id.btniewDiary);
        btnAddNew=(Button) findViewById(R.id.btnAddNewEntry);
        btnSettings=(Button) findViewById(R.id.btnSettings);
        txtWelcome = findViewById(R.id.txtWelcomeMsg);
        imgViewBackground = findViewById(R.id.imageViewBackground);
        progressBar = findViewById(R.id.progressBar);

        // Retrieving the value using its keys the file name
        SharedPreferences sh = getSharedPreferences("SharedPref", MODE_PRIVATE);

        // The value will be default as empty string
        String username = sh.getString("username", "Ramith");

        // set username
        txtWelcome.setText("Welcome " + username + "!");

        //set background image
        if(isNetworkAvailable()){
            progressBar.setVisibility(View.VISIBLE);

            String url = "https://picsum.photos/1080/1920?random";

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(url).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String imageUrl = response.request().url().toString();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.get().load(imageUrl).into(imgViewBackground);
                        }
                    });

                    //change welcome text color
                    txtWelcome.setTextColor(Color.parseColor("#ffffff"));
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }


        //on click functions
        btnViewDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent INT=new Intent(MainActivity.this, ViewDiaries.class);
                startActivity(INT);

            }
        });
        btnAddNew.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent INT=new Intent(MainActivity.this, AddNewEntry.class);
                startActivity(INT);

            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent INT=new Intent(MainActivity.this, Settings.class);
                startActivity(INT);

            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}