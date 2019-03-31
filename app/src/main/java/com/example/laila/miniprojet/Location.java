package com.example.laila.miniprojet;

import java.util.ArrayList;

public class Location {
    public String id;
    public String Photo ;
    public String description;
    long latitude;
    long longitude;
    String nom;
    String point_interet;

    ArrayList<Integer> rates ;
    ArrayList<Comments> commentaires;

    public Location(String id , String photo, String description, long latitude, long longitude, String nom, String point_interet, ArrayList<Integer> rates, ArrayList<Comments> commentaires) {
        this.id = id;
        Photo = photo;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nom = nom;
        this.point_interet = point_interet;
        this.rates = rates;
        this.commentaires = commentaires;
    }

    public Location() {
    }

    public ArrayList<Integer> getRates() {
        return rates;
    }

    public int getRate() {

        int rate = 0;
        for(int i = 0 ; i < rates.size() ; i++){
            rate += rates.get(i);
        }
        return (int) rate/rates.size();
    }

    public void setRates(ArrayList<Integer> rates) {
        this.rates = rates;
    }

    public ArrayList<Comments> getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(ArrayList<Comments> commentaires) {
        this.commentaires = commentaires;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPoint_interet() {
        return point_interet;
    }

    public void setPoint_interet(String point_interet) {
        this.point_interet = point_interet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

