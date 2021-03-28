package model;

import java.time.LocalDate;

public class Contact {
    private String surname, name, lastName, mobilePhone, homePhone, address, comment;
    private LocalDate dateOfBirth;

    public Contact(String surname, String name, String ln, String mobilePhone, String homePhone,
                   String address, LocalDate dateOfBirth, String comment) {

        this.surname = surname;
        this.name = name;
        this.lastName = ln;
        this.mobilePhone = mobilePhone;
        this.homePhone = homePhone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.comment = comment;
    }

    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getHomePhone() {
        return homePhone;
    }
    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Перегруженный метод проверки равенства объектов.
     * По условию два объекта равны, если совпадает триплет: Имя, Фамилия, Отчество
     * @param o объект для сравнения
     * @return булка результата сравнения двух контактов
     */
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Contact)) { return false; }

        Contact contact = (Contact)o;

        return (this.name.equals(contact.name)) &&
                (this.surname.equals(contact.surname)) && (this.lastName.equals(contact.lastName));
    }
}
