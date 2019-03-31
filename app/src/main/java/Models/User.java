package Models;

public class User {
    private String id;
    private  String nom;
    private String email;
    private String imageUrl;

    public User(){



    }
    public User(String id,String nom,String email){

        this.id=id;
        this.nom=nom;
        this.email=email;

    }
    public String getid() {
        return id;
    }

    public String getemail() {
        return email;
    }

    public String getnom() {
        return nom;
    }

    public String getimageUrl() {
        return imageUrl;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public void setid(String id) {
        this.id = id;
    }

    public void setimageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setnom(String nom) {
        this.nom = nom;
    }
}
