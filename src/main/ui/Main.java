package ui;

import controllers.FIleController;
import controllers.TableController;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.stage.WindowEvent;

import model.Contact;
import model.Table;
import model.exceptions.ContactAlreadyExistException;
import model.exceptions.ContactNameException;
import model.exceptions.ContactPhoneException;
import model.exceptions.ContactSurnameException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Main extends Application {
    private static Stage pStage;

    private static final BorderPane root = new BorderPane();
    private static final FileChooser fileChooser = new FileChooser();
    private static final FIleController fController = new FIleController();
    private static final TableController tController = new TableController();
    VBox filePage, helpPage;

    /**
     * Метод для выделения вью-объектов красной рамкой (не смог найти, как можно сделать это без css)
     * @param ctrl объект вьюхи, в нашем случае используются только TextField`ы
     */
    private void setErrorStyle(Control ctrl) {
        ctrl.setStyle("-fx-text-box-border: red; -fx-focus-color: red; -fx-border-color: red");
    }

    /**
     * Метод для возвращения обычной рамки для вью-объектов
     * @param ctrl объект вьюхи, в нашем случае используются только TextField`ы
     */
    private void setNormalStyle(Control ctrl) {
        ctrl.setStyle("");
    }

    /**
     * Метод для создания сцены для двух похожих модальных окон - окна добавления и окна редактирования контакта
     * @param contactInfo список горизонтальных блоков состоящих из лейбла и филда для записи информации
     * @param comment филд для комментария, для него не нужен лейбл, поэтому просто идет отдельно
     * @param btn кнопка действия
     * @param errorLabel лейбл для вывода ошибок
     * @return готовая сцена
     */
    private Scene createScene(List<HBox> contactInfo, TextArea comment, Button btn, Label errorLabel) {
        BorderPane page = new BorderPane();

        for (var box: contactInfo) // выставляем растяжение для всех филдов
            HBox.setHgrow(box.getChildren().get(1), Priority.ALWAYS);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(contactInfo);
        vbox.getChildren().add(comment);
        vbox.getChildren().add(new HBox(btn));
        vbox.getChildren().add(errorLabel); // поле для вывода ошибки

        VBox.setVgrow(comment, Priority.ALWAYS); // вертикальное растяжение филда для комментария
        HBox.setHgrow(btn, Priority.SOMETIMES); // горизонтальное растяжение кнопки
        vbox.setSpacing(5);

        page.setCenter(vbox);
        BorderPane.setMargin(vbox, new Insets(5, 5, 5, 5)); // отступы от экрана

        return new Scene(page, 300, 400);
    }

    /**
     * Метод для создания модальной страницы добавления контакта
     */
    private void createAddPage() {
        Stage addDialogPage = new Stage();
        addDialogPage.setResizable(false);
        addDialogPage.setTitle("Add contact");
        addDialogPage.initModality(Modality.WINDOW_MODAL);
        addDialogPage.initOwner(pStage);

        List<HBox> contactInfo = Arrays.asList(new HBox(new Label("Name"), new Region(), new TextField()),
                new HBox(new Label("Surname"), new Region(), new TextField()),
                new HBox(new Label("Last name"), new Region(), new TextField()),
                new HBox(new Label("Mobile phone"), new Region(), new TextField()),
                new HBox(new Label("Home Phone"), new Region(), new TextField()),
                new HBox(new Label("Address"), new Region(), new TextField()),
                new HBox(new Label("Date of birth"), new Region(), new DatePicker())
        );
        TextArea comment = new TextArea();
        Button addButton = new Button("Add");
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#ff0000")); // красный цвет текста

        addButton.setMaxWidth(Double.MAX_VALUE); // кнопочка с логикой добавления
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
                returnNormalStyle(errorLabel, contactInfo);

                try {
                    Table.mainData.add(Table.validateAndCreate(((TextField)contactInfo.get(0).getChildren().get(2)).getText(),
                            ((TextField)contactInfo.get(1).getChildren().get(2)).getText(),
                            ((TextField)contactInfo.get(2).getChildren().get(2)).getText(),
                            ((TextField)contactInfo.get(3).getChildren().get(2)).getText(),
                            ((TextField)contactInfo.get(4).getChildren().get(2)).getText(),
                            ((TextField)contactInfo.get(5).getChildren().get(2)).getText(),
                            ((DatePicker)contactInfo.get(6).getChildren().get(2)).getValue(),
                            comment.getText()));
                } catch (ContactNameException e) { // при ошибках выделяем поля с ошибкой красной рамкой и выводим сообщение
                    errorLabel.setText(e.getMessage());
                    setErrorStyle(((TextField)contactInfo.get(0).getChildren().get(2)));
                } catch (ContactSurnameException e) {
                    errorLabel.setText(e.getMessage());
                    setErrorStyle(((TextField)contactInfo.get(1).getChildren().get(2)));
                } catch (ContactPhoneException e) {
                    errorLabel.setText(e.getMessage());
                    setErrorStyle(((TextField)contactInfo.get(3).getChildren().get(2)));
                    setErrorStyle(((TextField)contactInfo.get(4).getChildren().get(2)));
                } catch (ContactAlreadyExistException e) {
                    errorLabel.setText(e.getMessage());
                }
            }
        });

        Scene scene = createScene(contactInfo, comment, addButton, errorLabel);
        addDialogPage.setScene(scene);
        addDialogPage.show();
    }

    /**
     * Метод для создания модальной страницы редактирования контакта
     */
    private void createEditPage() {
        Stage addDialogPage = new Stage();
        addDialogPage.setResizable(false);
        addDialogPage.setTitle("Edit contact");
        addDialogPage.initModality(Modality.WINDOW_MODAL);
        addDialogPage.initOwner(pStage);

        Contact contact = tController.viewTable.getSelectionModel().getSelectedItem();
        if (contact == null)
            return; // выход, если не вышло прочесть выделенную строку

        List<HBox> contactInfo = Arrays.asList(
                new HBox(new Label("Name"), new Region(), new TextField(contact.getName())),
                new HBox(new Label("Surname"), new Region(), new TextField(contact.getSurname())),
                new HBox(new Label("Last name"), new Region(), new TextField(contact.getLastName())),
                new HBox(new Label("Mobile phone"), new Region(), new TextField(contact.getMobilePhone())),
                new HBox(new Label("Home Phone"), new Region(), new TextField(contact.getHomePhone())),
                new HBox(new Label("Address"), new Region(), new TextField(contact.getAddress())),
                new HBox(new Label("Date of birth"), new Region(), new DatePicker(contact.getDateOfBirth()))
        );
        TextArea comment = new TextArea(contact.getComment());
        Button saveButton = new Button("Save");
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#ff0000")); // красный цвет текста

        saveButton.setMaxWidth(Double.MAX_VALUE); // кнопочка с логикой редактирования
        saveButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent actionEvent) {
                returnNormalStyle(errorLabel, contactInfo);

                try {
                    Table.validateAndEdit(contact, ((TextField)contactInfo.get(0).getChildren().get(2)).getText(),
                            ((TextField)contactInfo.get(1).getChildren().get(2)).getText(),
                            ((TextField)contactInfo.get(2).getChildren().get(2)).getText(),
                            ((TextField)contactInfo.get(3).getChildren().get(2)).getText(),
                            ((TextField)contactInfo.get(4).getChildren().get(2)).getText(),
                            ((TextField)contactInfo.get(5).getChildren().get(2)).getText(),
                            ((DatePicker)contactInfo.get(6).getChildren().get(2)).getValue(),
                            comment.getText());
                    // рефреш таблицы, ибо изменение элемента Observable списка не затриггерит ивент на авто-рефреш
                    tController.viewTable.refresh();
                } catch (ContactNameException e) { // при ошибках выделяем поля с ошибкой красной рамкой и выводим сообщение
                    errorLabel.setText(e.getMessage());
                    setErrorStyle(((TextField)contactInfo.get(0).getChildren().get(2)));
                } catch (ContactSurnameException e) {
                    errorLabel.setText(e.getMessage());
                    setErrorStyle(((TextField)contactInfo.get(1).getChildren().get(2)));
                } catch (ContactPhoneException e) {
                    setErrorStyle(((TextField)contactInfo.get(3).getChildren().get(2)));
                    setErrorStyle(((TextField)contactInfo.get(4).getChildren().get(2)));
                    errorLabel.setText(e.getMessage());
                } catch (ContactAlreadyExistException e) {
                    errorLabel.setText(e.getMessage());
                }
            }
        });

        Scene scene = createScene(contactInfo, comment, saveButton, errorLabel);
        addDialogPage.setScene(scene);
        addDialogPage.show();
    }

    /**
     * Метод для возвращения первоначального вида страницам добавления/редактирования
     * @param errorLabel лейбл ошибки
     * @param contactInfo филды контакта
     */
    private void returnNormalStyle(Label errorLabel, List<HBox> contactInfo) {
        errorLabel.setText("");
        setNormalStyle(((TextField)contactInfo.get(0).getChildren().get(2)));
        setNormalStyle(((TextField)contactInfo.get(1).getChildren().get(2)));
        setNormalStyle(((TextField)contactInfo.get(3).getChildren().get(2)));
        setNormalStyle(((TextField)contactInfo.get(4).getChildren().get(2)));
    }

    /**
     * Метод для создания верхнего тулбара
     * @return готовый тулбар
     */
    public ToolBar createToolBar() {
        // создание кнопочек и их логики
        Button filePageBtn = new Button("File");
        filePageBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent actionEvent) {
                root.setCenter(filePage);
            }
        });

        MenuItem importItem = new MenuItem("import");
        importItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent actionEvent) {
                Stage importPage = new Stage();
                importPage.setResizable(false);
                importPage.setTitle("Import data");
                importPage.initModality(Modality.WINDOW_MODAL);
                importPage.initOwner(pStage);

                fController.fileChooserImport(fileChooser.showOpenDialog(importPage));
            }
        });
        MenuItem exportItem = new MenuItem("export");
        exportItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent actionEvent) {
                Stage exportPage = new Stage();
                exportPage.setResizable(false);
                exportPage.setTitle("Import data");
                exportPage.initModality(Modality.WINDOW_MODAL);
                exportPage.initOwner(pStage);

                fController.fileChooserExport(fileChooser.showSaveDialog(exportPage));
            }
        });
        MenuButton settingsPageBtn = new MenuButton("Settings", null, importItem, exportItem);

        Button resPageBtn = new Button("Help");
        resPageBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent actionEvent) {
                root.setCenter(helpPage);
            }
        });

        return new ToolBar(filePageBtn, new Separator(), settingsPageBtn, new Separator(), resPageBtn);
    }

    /**
     * Метод для создания нижней панели для работы с таблицей
     * @return горизонтальный бокс со всеми элементами
     */
    public HBox createBottomPanel() {
        // создание кнопочек и их логики
        Button addButton = new Button("Add");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Button searchButton = new Button("Search");

        TextField textField = new TextField();

        addButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent actionEvent) {
                createAddPage();
            }
        });

        editButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent actionEvent) {
                createEditPage();
            }
        });

        deleteButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent actionEvent) {
                tController.removeSelectedContact();
            }
        });

        searchButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent actionEvent) {
                tController.search(textField.getText());
            }
        });

        HBox hBox = new HBox(addButton, editButton, deleteButton, textField, searchButton);
        hBox.setSpacing(3);
        HBox.setHgrow(textField, Priority.ALWAYS);
        textField.setMaxWidth(400);

        // черная магия с растяжением, чтобы все смотрелось красиво при изменении размера экрана
        HBox.setHgrow(addButton, Priority.SOMETIMES);
        HBox.setHgrow(editButton, Priority.SOMETIMES);
        HBox.setHgrow(deleteButton, Priority.SOMETIMES);
        HBox.setHgrow(searchButton, Priority.SOMETIMES);
        addButton.setMaxWidth(Double.MAX_VALUE);
        editButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        searchButton.setMaxWidth(Double.MAX_VALUE);

        return hBox;
    }

    /**
     * Метод для создания страницы справки :)
     */
    private void createHelpPage() {
        HBox hBox = new HBox(new Region(),
                new Label("────────▄──────────────▄\n" +
                        "────────▌▒█───────────▄▀▒▌\n" +
                        "────────▌▒▒▀▄───────▄▀▒▒▒▐\n" +
                        "───────▐▄▀▒▒▀▀▀▀▄▄▄▀▒▒▒▒▒▐\n" +
                        "─────▄▄▀▒▒▒▒▒▒▒▒▒▒▒█▒▒▄█▒▐\n" +
                        "───▄▀▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▀██▀▒▌\n" +
                        "──▐▒▒▒▄▄▄▒▒▒▒▒▒▒▒▒▒▒▒▒▀▄▒▒▌\n" +
                        "──▌▒▒▐▄█▀▒▒▒▒▄▀█▄▒▒▒▒▒▒▒█▒▐\n" +
                        "─▐▒▒▒▒▒▒▒▒▒▒▒▌██▀▒▒▒▒▒▒▒▒▀▄▌\n" +
                        "─▌▒▀▄██▄▒▒▒▒▒▒▒▒▒▒▒░░░░▒▒▒▒▌\n" +
                        "_▌▀▐▄█▄█▌▄▒▀▒▒▒▒▒▒░░░░░░▒▒▒▐\n" +
                        "▐▒▀▐▀▐▀▒▒▄▄▒▄▒▒▒▒▒░░░░░░▒▒▒▒▌\n" +
                        "▐▒▒▒▀▀▄▄▒▒▒▄▒▒▒▒▒▒░░░░░░▒▒▒▐\n" +
                        "─▌▒▒▒▒▒▒▀▀▀▒▒▒▒▒▒▒▒░░░░▒▒▒▒▌\n" +
                        "─▐▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▐\n" +
                        "──▀▄▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▄▒▒▒▒▌\n" +
                        "────▀▄▒▒▒▒▒▒▒▒▒▒▄▄▄▀▒▒▒▒▄▀\n" +
                        "───▐▀▒▀▄▄▄▄▄▄▀▀▀▒▒▒▒▒▄▄▀\n" +
                        "──▐▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▀▀"), new Region());
        HBox.setHgrow(hBox.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(hBox.getChildren().get(2), Priority.ALWAYS);

        helpPage = new VBox(new Label("Creator: Sokolovskiy Vatslav, BSE191\n" +
                "Check this handsome doge:\n"), hBox);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("PhoneBook App");
        pStage = primaryStage;

        ToolBar toolBar = createToolBar(); // создаем тулбар

        filePage = new VBox(tController.viewTable, createBottomPanel()); // создаем страницу File
        createHelpPage(); // создаем страницу Help
        VBox.setVgrow(tController.viewTable, Priority.ALWAYS);

        File appData = new File("appData.csv");
        if (appData.exists()) // считываем данные с предыдущей сессии, если они есть
            fController.fileChooserImport(appData);

        root.setTop(toolBar);
        root.setCenter(filePage);

        Scene scene = new Scene(root, 960, 600);
        pStage.setMinHeight(600);
        pStage.setMinWidth(960);
        // волшебный ивент, который позволяет сохранять данные при выходе из приложения
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            @Override public void handle(WindowEvent actionEvent) {
                fController.fileChooserExport(appData);
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
