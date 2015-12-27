/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Book;
import models.Reader;
import models.Using;
import org.hibernate.Session;
import util.HibernateUtil;

/**
 *
 * @author razikov
 */
public class ViewsController implements Initializable {
    
    private final String ERROR_FIELD_FORMAT = "В поля введены неверные значения";
    private final String ERROR_CONNECTION_FAIL = "Не удалось установить соединение с БД";
    private final String ERROR_NON_SELECTED = "Выберите запись в таблице";
    
    private ObservableList<Book> bookData = FXCollections.observableArrayList(this.getBooksList());
    private ObservableList<Reader> readerData = FXCollections.observableArrayList(this.getReadersList());
    private HashMap<String, Reader> mapReaderCB = new HashMap<String, Reader>();
    
    @FXML
    private TextField authorTextField, titleTextField, genreTextField, ageTextField, QuantityTextField, 
            nameTextField, familyNameTextField, fatherNameTextField, ageTextField2;
    @FXML
    private ComboBox<String> cbReaders, cbReaders2;
    
    @FXML
    private Button addBookButton, deleteBookButton;
    //Таблица книг
    @FXML
    private TableView<Book> tableBooks;
    @FXML
    private TableColumn<Book, Integer> idBook;
    @FXML
    private TableColumn<Book, String> author;
    @FXML
    private TableColumn<Book, String> title;
    @FXML
    private TableColumn<Book, String> genre;
    @FXML
    private TableColumn<Book, Integer> age;
    @FXML
    private TableColumn<Book, Integer> quantity;
    
    //Таблица читателей
    @FXML
    private TableView<Reader> tableReaders;
    @FXML
    private TableColumn<Reader, Integer> idReaderCol;
    @FXML
    private TableColumn<Reader, String> fatherNameCol;
    @FXML
    private TableColumn<Reader, String> familyNameCol;
    @FXML
    private TableColumn<Reader, String> nameCol;
    @FXML
    private TableColumn<Reader, Integer> ageRCol;
    @FXML
    private TableColumn<Reader, Set<Using>> listCol;
    
    /**
     * Обработчик события сохранения новой книги,
     * валидирует данные введенные в поля.
     * @param event событие
     */
    @FXML
    void addBook(ActionEvent event) {
        try{
            System.out.println("addBook");
            Book book = new Book();
            book.setAuthor(authorTextField.getText());
            book.setTitle(titleTextField.getText());
            book.setGenre(genreTextField.getText());
            book.setAge(Integer.parseInt(ageTextField.getText()));
            book.setQuantity(Integer.parseInt(QuantityTextField.getText()));
            
            if(!book.validate()) {
                showError(ERROR_FIELD_FORMAT);
            }
            //Сохраняет книгу в БД
            saveObjectToDB(book);
            //Добавляет книгу в таблицу
            bookData.add(book);
        }
        catch (NumberFormatException ex){
            showError(ERROR_FIELD_FORMAT);
        }
    }
    
    @FXML
    void deleteBook(ActionEvent event) {
        System.out.println("deleteBook");
        int selectedIndex = tableBooks.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            //Получает выделенную книгу
            Book b = bookData.get(selectedIndex);
            //Удаляет книгу из БД
            deleteObjectFromDB(b);
            //Удаляет книгу из таблицы
            bookData.remove(b);
        } else {
            showError(ERROR_NON_SELECTED);
        }
    }
    
    //READER
    @FXML
    private void addReader(ActionEvent event) {
        try{
            System.out.println("addReader");
            Reader reader = new Reader();
            reader.setName(nameTextField.getText());
            reader.setFamilyName(familyNameTextField.getText());
            reader.setFatherName(fatherNameTextField.getText());
            reader.setAge(Integer.parseInt(ageTextField2.getText()));
            
            //Сохраняет .. в БД
            saveObjectToDB(reader);
            //Добавляет .. в таблицу
            readerData.add(reader);
        }
        catch (NumberFormatException ex){
            showError(ERROR_FIELD_FORMAT);
        }
    }
    
    @FXML
    private void deleteReader(ActionEvent event) {
        System.out.println("deleteReader");
        int selectedIndex = tableReaders.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            //Получает 
            Reader r = readerData.get(selectedIndex);
            //Удаляет книгу из БД
            //!!esli net knig na rukah
            deleteObjectFromDB(r);
            //Удаляет книгу из таблицы
            readerData.remove(r);
        } else {
            showError(ERROR_NON_SELECTED);
        }
    }
    
    /**
     * Получает список книг из базы данных
     * @return список книг
     */
    private List<models.Book> getBooksList(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        List<models.Book> booksDB = session.createCriteria(models.Book.class).list();
        session.getTransaction().commit();
        session.close();
        return booksDB;
    }
    
    /**
     * Получает список читателей из базы данных
     * @return список читателей
     */
    private List<models.Reader> getReadersList(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        List<models.Reader> readersDB = session.createCriteria(models.Reader.class).list();
        session.getTransaction().commit();
        session.close();
        return readersDB;
    }

    /**
     * Сохраняет созданную книгу в БД
     */
    private void saveObjectToDB(Object o){
        new Thread(() ->{
            try{
                Session session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                session.save(o);
                session.getTransaction().commit();
                session.close();
            }
            catch (Exception ex){
                Platform.runLater(() -> showError(ERROR_CONNECTION_FAIL));
            }
        }).start();
    }
    
    /**
     * Удаляет книгу из базы данных
     * @param book книга для удаления
     */
    private void deleteObjectFromDB(Object o){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(o);
        session.getTransaction().commit();
        session.close();
    }
    
    /**
     * Показать диалог ошибки
     * @param message сообщение
     */
    public static void showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Показать диалог подтверждения
     * @param message сообщение
     * @return результат
     */
    public static boolean showConfirm(String message){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> clickedButton = alert.showAndWait();
        if(clickedButton.get() == ButtonType.OK)
            return true;
        return false;
    }
    /*
    private void initFindAction() {
        ObservableList<String> actionsList = FXCollections.observableArrayList(
                "Пополнить счет",
                "Снятие процентов",
                "Закрытие счета"
        );
        chooseActionMainPage.setItems(actionsList.sorted());
    }
    */
    private void initReaderComboBox() {
        
        if (readerData == null)
            return;
        Iterator iterator = readerData.iterator();
        ObservableList<String> readerList = FXCollections.observableArrayList();
        while (iterator.hasNext()) {
            Reader r = (Reader) iterator.next();
            String s = r.getFamilyName() + " " + r.getName()+" "+r.getFatherName();
            readerList.add(s);
            mapReaderCB.put(s, r);
        }
        
        cbReaders2.setItems(readerList);
        cbReaders.setItems(readerList);
    }
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Book
        idBook.setCellValueFactory(new PropertyValueFactory<>("id"));
        author.setCellValueFactory(new PropertyValueFactory<>("author"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        genre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        age.setCellValueFactory(new PropertyValueFactory<>("age"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        // заполняем таблицу данными
        tableBooks.setItems(bookData);
        
        //Reader
        idReaderCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        familyNameCol.setCellValueFactory(new PropertyValueFactory<>("familyName"));
        fatherNameCol.setCellValueFactory(new PropertyValueFactory<>("fatherName"));
        ageRCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        //TODO: lazy load
        listCol.setCellValueFactory((CellDataFeatures<Reader, Set<Using>> x) -> {
            Reader row = x.getValue();
            Set<Using> set = row.getList();
            if (set != null) {
                String t = "";
                for(Using u : set) {
                    String b = u.getIdBook().getId() + ", " + u.getIdBook().getAuthor() + ", " + u.getIdBook().getTitle();
                    String d = u.getDateReturn().toString();
                    t += b + "; " + d + "\n";
                }
                return new ReadOnlyObjectWrapper(t);
            } else {
                return new ReadOnlyObjectWrapper("");
            }
        });
        // заполняем таблицу данными
        tableReaders.setItems(readerData);
        
        //init combobox
        initReaderComboBox();
    }
}
