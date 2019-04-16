package com.example.headsup_maryandkitedition;

import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    int numberOfTeams;
    ArrayList<Player> players = new ArrayList<Player>();
    int currentTeam = 1;
    int wordsPP = 0;
    int currentWordsEntered = 0;
    String[] currentWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    void changeToTeamNum(View v) {
        setContentView(R.layout.number_of_teams);
    }

    void changeToTeamMembers(View v) {
        EditText result = findViewById(R.id.numberOfTeams);
        if (!result.getText().toString().equals("")) {
            numberOfTeams = Integer.parseInt(result.getText().toString());
            Log.v("test", "the number of teams is: " + numberOfTeams);
            setContentView(R.layout.people_on_team);
        } else {
            this.toastHelper("Please enter a number");
        }
    }

    void changeToNumberOfWordsOrStay(View v) {
        TextView instruct = findViewById(R.id.teamNumber);
        if(currentTeam >= numberOfTeams) {
            setContentView(R.layout.number_of_words);
        } else {
            EditText result = findViewById(R.id.teamNames);
            if (!result.getText().toString().equals("")) {
                String[] names = result.getText().toString().split(";");
                Log.v("test", "" + names.length);
                for (String name : names) {
                    Player currentPlayer = new Player(name, currentTeam);
                    players.add(currentPlayer);
                }
                currentTeam++;
                instruct.setText("Team " + currentTeam);
                result.setText("");
            } else {
                this.toastHelper("Please enter players' names");
            }
        }
    }

    void changeToEnterWords(View v) {
        EditText result = findViewById(R.id.wordNumber);
        if (!result.getText().toString().equals("")) {
            wordsPP = Integer.parseInt(result.getText().toString());
            Log.v("test", "the number of words is: " + wordsPP);
            this.toastHelper("number successfully entered");
            setContentView(R.layout.enter_word);
        } else {
            this.toastHelper("Please enter a number");
        }

    }

    void toastHelper(String s) {
        Toast warning = Toast.makeText(this, s , Toast.LENGTH_LONG);
        warning.show();
    }
}
