package com.example.dairyapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.util.Calendar;

public class EditEntry extends AppCompatActivity  {
    Toolbar toolbar;
    TextView selectedDateText;
    TextView selectedTimeText;
    Button pickDateBtn;
    Button pickTimeBtn;
    EditText editTextData;
    Button btnUpdate;
    Button btnDelete;
    Button btnUpdateImage;
    ImageView imgPreview;
    Uri selectedImageUri;

    //db
    private DBManager dbManager;

    private long _id;

    int SELECT_PICTURE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_entry);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //app bar title
        setTitle("Edit Entry");

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //back navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbManager = new DBManager(this);
        dbManager.open();

        editTextData = (EditText) findViewById(R.id.editTextdata_update);
        selectedDateText = findViewById(R.id.txtDate_update);
        selectedTimeText = findViewById(R.id.txtTime_update);
        imgPreview = findViewById(R.id.imgSelected_update);

        btnUpdate = (Button) findViewById(R.id.btnUpdateEntry);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnUpdateImage = (Button) findViewById(R.id.btnUpdateImage);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String data = intent.getStringExtra("data");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String image = intent.getStringExtra("image");

        _id = Long.parseLong(id);

        //////////////// update UI from DB ////////////

        //set data from db to UI
        selectedDateText.setText(date);
        selectedTimeText.setText(time);
        editTextData.setText(data);
        try{
            if (image!=null) {
                //convert to URI
                selectedImageUri = Uri.parse(image);

                // Load the image using Glide
                Glide.with(this)
                        .load(selectedImageUri)
                        .placeholder(R.drawable.baseline_image_24)
                        .into(imgPreview);
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
        }

        //////// Date //////////
        // set user selected date
        pickDateBtn = findViewById(R.id.btnEditDate);

        //click listener for pick date button
        pickDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditEntry.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // setting date to our text view.
                                selectedDateText.setText( String.format("%02d", dayOfMonth) + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + year);

                            }
                        },
                        //passing year, month and day for selected date in our date picker.
                        year, month, day);

                //disable future dates
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                // display date picker dialog.
                datePickerDialog.show();
            }
        });

        ///////// Time ///////////
        //set user selected time
        pickTimeBtn = findViewById(R.id.btnEditTime);

        // listener for our pick date button
        pickTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting our hour, minute.
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // on below line we are initializing our Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditEntry.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // on below line we are setting selected time
                                // in our text view.
                                selectedTimeText.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                            }
                        }, hour, minute, false);
                // at last we are calling show to
                // display our time picker dialog.
                timePickerDialog.show();
            }
        });

        /////// Image /////////
        // handle the Choose Image button to trigger
        btnUpdateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an instance of the intent of the type image
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);

                // pass the constant to compare it with the returned requestCode
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
            }
        });

        ///////// Update ////////
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //variables
                final String date = selectedDateText.getText().toString();
                final String time = selectedTimeText.getText().toString();
                final String data = editTextData.getText().toString();
                final String imageURI = selectedImageUri.toString();

                dbManager.update(_id,date,time,data, imageURI);

                //show toast msg
                Toast.makeText(getApplicationContext(), "Entry updated successfully",Toast.LENGTH_LONG).show();

                //navigate to diary list
                Intent INT=new Intent(EditEntry.this, ViewDiaries.class);
                startActivity(INT);
            }
        });

        ////////// Delete //////////
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbManager.delete(_id);

                //show toast msg
                Toast.makeText(getApplicationContext(), "Entry deleted successfully",Toast.LENGTH_LONG).show();

                //navigate to diary list
                Intent INT=new Intent(EditEntry.this, ViewDiaries.class);
                startActivity(INT);
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    //imgPreview.setImageURI(selectedImageUri);
                    Glide.with(this)
                            .load(selectedImageUri)
                            .placeholder(R.drawable.baseline_image_24)
                            .into(imgPreview);
                }
            }
        }
    }

}