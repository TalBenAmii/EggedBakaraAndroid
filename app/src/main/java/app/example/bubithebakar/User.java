package app.example.bubithebakar;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * This class holds the user who has his own username, password, hebrew and english word lists.
 * This class have getters, setters and constructors.
 */

public class User implements Serializable {
    private int monthlyBakarot = 0, monthlyTikufim = 0, monthlyKnasot = 0, monthlyBakarotGoal = 0, monthlyTikufimGoal = 0, monthlyKnasotGoal = 0;
    private String userName;
    private String password;
    private HashMap<String,DayData> history = new HashMap<>();
    private String lastUpdatedDate;
    public User() {
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public void resetHistory() {
        this.history = new HashMap<>();
    }

    public HashMap<String, DayData> getHistory() {
        return history;
    }

    public void monthReset() {
        this.monthlyTikufim = 0;
        this.monthlyBakarot = 0;
        this.monthlyKnasot = 0;
    }

    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void updateHistory(String date, DayData dayData) {
        this.history.put(date,dayData);
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public int getMonthlyBakarot() {
        return monthlyBakarot;
    }

    public void addMonthlyBakarot(int monthlyBakarot) {
        this.monthlyBakarot += monthlyBakarot;
    }

    public int getMonthlyTikufim() {
        return monthlyTikufim;
    }

    public void addMonthlyTikufim(int monthlyTikufim) {
        this.monthlyTikufim += monthlyTikufim;
    }

    public int getMonthlyKnasot() {
        return monthlyKnasot;
    }

    public void addMonthlyKnasot(int monthlyKnasot) {
        this.monthlyKnasot += monthlyKnasot;
    }

    public int getMonthlyBakarotGoal() {
        return monthlyBakarotGoal;
    }

    public void setMonthlyBakarotGoal(int monthlyBakarotGoal) {
        this.monthlyBakarotGoal = monthlyBakarotGoal;
    }

    public int getMonthlyTikufimGoal() {
        return monthlyTikufimGoal;
    }

    public void setMonthlyTikufimGoal(int monthlyTikufimGoal) {
        this.monthlyTikufimGoal = monthlyTikufimGoal;
    }

    public int getMonthlyKnasotGoal() {
        return monthlyKnasotGoal;
    }

    public void setMonthlyKnasotGoal(int monthlyKnasotGoal) {
        this.monthlyKnasotGoal = monthlyKnasotGoal;
    }

    @Override
    public String toString() {
        return userName + "- " + "בקרות: " + monthlyBakarot + " " + "תיקופים: " + monthlyTikufim + " " + "קנסות: " + monthlyKnasot;
    }
}
