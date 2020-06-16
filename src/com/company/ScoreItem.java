package com.company;

public class ScoreItem {

    private int city;
    private int item;
    private double score;

    public ScoreItem(int city, int item, double score) {
        this.city = city;
        this.item = item;
        this.score = score;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }


}
