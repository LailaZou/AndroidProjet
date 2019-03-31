package com.example.laila.miniprojet;

public class Comments {
    String auteur , contenu , image;

    public Comments(String auteur, String contenu, String image) {
        this.auteur = auteur;
        this.contenu = contenu;
        this.image = image;
    }

    public Comments() {
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
