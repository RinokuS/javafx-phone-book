package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import model.exceptions.ContactAlreadyExistException;
import model.exceptions.ContactNameException;
import model.exceptions.ContactPhoneException;
import model.exceptions.ContactSurnameException;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Table {
    public static final ObservableList<Contact> mainData = FXCollections.observableArrayList();
    private static final Pattern namePattern = Pattern.compile("[a-zA-Zа-я-А-Я]+");
    private static final Pattern phonePattern = Pattern.compile("[+]?[-()\\s0-9]+");

    public static ObservableList<Contact> getFilteredData(String filterWord) {
        return mainData.filtered(contact -> {
            if (filterWord == null || filterWord.isEmpty())
                return true;

            String lowerCaseFW = filterWord.toLowerCase();

            if (contact.getSurname().toLowerCase().contains(lowerCaseFW)) {
                return true;
            } else if (contact.getName().toLowerCase().contains(lowerCaseFW)) {
                return true;
            } else {
                return contact.getLastName().toLowerCase().contains(lowerCaseFW);
            }
        });
    }

    /**
     * Метод для валидации имени, фамилии и номеров контакта, на которые стоят ограничения по ТЗ.
     * При любой из ошибок валидации выкидывает соответствующую ошибку.
     * @param name имя контакта
     * @param surname фамилия контакта
     * @param mobilePhone мобильный телефон
     * @param homePhone домашний телефон
     * @throws ContactNameException ошибка валидации имени
     * @throws ContactSurnameException ошибка валидации фамилии
     * @throws ContactPhoneException ошибка валидации номеров
     */
    public static void validateContact(String name, String surname,  String mobilePhone, String homePhone)
            throws ContactNameException, ContactSurnameException, ContactPhoneException {
        if (name.isBlank() || name.isEmpty())
            throw new ContactNameException("Name can't be empty!");
        if (!namePattern.matcher(name).matches())
            throw new ContactNameException("Name must be pattern-like!");

        if (surname.isBlank() || surname.isEmpty())
            throw new ContactSurnameException("Surname can't be empty!");
        if (!namePattern.matcher(surname).matches())
            throw new ContactSurnameException("Surname must be pattern-like!");

        if ((mobilePhone.isBlank() || mobilePhone.isEmpty()) && (homePhone.isBlank() || homePhone.isEmpty()))
            throw new ContactPhoneException("At least one phone number should be not empty!");
        if (!(phonePattern.matcher(mobilePhone).matches()) && !(phonePattern.matcher(homePhone).matches()))
            throw new ContactPhoneException("Strange symbols in phone number!");
    }

    /**
     * Метод для создания контакта по переданным аттрибутам, если они прошли валидацию.
     * @param name имя
     * @param surname фамилия
     * @param ln отчество
     * @param mobilePhone мобильный телефон
     * @param homePhone домашний телефон
     * @param address адрес
     * @param dateOfBirth дата рождения
     * @param comment комментарий/заметка
     * @return объект типа Contact с заданными параметрами
     * @throws ContactNameException ошибка валидации имени
     * @throws ContactSurnameException ошибка валидации фамилии
     * @throws ContactPhoneException ошибка валидации номеров
     * @throws ContactAlreadyExistException ошибка добавления существующего контакта
     */
    public static Contact validateAndCreate(String name, String surname, String ln, String mobilePhone,
                                            String homePhone, String address, LocalDate dateOfBirth, String comment)
            throws ContactNameException, ContactSurnameException, ContactPhoneException, ContactAlreadyExistException {
        validateContact(name, surname, mobilePhone, homePhone); // проводим валидацию
        Contact contact = new Contact(surname, name, ln, mobilePhone, // создаем объект контакта
                homePhone, address, dateOfBirth, comment);

        if (mainData.contains(contact)) // проверяем, существует ли уже подобный контакт
            throw new ContactAlreadyExistException("Contact already exist!");

        return contact;
    }

    /**
     * Метод для редактирования существующего контакта по переданным аттрибутам, если они прошли валидацию.
     * @param toEdit контакт, который будет отредактирован
     * @param name новое имя
     * @param surname новая фамилия
     * @param ln новое отчество
     * @param mobilePhone новый мобильный телефон
     * @param homePhone новый домашний телефон
     * @param address новый адрес
     * @param dateOfBirth новая дата рождения
     * @param comment новый комментарий/заметка
     * @throws ContactNameException ошибка валидации имени
     * @throws ContactSurnameException ошибка валидации фамилии
     * @throws ContactPhoneException ошибка валидации номеров
     * @throws ContactAlreadyExistException ошибка добавления существующего контакта
     */
    public static void validateAndEdit(Contact toEdit, String name, String surname, String ln, String mobilePhone,
                                       String homePhone, String address, LocalDate dateOfBirth, String comment)
            throws ContactNameException, ContactSurnameException, ContactPhoneException, ContactAlreadyExistException {
        validateContact(name, surname, mobilePhone, homePhone); // валидация аттрибутов
        Contact contact = new Contact(surname, name, ln, mobilePhone, // создаем новый объект
                homePhone, address, dateOfBirth, comment);
        // если новый объект не совпадает со старым, но при этом совпадает с каким-то из списка - останавливаем редактирование
        if (!toEdit.equals(contact) && mainData.contains(contact))
            throw new ContactAlreadyExistException("Contact already exist!");
        // после прохождения всех валидаций переносим аттрибуты в контакт для редактирования
        toEdit.setName(contact.getName());
        toEdit.setSurname(contact.getSurname());
        toEdit.setLastName(contact.getLastName());
        toEdit.setMobilePhone(contact.getMobilePhone());
        toEdit.setHomePhone(contact.getHomePhone());
        toEdit.setAddress(contact.getAddress());
        toEdit.setDateOfBirth(contact.getDateOfBirth());
        toEdit.setComment(contact.getComment());
    }
}
