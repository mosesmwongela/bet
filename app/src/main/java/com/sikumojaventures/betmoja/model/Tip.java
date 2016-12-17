package com.sikumojaventures.betmoja.model;

/**
 * Created by turnkey on 12/17/2016.
 */

public class Tip {

    private String tip_id, home_team, away_team, date, kick_off, odd, prediction, score, result, bought, image;

    public Tip() {
    }

    public Tip(String tip_id, String home_team, String away_team, String date, String kick_off, String odd, String prediction, String score, String result, String bought, String image) {
        this.tip_id = tip_id;
        this.home_team = home_team;
        this.away_team = away_team;
        this.date = date;
        this.kick_off = kick_off;
        this.odd = odd;
        this.prediction = prediction;
        this.score = score;
        this.result = result;
        this.bought = bought;
        this.image = image;
    }

    public String getTip_id(String tip_id) {
        return this.tip_id;
    }

    public void setTip_id(String tip_id) {
        this.tip_id = tip_id;
    }

    public String getHome_team() {
        return home_team;
    }

    public void setHome_team(String home_team) {
        this.home_team = home_team;
    }

    public String getAway_team() {
        return away_team;
    }

    public void setAway_team(String away_team) {
        this.away_team = away_team;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKick_off() {
        return kick_off;
    }

    public void setKick_off(String kick_off) {
        this.kick_off = kick_off;
    }

    public String getOdd() {
        return odd;
    }

    public void setOdd(String odd) {
        this.odd = odd;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getBought() {
        return bought;
    }

    public void setBought(String bought) {
        this.bought = bought;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
