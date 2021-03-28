package controllers;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import model.Contact;
import model.Table;

import java.util.Date;

public class TableController {
    public TableView<Contact> viewTable;

    public TableController() {
        viewTable = new TableView<>();

        TableColumn<Contact, String> surnameColumn = new TableColumn<>("Surname");
        surnameColumn.setCellValueFactory(new PropertyValueFactory<Contact, String>("surname"));
        // столбец для вывода имени
        TableColumn<Contact, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<Contact, String>("name"));
        // столбец для вывода отчества
        TableColumn<Contact, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<Contact, String>("lastName"));
        // столбец для вывода мобильного телефона
        TableColumn<Contact, String> mobilePhoneColumn = new TableColumn<>("Mobile Phone");
        mobilePhoneColumn.setCellValueFactory(new PropertyValueFactory<Contact, String>("mobilePhone"));
        // столбец для вывода домашнего телефона
        TableColumn<Contact, String> homePhoneColumn = new TableColumn<>("Home Phone");
        homePhoneColumn.setCellValueFactory(new PropertyValueFactory<Contact, String>("homePhone"));
        // столбец для вывода адреса
        TableColumn<Contact, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<Contact, String>("address"));
        // столбец для вывода даты рождения
        TableColumn<Contact, Date> dobColumn = new TableColumn<>("Date of Birth");
        dobColumn.setCellValueFactory(new PropertyValueFactory<Contact, Date>("dateOfBirth"));
        // столбец для вывода заметок
        TableColumn<Contact, String> commentColumn = new TableColumn<>("Comment");
        commentColumn.setCellValueFactory(new PropertyValueFactory<Contact, String>("comment"));

        // добавляем столбец
        viewTable.getColumns().addAll(surnameColumn, nameColumn, lastNameColumn, mobilePhoneColumn,
                homePhoneColumn, addressColumn, dobColumn, commentColumn);

        viewTable.setItems(Table.mainData);
    }

    public void search(String filterWord) {
        viewTable.setItems(Table.getFilteredData(filterWord));
    }

    public void removeSelectedContact() {
        Table.mainData.remove(viewTable.getSelectionModel().getSelectedItem());
    }
}
