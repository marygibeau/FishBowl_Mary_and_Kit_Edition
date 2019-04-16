package com.example.headsup_maryandkitedition;

import android.content.Context;
import android.graphics.Color;
import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.headsup_maryandkitedition.database.DatabaseHelper;
import com.example.headsup_maryandkitedition.database.model.DatabaseHelperTester;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_words);


        // initWordSpinner();
    }

    private void initWordSpinner() {
        final Spinner spinner = findViewById(R.id.spinner);
        final TextView wordsList = findViewById(R.id.wordsList);
        final EditText changeWordField = findViewById(R.id.changeWordInput);
        // use the array of input words

        String[] temp = new String[] { "temp0", "temp1", "temp2", "temp3", "temp4", "temp5", "temp6"};
        List<String> tempList = Arrays.asList(temp);

        wordsList.setText(getFormattedWordsList(tempList));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, tempList){

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // Notify the selected item text
                showToast("Selected: " + selectedItemText);
                changeWordField.setText(selectedItemText);
                changeWordField.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private String getFormattedWordsList(List<String> words) {
        String res = "";
        for (int i = 0; i < words.size(); i++) {
            if (i != 0 && i % 3 == 0) res += System.getProperty("line.separator");
            res += "    " + words.get(i);
        }
        return res;
    }

    public void updateWord(View view) {
        final Spinner spinner = findViewById(R.id.spinner);
        final TextView wordsList = findViewById(R.id.wordsList);
        final EditText changeWordField = findViewById(R.id.changeWordInput);

        final String updated = changeWordField.getText().toString();

        String[] temp = new String[] { "temp0", "temp1", "temp2", "temp3", "temp4", "temp5", "temp6"};
        final List<String> tempList = Arrays.asList(temp);

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, tempList){

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // Notify the selected item text
                showToast("Selected: " + selectedItemText);
                tempList.set(position, updated);
                wordsList.setText(getFormattedWordsList(tempList));
//                spinnerArrayAdapter.notifyDataSetChanged();
//                return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setAdapter(spinnerArrayAdapter);
    }

    public void goToEditWordView(View view) {
        setContentView(R.layout.edit_word);
        initWordSpinner();
    }

}
