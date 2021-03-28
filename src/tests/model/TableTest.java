package model;

import javafx.collections.ObservableList;
import model.exceptions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {

    @BeforeAll
    static void beforeAll() {
        Contact contact = new Contact("Test", "Test", "Test", "777",
                "777", "address", null, "lol!");

        Table.mainData.add(contact);
    }

    @Test
    void getFilteredData() {
        Contact contact1 = new Contact("Good", "Test", "Test", "777",
                "777", "address", null, "lol!");
        Contact contact2 = new Contact("Test", "Good", "Test", "888",
                "888", "address", null, "wow!");
        Contact contact3 = new Contact("Test", "Test", "Good", "999",
                "999", "address", null, "lmao!");

        Contact contact4 = new Contact("Bad", "Test", "Test", "777",
                "777", "address", null, "lol!");
        Contact contact5 = new Contact("Test", "Bad", "Test", "888",
                "888", "address", null, "wow!");
        Contact contact6 = new Contact("Test", "Test", "Bad", "999",
                "999", "address", null, "lmao!");
        Table.mainData.addAll(Arrays.asList(contact1, contact2, contact3,
                contact4, contact5, contact6));

        ObservableList<Contact> filteredList = Table.getFilteredData("Good");
        // проверим размерность
        assertNotEquals(Table.mainData.size(), 3);
        assertEquals(filteredList.size(), 3);
        // проверим наличие заведомо подходящих контактов
        assertTrue(filteredList.contains(contact1));
        assertTrue(filteredList.contains(contact2));
        assertTrue(filteredList.contains(contact3));
        // проверим наличие заведомо неподходящих контактов
        assertFalse(filteredList.contains(contact4));
        assertFalse(filteredList.contains(contact5));
        assertFalse(filteredList.contains(contact6));
        // проверим корректность обработки пустых слов поиска
        ObservableList<Contact> filteredList2 = Table.getFilteredData(null);
        assertEquals(filteredList2.size(), Table.mainData.size());

        ObservableList<Contact> filteredList3 = Table.getFilteredData("");
        assertEquals(filteredList3.size(), Table.mainData.size());
    }

    @Test
    void validateContact() {
        assertThrows(ContactNameException.class, () -> {
            Table.validateContact("", "Иванов", "8 800 555 35 35", "");
        });
        assertThrows(ContactNameException.class, () -> {
            Table.validateContact("123Валера123", "Иванов", "8 800 555 35 35", "");
        });

        assertThrows(ContactSurnameException.class, () -> {
            Table.validateContact("Валера", "", "8 800 555 35 35", "");
        });
        assertThrows(ContactSurnameException.class, () -> {
            Table.validateContact("Валера", "BiG_Ivanofff_BiG", "8 800 555 35 35", "");
        });

        assertThrows(ContactPhoneException.class, () -> {
            Table.validateContact("Валера", "Иванов", "", "");
        });
        assertThrows(ContactPhoneException.class, () -> {
            Table.validateContact("Валера", "Иванов", "нету!", "");
        });
        assertThrows(ContactPhoneException.class, () -> {
            Table.validateContact("Валера", "Иванов", "", "забыл!!");
        });

        assertDoesNotThrow(() -> {
            Table.validateContact("Валера", "Иванов", "8 800 555 35 35", "");
        });
        assertDoesNotThrow(() -> {
            Table.validateContact("Valera", "Ivanov", "", "8-800-555-35-35");
        });
        assertDoesNotThrow(() -> {
            Table.validateContact("Топ", "Ассист",
                    "+7 (926) 000-00-00", "");
        });
    }

    @Test
    void validateAndCreate() throws ContactPhoneException, ContactNameException,
            ContactSurnameException, ContactAlreadyExistException {
        Contact contact = Table.validateAndCreate("You", "Are", "Breathtaking",
                "000", "", "home", null, "Love this one!");

        assertEquals(contact.getName(), "You");
        assertEquals(contact.getSurname(), "Are");
        assertEquals(contact.getLastName(), "Breathtaking");
        assertEquals(contact.getMobilePhone(), "000");
        assertEquals(contact.getHomePhone(), "");
        assertEquals(contact.getAddress(), "home");
        assertNull(contact.getDateOfBirth());
        assertEquals(contact.getComment(), "Love this one!");
        // проверим ошибку при попытке добавить контакт с существующей комбинацией Имени, Фамилии и Отчества
        assertThrows(ContactAlreadyExistException.class, () -> {
            Table.validateAndCreate("Test", "Test", "Test",
                    "000", "", "home", null, "Love this one!");
        });
    }

    @Test
    void validateAndEdit() throws ContactPhoneException, ContactNameException,
            ContactSurnameException, ContactAlreadyExistException {
        Contact contact = new Contact("You", "Are", "Breathtaking",
                "000", "", "home", null, "Love this one!");

        Table.validateAndEdit(contact, "I", "Am", "Breathtaking",
                "999", "", "won`t say!", null, "cool!");

        assertEquals(contact.getName(), "I");
        assertEquals(contact.getSurname(), "Am");
        assertEquals(contact.getLastName(), "Breathtaking");
        assertEquals(contact.getMobilePhone(), "999");
        assertEquals(contact.getHomePhone(), "");
        assertEquals(contact.getAddress(), "won`t say!");
        assertNull(contact.getDateOfBirth());
        assertEquals(contact.getComment(), "cool!");
        // проверим ошибку при попытке изменить контакт на существующую комбинацию Имени, Фамилии и Отчества
        assertThrows(ContactAlreadyExistException.class, () -> {
            Table.validateAndEdit(contact, "Test", "Test", "Test",
                    "999", "", "won`t say!", null, "cool!");
        });
    }

}