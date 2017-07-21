package groupdeadline.com.moviereview.user;

/**
 * Created by KunPark on 7/19/2017.
 */

public class User {
    private String userName;
    private String idUser;
    private String linkImageUser;
    private String email;

    //Contructor

    public User(String userName, String idUser, String linkImageUser, String email) {
        this.userName = userName;
        this.idUser = idUser;
        this.linkImageUser = linkImageUser;
        this.email = email;
    }
    //Getter and Setter

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getLinkImageUser() {
        return linkImageUser;
    }

    public void setLinkImageUser(String linkImageUser) {
        this.linkImageUser = linkImageUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
