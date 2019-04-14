package com.example.headsup_maryandkitedition.database.model;

public class WordInstance {
    private int id;
    private String playerName;
    private String word;
    private int guessSuccess;
    private String createdAt;

    public WordInstance() {}

    public int getId() {
        return id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getWord() {
        return word;
    }

    public int getGuessSuccess() {
        return guessSuccess;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setGuessSuccess(int guessSuccess) {
        this.guessSuccess = guessSuccess;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
