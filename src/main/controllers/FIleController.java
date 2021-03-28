package controllers;

import javafx.scene.control.Alert;
import model.Contact;
import model.Table;
import model.exceptions.ContactAlreadyExistException;
import model.exceptions.ContactNameException;
import model.exceptions.ContactPhoneException;
import model.exceptions.ContactSurnameException;

import java.io.*;

import java.time.LocalDate;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FIleController {
    /**
     * Метод для вывода AlertDialog`а о строках, которые не смогли быть прочитаны
     * @param indexes индексы плохих строк с сообщениями об ошибках
     */
    private void showBadElems(List<String> indexes) {
        if (!indexes.isEmpty()) {
            var msg = indexes.stream().map(Object::toString)
                    .collect(Collectors.joining(", "));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Warning");
            alert.setHeaderText("Not all contacts were imported due to non-compliance with requirements");
            alert.setContentText("Indexes of bad contacts: " + msg);

            alert.show();
        }
    }

    /**
     * Метод для импорта значений из файла в таблицу
     * @param file выбранный пользователем файл
     */
    public void fileChooserImport(File file) {
        List<Contact> contacts = new ArrayList<>();
        List<String> badElems = new ArrayList<>();
        if (file == null || !file.exists()) // если файла нет, то и импортить нечего...
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            int ind = 1;

            while (line != null) {
                String[] attributes = line.split(";", -1);
                // если аттрибутов не 8 - то данные уже неверные
                if (attributes.length != 8)
                    return;

                LocalDate date;
                if (attributes[6].isBlank() || attributes[6].isEmpty()) {
                    date = null;
                } else {
                    try { // если на вход подается некорректный формат даты - просто зануляем его
                        date = LocalDate.parse(attributes[6]);
                    } catch (DateTimeParseException e) {
                        date = null;
                    }
                }

                try {
                    contacts.add(Table.validateAndCreate(attributes[1], attributes[0], attributes[2], attributes[3],
                            attributes[4], attributes[5], date, attributes[7])); // пытаемся валидировать значение
                } catch (ContactSurnameException | ContactAlreadyExistException |
                        ContactNameException | ContactPhoneException e) {
                    badElems.add(ind + ": " + e.getMessage()); // записываем индекс и ошибку, если валидация не удалась
                }

                line = br.readLine();
                ind++;
            }

            Table.mainData.addAll(contacts); // записываем валидированные значения в таблицу
            showBadElems(badElems); // показываем AlertDialog о значениях, которые не прошли валидацию
        } catch (IOException e) {
            System.console().printf(e.getMessage());
        }
    }

    /***
     * Метод для экспорта данных из таблицы в файл
     * @param file выбранный пользователем файл
     */
    public void fileChooserExport(File file) {
        if (file == null) // если файл не выбрали, то экспортить некуда!
            return;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (var contact: Table.mainData) {
                String date = "";
                if (contact.getDateOfBirth() != null) // костыль, чтобы заменить null значение даты на пустую строку
                    date = contact.getDateOfBirth().toString();

                String line = contact.getSurname() + ';' + contact.getName() + ';' + contact.getLastName() + ';' +
                        contact.getMobilePhone() + ';' + contact.getHomePhone() + ';' + contact.getAddress() + ';' +
                        date + ';' + contact.getComment();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.console().printf(e.getMessage());
        }
    }
}
