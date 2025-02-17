package com.example.dairyapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddNewEntry extends AppCompatActivity {
    //layout components
    Toolbar toolbar;
    TextView selectedDateText;
    TextView selectedTimeText;
    Button pickDateBtn;
    Button pickTimeBtn;
    EditText editTextData;
    Button btnSave;
    Button btnImage;

    // One Preview Image
    ImageView imgPreview;
    //image path
    Uri selectedImageUri;

    // constant to compare the activity result code
    int SELECT_PICTURE = 200;

    //db
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_entry);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //app bar title
        setTitle("Add New Entry");

        //UI Components
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        selectedDateText = findViewById(R.id.txtDate);
        selectedTimeText = findViewById(R.id.txtTime);
        pickDateBtn = findViewById(R.id.btnEditDate);
        pickTimeBtn = findViewById(R.id.btnEditTime);
        btnImage = findViewById(R.id.btnAddImage);
        imgPreview = findViewById(R.id.imgSelected);
        editTextData = (EditText) findViewById(R.id.editTextTextMultiLine);
        btnSave = findViewById(R.id.btnSaveEntry);

        //toolbar
        setSupportActionBar(toolbar);

        //back navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ////////// Date ///////////
        //set current date
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        selectedDateText.setText(formattedDate);

        //set current time
        // variable for simple date format.
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        // for current date and time and calling a simple date format in it.
        String currentTime = sdf.format(new Date());
        selectedTimeText.setText(currentTime);

        // set user selected date
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
                        AddNewEntry.this,
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

        ///////////// Time //////////////
        //set user selected time
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewEntry.this,
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

        ///////// Image ///////////
        // handle the Choose Image button to trigger
        btnImage.setOnClickListener(new View.OnClickListener() {
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

        ////////// Save ///////////
        //db
        dbManager = new DBManager(this);
        dbManager.open();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //variables
                final String date = selectedDateText.getText().toString();
                final String time = selectedTimeText.getText().toString();
                final String data = editTextData.getText().toString();

                if(data.matches("")){
                    Toast.makeText(getApplicationContext(), "Dairy entry is empty",Toast.LENGTH_LONG).show();
                }else{
                    if(selectedImageUri!=null){
                        final String imageURI = selectedImageUri.toString();
                        dbManager.insert(date,time,data, imageURI);
                    }else{
                        dbManager.insert(date,time,data, null);
                    }
                    //show toast msg
                    Toast.makeText(getApplicationContext(), "Entry added successfully",Toast.LENGTH_LONG).show();
                    //navigate to diary list
                    Intent INT=new Intent(AddNewEntry.this, ViewDiaries.class);
                    startActivity(INT);
                }

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
                    imgPreview.setImageURI(selectedImageUri);
                }
            }
        }
    }
}