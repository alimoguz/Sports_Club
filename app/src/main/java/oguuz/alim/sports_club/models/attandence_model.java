package oguuz.alim.sports_club.models;

/**
 * Created by Alim on 22.4.2018.
 */

public class attandence_model {

    public String name;
    public String surname;
    public String pushId;
    public String send;

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getName() {
        return name;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
