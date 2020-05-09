package util;

import android.app.Application;

public class JournalApi extends Application {
    private String username;
    private String userId;
    private static JournalApi instance;   //Make singleton, thats why we need a type of journaltype

    public static JournalApi getInstance() {                         //getinstance method of type JournalApi
        if (instance == null)
            instance = new JournalApi();
        return instance;
    }



    public JournalApi() {}      //Empty constructor



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
