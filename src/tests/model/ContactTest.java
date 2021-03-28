package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContactTest {

    @Test
    void testEquals() {
        // простая проверка нашего перегруженного метода .equals()
        Contact cntct1 = new Contact("Иванов", "Иван", "Иванович",
                "777", "888", "address", null, "");
        Contact cntct2 = new Contact("Иванов", "Иван", "Иванович",
                "888", "777", "address2", null, "");
        Contact cntct3 = new Contact("Иванов", "Петр", "Иванович",
                "777", "888", "address", null, "");

        assertEquals(cntct1, cntct2);
        assertNotEquals(cntct1, cntct3);
    }
}