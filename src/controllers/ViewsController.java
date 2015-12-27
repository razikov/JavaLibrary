/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Book;
import models.Reader;
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
    private Book book = new Book();
    private Reader reader = new Reader();
    
    @FXML
    private TextField authorTextField, titleTextField, genreTextField, ageTextField, QuantityTextField;
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
    private TableColumn<Reader, Integer> idReader;
    @FXML
    private TableColumn<Reader, String> fname;
    @FXML
    private TableColumn<Reader, String> lname;
    @FXML
    private TableColumn<Reader, String> oname;
    @FXML
    private TableColumn<Reader, Integer> ageR;
    @FXML
    private TableColumn<Reader, Integer> list;
    
    /**
     * Обработчик события сохранения новой книги,
     * валидирует данные введенные в поля.
     * @param event событие
     */
    @FXML
    void addBook(ActionEvent event) {
        try{
            System.out.println("addBook");
            book.setAuthor(authorTextField.getText());
            book.setTitle(titleTextField.getText());
            book.setGenre(genreTextField.getText());
            book.setAge(Integer.parseInt(ageTextField.getText()));
            book.setQuantity(Integer.parseInt(QuantityTextField.getText()));
            
            if(!book.validate()) {
                showError(ERROR_FIELD_FORMAT);
            }
            //Сохраняет книгу в БД
            saveBookToDB();
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
            deleteBookFromDB(b);
            //Удаляет книгу из таблицы
            bookData.remove(b);
        } else {
            showError(ERROR_NON_SELECTED);
        }
    }
    
    //READER
    @FXML
    private void addReader(ActionEvent event) {
        System.out.println("addReader");
    }
    
    @FXML
    private void deleteReader(ActionEvent event) {
        System.out.println("deleteReader");
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
    private void saveBookToDB(){
        new Thread(() ->{
            try{
                Session session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                session.save(book);
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
    private void deleteBookFromDB(Book book){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(book);
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // устанавливаем тип и значение которое должно хранится в колонке
        idBook.setCellValueFactory(new PropertyValueFactory<Book, Integer>("id"));
        author.setCellValueFactory(new PropertyValueFactory<Book, String>("author"));
        title.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
        genre.setCellValueFactory(new PropertyValueFactory<Book, String>("genre"));
        age.setCellValueFactory(new PropertyValueFactory<Book, Integer>("age"));
        quantity.setCellValueFactory(new PropertyValueFactory<Book, Integer>("quantity"));
        // заполняем таблицу данными
        tableBooks.setItems(bookData);
    }
}
