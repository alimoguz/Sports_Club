package oguuz.alim.sports_club.models;

/**
 * Created by Alim on 21.4.2018.
 */

public class students {

    public String name;
    public String surname;
    public String mothername;
    public String fathername;
    public String phone;
    public String address;
    public String reg_date;
    public String birth_date;
    public String send;
    public String  image;

    public String getSend() {
        return send;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getNumber_course() {
        return number_course;
    }

    public void setNumber_course(String number_course) {
        this.number_course = number_course;
    }

    public String number_course;


    public students(){

    }


    public students(String name, String surname, String mothername, String fathername,String phone, String address, String reg_date, String birth_date,String number_course) {
        this.name = name;
        this.surname = surname;
        this.mothername = mothername;
        this.fathername = fathername;
        this.phone = phone;
        this.address = address;
        this.reg_date = reg_date;
        this.birth_date = birth_date;
        this.number_course=number_course;


    }

    public String getName() {
        return name;
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

    public String getMothername() {
        return mothername;
    }

    public void setMothername(String mothername) {
        this.mothername = mothername;
    }

    public String getFathername() {
        return fathername;
    }

    public void setFathername(String fathername) {
        this.fathername = fathername;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReg_date() {
        return reg_date;
    }

    public void setReg_date(String reg_date) {
        this.reg_date = reg_date;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }


}
