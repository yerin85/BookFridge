package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

public class SearchMenu extends AppCompatActivity {

    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menu);

        //dropdown list
        Spinner spinner = findViewById(R.id.search_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.search_type_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //search button
        ImageButton searchButton = findViewById(R.id.search_button);

        //search text
        final EditText editText = findViewById(R.id.search_input);
        editText.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                query= editText.getText().toString();
            }
        });


    }
}