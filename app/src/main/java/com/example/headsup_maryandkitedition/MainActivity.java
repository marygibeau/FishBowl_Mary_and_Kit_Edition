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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.headsup_maryandkitedition.database.DatabaseHelper;
import com.example.headsup_maryandkitedition.database.model.WordInstance;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
//    TODO: Mary - check input validity
//    TODO: Mary - randomize player array so teams rotate
//    TODO: Kiet - get random word from database
//    TODO: Mary - create round UI
//    TODO: Kiet - get random word to be displayed by UI
//    TODO: Kiet - update number of skips and in-play variables for each word in database during play


    // region PUBLIC VARIABLES
    int numberOfTeams;
    ArrayList<Player> players = new ArrayList<Player>();
    int currentTeam = 1;
    int wordsPP = 0;
    int currentWordsEntered = 0;
    int currentPlayer = 0;
    String[] currentWords;
    List<WordInstance> playableWords = new ArrayList<>();
    int currentWordIndex = 0;
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

//  set up selectable dropdown menu of words that player can edit
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

//  player has updated word(s); save word and take back to list of words confirmation
    public void editWordButton(View v) {
        final EditText input = findViewById(R.id.changeWordInput);
        String updatedWord = input.getText().toString();

        currentWords[editDropdownPos] = updatedWord;
        setContentView(R.layout.confirm_words);
        initConfirmWordsView();
    }

//  player confirms his/her words and we write words to the db
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
        initRound1();
    }
    // endregion

    private void initRound1() {
        // get all words that haven't been guessed correctly (and shuffled)
        playableWords = db.getWordsByGuessSuccessRandomized(0);

        startTimer();
        cycleWords();
    }

    public void correctGuess(View v) {
        // update record in db for field guessSuccess
        WordInstance curr = playableWords.get(currentWordIndex % playableWords.size());
        curr.setGuessSuccess(1);

        db.updateGuessSuccess(curr, 1);

        printWordTable(db.getWordsByGuessSuccess(1));

        currentWordIndex++;
        cycleWords();
    }

    public void skipWord(View v) {
        WordInstance curr = playableWords.get(currentWordIndex % playableWords.size());
        curr.setGuessSuccess(0);
        curr.setSkips(curr.getSkips() + 1);

        db.updateGuessSuccess(curr, 0);

        printWordTable(db.getWordsByGuessSuccess(0));

        currentWordIndex++;
        cycleWords();
    }

    private void startTimer() {
        final TextView displayedWord = findViewById(R.id.timer);
        final LinearLayout skipButton = findViewById(R.id.skipButton);
        final LinearLayout guessedButton = findViewById(R.id.guessedButton);

        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int count = 30;
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (count < 0) {
                            timer.cancel();
                            displayedWord.setText("Time's up!");
                            skipButton.setOnClickListener(null);
                            guessedButton.setOnClickListener(null);
                            return;
                        }
                        displayedWord.setText(count + "");
                        count--;
                    }
                });
            }
        }, 1000, 1000);
    }

    private void cycleWords() {
        TextView displayedWord = findViewById(R.id.displayedWord);
        WordInstance curr = playableWords.get(currentWordIndex % playableWords.size());

        while (curr.getGuessSuccess() == 1) {
            currentWordIndex++;
            curr = playableWords.get(currentWordIndex % playableWords.size());
        }
        displayedWord.setText(curr.getWord());
    }

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

//  creates formmatted string of words to be displayed for confirmation
    private String getFormattedWordsList(String[] words) {
        String res = "";
        for (int i = 0; i < words.length; i++) {
            if (i != 0 && i % 3 == 0) res += System.getProperty("line.separator");
            res += "    " + words[i];
        }
        return res;
    }

//  logs content of Word table from db for debugging
    private void printWordTable(List<WordInstance> words) {
        String res = "\n-----Word Table-----\n";

        for (WordInstance w : words) {
            res += w.toString();
        }

        Log.v("DBTEST", res);
    }
    // endregion
}
