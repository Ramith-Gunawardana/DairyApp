package com.example.dairyapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class ViewDiaries extends AppCompatActivity {
    private DBManager dbManager;

    private ListView listView;

    private SimpleCursorAdapter adapter;

    final String[] from = new String[] {
            DatabaseHelper.DATA,
            DatabaseHelper.DATE,
            DatabaseHelper.TIME,
            DatabaseHelper._ID,
            DatabaseHelper.IMAGE,
    };

    final int[] to = new int[] { R.id.data, R.id.date, R.id.time,R.id.id, R.id.imageURI };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_diaries);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //app bar title
        setTitle("View Diaries");

        //toolbar
        Toolbar viewDiairesToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(viewDiairesToolbar);
        
        //navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //view entry items
        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch();

        listView = (ListView) findViewById(R.id.list_view);

        adapter = new SimpleCursorAdapter(this, R.layout.list_item, cursor, from, to, 0);
        adapter.notifyDataSetChanged();


        listView.setAdapter(adapter);

         //OnCLickListiner For List Items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long viewId) {
                TextView dataTextView = (TextView) view.findViewById(R.id.data);
                TextView dateTextView = (TextView) view.findViewById(R.id.date);
                TextView timeTextView = (TextView) view.findViewById(R.id.time);
                TextView idTextView = (TextView) view.findViewById(R.id.id);
                TextView imageTextView = (TextView) view.findViewById(R.id.imageURI);

                String data = dataTextView.getText().toString();
                String date = dateTextView.getText().toString();
                String time = timeTextView.getText().toString();
                String id = idTextView.getText().toString();
                String image = imageTextView.getText().toString();

                Intent modify_intent = new Intent(getApplicationContext(), EditEntry.class);
                modify_intent.putExtra("id", id);
                modify_intent.putExtra("data", data);
                modify_intent.putExtra("date", date);
                modify_intent.putExtra("time", time);
                modify_intent.putExtra("image", image);

                startActivity(modify_intent);
            }
        });
    }

}