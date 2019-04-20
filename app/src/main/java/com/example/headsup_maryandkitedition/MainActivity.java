package com.example.headsup_maryandkitedition;

import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.headsup_maryandkitedition.database.DatabaseHelper;
import com.example.headsup_maryandkitedition.database.model.WordInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // region PUBLIC VARIABLES
    int numberOfTeams;
    ArrayList<Player> players = new ArrayList<Player>();
    int currentTeam = 1;
    int wordsPP = 0;
    int currentWordsEntered = 0;
    int currentPlayer = 0;
    String[] currentWords;
    int editDropdownPos = 0;
    DatabaseHelper db;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(getApplicationContext());
        db.reset();
    }

    // region GAMESTATES
//    game starts here
    public void mainActivityNewGameButton(View v) {
        setContentView(R.layout.number_of_teams);
    }

//    capture number of teams from edit text
    public void numberOfTeamsButton(View v) {
        EditText result = findViewById(R.id.numberOfTeams);
        if (!result.getText().toString().equals("")) {
            numberOfTeams = Integer.parseInt(result.getText().toString());
            setContentView(R.layout.people_on_team);
        } else {
            this.toastHelper("Please enter a number");
        }
    }

//    ask each team for the names of players on their team
    public void peopleOnTeamButton(View v) {
        TextView instruct = findViewById(R.id.teamNumber);
//        captures last team's names and then changes to number of words screen
        if (currentTeam >= numberOfTeams) {
            EditText result = findViewById(R.id.teamNames);
            if (!result.getText().toString().equals("")) {
                String[] names = result.getText().toString().split(";");
                for (String name : names) {
                    Player cPlayer = new Player(name, currentTeam);
                    players.add(cPlayer);
                }
                setContentView(R.layout.number_of_words);
            } else {
                this.toastHelper("Please enter players' names");
            }
//            loops through number of teams entered and captures names
        } else {
            EditText result = findViewById(R.id.teamNames);
            if (!result.getText().toString().equals("")) {
                String[] names = result.getText().toString().split(";");
                for (String name : names) {
                    Player cPlayer = new Player(name, currentTeam);
                    players.add(cPlayer);
                }
                currentTeam++;
                instruct.setText("Team " + currentTeam);
                result.setText("");
            } else {
                this.toastHelper("Please enter players' names");
            }
        }
    }

//    captures number of words each person will enter
    public void numberOfWordsButton(View v) {
        EditText result = findViewById(R.id.wordNumber);
        if (!result.getText().toString().equals("")) {
            wordsPP = Integer.parseInt(result.getText().toString());
            currentWords = new String[wordsPP];
            setContentView(R.layout.enter_word);
            initEnterWordsView();
        } else {
            this.toastHelper("Please enter a number");
        }
    }

//    captures each players words and adds to database
    public void enterWordsButton(View v) {
        EditText result = findViewById(R.id.wordInputBox);
        TextView currentWordLabel = findViewById(R.id.wordCountLabel);
//        captures last word and then changes to confirm screen
        if (currentWordsEntered >= wordsPP - 1) {
            if (!result.getText().toString().equals("")) {
                currentWords[currentWordsEntered] = result.getText().toString();
                setContentView(R.layout.confirm_words);
                initConfirmWordsView();
            } else {
                this.toastHelper("please enter a word");
            }
//            captures each word entered by player and changes labels to reflect submitted word
        } else {
            if (!result.getText().toString().equals("")) {
                currentWords[currentWordsEntered] = result.getText().toString();
                currentWordsEntered++;
                result.setText("");
                currentWordLabel.setText(currentWordsEntered + "/" + wordsPP);
            } else {
                this.toastHelper("please enter a word");
            }
        }
    }

//    @kiet comment this
    public void confirmWordsChangeButton(View v) {
        setContentView(R.layout.edit_word);

        final Spinner spinner = findViewById(R.id.spinner);
        final EditText changeWordInput = findViewById(R.id.changeWordInput);

        List<String> words = Arrays.asList(currentWords);

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, words) {

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
                editDropdownPos = position;
                // Notify the selected item text
                toastHelper("Selected: " + selectedItemText);
                changeWordInput.setText(selectedItemText);
                changeWordInput.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

//    @kiet comment this
    public void editWordButton(View v) {
        final EditText input = findViewById(R.id.changeWordInput);
        String updatedWord = input.getText().toString();

        currentWords[editDropdownPos] = updatedWord;
        setContentView(R.layout.confirm_words);
        initConfirmWordsView();
    }

//    @kiet comment this
    public void confirmWordsConfirmButton(View v) {
        // insert words in db
        db = new DatabaseHelper(getApplicationContext());
        for (String word : currentWords) db.createWord(word, players.get(currentPlayer).getName());
        printWordTable(db.getAllWords());

        // logic for going to next player for words or starting game
        if (currentPlayer == players.size() - 1) {
            // start game
            setContentView(R.layout.round1_instructions);
        } else {
            // go to next player
            currentPlayer++;
            setContentView(R.layout.enter_word);
            // reset ui for enter_words
            initEnterWordsView();
        }
    }

//    start round1 after instructions
    public void round1InstructButton(View v) {
        setContentView(R.layout.round1);
    }
    // endregion


    // region HELPER FUNCTIONS
//    helps write quick toast messages
    private void toastHelper(String s) {
        Toast warning = Toast.makeText(this, s, Toast.LENGTH_LONG);
        warning.show();
    }

//    initializes the enter words view with correct text
    private void initEnterWordsView() {
        TextView instruct = findViewById(R.id.instructions);
        instruct.setText(players.get(currentPlayer).getName() + " provide a word for the game");
        TextView wordCountLabel = findViewById(R.id.wordCountLabel);
        wordCountLabel.setText("0/" + wordsPP);
        currentWordsEntered = 0;
    }

//    initializes the confirm words view with correct text
    private void initConfirmWordsView() {
        TextView wordsList = findViewById(R.id.wordsList);
        wordsList.setText(getFormattedWordsList(currentWords));
    }

//    @kiet comment this
    private void displayWords() {
        String res = "";
        for (int i = 0; i < currentWords.length; i++) {
            if (i != 0 && i % 3 == 0) res += System.getProperty("line.separator");
            res += "    " + currentWords[i];
        }
        TextView list = findViewById(R.id.wordsList);
        list.setText(res);
    }

//    @kiet comment this
    private String getFormattedWordsList(String[] words) {
        String res = "";
        for (int i = 0; i < words.length; i++) {
            if (i != 0 && i % 3 == 0) res += System.getProperty("line.separator");
            res += "    " + words[i];
        }
        return res;
    }

//    @kiet comment this
    private void printWordTable(List<WordInstance> words) {
        String res = "\n-----Word Table-----\n";

        for (WordInstance w : words) {
            res += w.toString();
        }

        Log.v("DBTEST", res);
    }
    // endregion
}
