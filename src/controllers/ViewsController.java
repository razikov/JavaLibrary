/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Book;
import org.hibernate.Session;
import util.HibernateUtil;

/**
 *
 * @author razikov
 */
public class ViewsController implements Initializable {
    
    private ObservableList<Book> bookData = FXCollections.observableArrayList(this.getBooksList());
    
    @FXML
    private Label label;
    @FXML
    private Button addBookButton, deleteBookButton;
    //Таблица
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
    
    
    @FXML
    private void addBook(ActionEvent event) {
        System.out.println("addB");
    }
    
    @FXML
    private void deleteBook(ActionEvent event) {
        System.out.println("deleteB");
    }
    
    //READER
    @FXML
    private void addReader(ActionEvent event) {
        System.out.println("addR");
    }
    
    @FXML
    private void deleteReader(ActionEvent event) {
        System.out.println("deleteR");
    }
    
    /**
     * Получает список книг из базы данных
     * @return список книг
     */
    public List<models.Book> getBooksList(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        List<models.Book> booksDB = session.createCriteria(models.Book.class).list();
        session.getTransaction().commit();
        session.close();
        return booksDB;
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
