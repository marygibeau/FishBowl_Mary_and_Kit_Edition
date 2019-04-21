package com.example.headsup_maryandkitedition.database.model;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.headsup_maryandkitedition.database.DatabaseHelper;

import java.util.List;

public class DatabaseHelperTester {
    private DatabaseHelper db;

    public DatabaseHelperTester(DatabaseHelper db) {
        this.db = db;
    }

    public void testInsertWord() {
        final String test = "Test";
        final String player = "Player";
        for (int i = 0; i < 10; i++) {
            db.createWord("Test" + i, "Player" + i);
        }

        printWordTable(db.getAllWords());
    }

    public void printWordTable(List<WordInstance> words) {
        String res = "\n-----Word Table-----\n";

        for (WordInstance w : words) {
            res += w.toString();
        }

        Log.v("DBTEST", res);
    }
}
