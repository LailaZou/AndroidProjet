package com.example.laila.miniprojet;
public class Commentaire {
    private int mImageResource;
    private String auteur;
    private String commentaire;

    public Commentaire(String text1, String text2) {
        mImageResource = R.drawable.edit_icon;
        auteur = text1;
        commentaire = text2;
    }

    public String auteur() {
        return auteur;
    }

    public String commentaire() {
        return commentaire;
    }

    public int getImageResource() {
        return mImageResource;
    }
}
