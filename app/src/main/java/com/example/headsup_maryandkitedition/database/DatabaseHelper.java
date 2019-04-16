package com.example.headsup_maryandkitedition.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.headsup_maryandkitedition.database.model.WordInstance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "FishbowlDB";

    // Table Names
    private static final String TABLE_WORD = "Word";

    // Common column names
    private static final String KEY_ID = "Id";
    private static final String KEY_CREATED_AT = "CreatedAt";

    // Word Table = column names
    private static final String KEY_PLAYER= "Player";
    private static final String KEY_WORD = "WordString";
    private static final String KEY_SKIPS = "Skips";
    private static final String KEY_GUESS_SUCCESS = "GuessSuccess";

    // Table Create Statements
    private static final String CREATE_TABLE_WORD =
            "Create Table " +
            TABLE_WORD + "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_PLAYER + " TEXT," +
            KEY_WORD + " TEXT, " +
            KEY_GUESS_SUCCESS + " INT," +
            KEY_SKIPS + " INT, " +

            KEY_CREATED_AT + " DATETIME" +")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORD);
        // creating required tables
        db.execSQL(CREATE_TABLE_WORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORD);

        // create new tables
        onCreate(db);
    }

    public long createWord(String word, String playerName) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLAYER, playerName);
        values.put(KEY_WORD, word);
        values.put(KEY_SKIPS, 0);
        values.put(KEY_GUESS_SUCCESS, 0);
        values.put(KEY_CREATED_AT, getDateTime());

        Log.v("QUERYTAG", "");

        // insert row
        long wid = db.insert(TABLE_WORD, null, values);
        Log.v("CREATE", "WID: " + wid);

        return wid;
    }

    public List<WordInstance> getAllWords() {
        List<WordInstance> words = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_WORD + " w";

        Log.v("QUERYTAG", selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                WordInstance wi = new WordInstance();
                wi.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                wi.setPlayerName(c.getString(c.getColumnIndex(KEY_PLAYER)));
                wi.setWord(c.getString(c.getColumnIndex(KEY_WORD)));
                wi.setGuessSuccess(c.getInt(c.getColumnIndex(KEY_GUESS_SUCCESS)));
                wi.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                words.add(wi);
            } while (c.moveToNext());
        }

        return words;
    }

    // get all words by succesful or failed guess
    public List<WordInstance> getWordsByGuessSuccess(final int guess) {
        List<WordInstance> words = new ArrayList<>();
        String selectQuery =
                "SELECT * FROM " + TABLE_WORD + " w " +
                "WHERE w." +
                KEY_GUESS_SUCCESS + " = '" + guess + "'";

        Log.v("QUERYTAG", selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                WordInstance wi = new WordInstance();
                wi.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                wi.setPlayerName(c.getString(c.getColumnIndex(KEY_PLAYER)));
                wi.setWord(c.getString(c.getColumnIndex(KEY_WORD)));
                wi.setGuessSuccess(c.getInt(c.getColumnIndex(KEY_GUESS_SUCCESS)));
                wi.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                words.add(wi);
            } while (c.moveToNext());
        }

        return words;
    }

    /**
     * get datetime
     **/
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
