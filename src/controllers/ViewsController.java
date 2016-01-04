/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Book;
import models.Reader;
import models.Using;
import org.hibernate.Query;
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
    private ObservableList<Using> usingData = FXCollections.observableArrayList();
    private HashMap<String, Reader> mapReaderCB = new HashMap<String, Reader>();
    
    @FXML
    private TextField authorTextField, titleTextField, genreTextField, ageTextField, QuantityTextField, 
            nameTextField, familyNameTextField, fatherNameTextField, ageTextField2,
            findGenreTextField, findAuthorTextField;
    @FXML
    private ComboBox<String> cbReaders, cbReaders2;
    @FXML
    private DatePicker datePicker;
    
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
    
    //Таблица возврата
    @FXML
    private TableView<Using> tableBooksReader;
    @FXML
    private TableColumn<Using, Integer> idUsingCol;
    @FXML
    private TableColumn<Using, String> authorUsingCol;
    @FXML
    private TableColumn<Using, String> titleUsingCol;
    @FXML
    private TableColumn<Using, String> genreUsingCol;
    @FXML
    private TableColumn<Using, Integer> ageUsingCol;
    @FXML
    private TableColumn<Using, Date> dateUsingCol;
    
    /**
     * Обработчик события добавления книги
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
            
            //Обновление вида
            bookData = FXCollections.observableArrayList(this.getBooksList());
            tableBooks.setItems(bookData);
            tableBooks.refresh();
        }
        catch (NumberFormatException ex){
            showError(ERROR_FIELD_FORMAT);
        }
    }
    /**
     * Обработчик события удаления книги
     * @param event событие
     */
    @FXML
    void deleteBook(ActionEvent event) {
        System.out.println("deleteBook");
        int selectedIndex = tableBooks.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Book b = bookData.get(selectedIndex);
            
            deleteObjectFromDB(b);
            //TODO: Работает только если активно каскадное удаление, добавить в анотации
            
            //Обновление вида
            bookData.remove(b);
            tableBooks.refresh();
            
            readerData = FXCollections.observableArrayList(this.getReadersList());
            tableReaders.setItems(readerData);
            tableReaders.refresh();
            
            String s = cbReaders2.getValue();
            if (s != null) {
                Reader r = mapReaderCB.get(s);
                usingData = FXCollections.observableArrayList(getUsingListByReader(r));
            } else {
                usingData = FXCollections.observableArrayList();
            }
            tableBooksReader.setItems(usingData);
            tableBooksReader.refresh();
        } else {
            showError(ERROR_NON_SELECTED);
        }
    }
    /**
     * Обработчик события добавления читателя
     * @param event событие
     */
    @FXML
    void addReader(ActionEvent event) {
        try{
            System.out.println("addReader");
            Reader reader = new Reader();
            reader.setName(nameTextField.getText());
            reader.setFamilyName(familyNameTextField.getText());
            reader.setFatherName(fatherNameTextField.getText());
            reader.setAge(Integer.parseInt(ageTextField2.getText()));
            
            //Сохраняет .. в БД
            saveObjectToDB(reader);
            
            //Обновление вида
            readerData = FXCollections.observableArrayList(this.getReadersList());
            tableReaders.setItems(readerData);
            tableReaders.refresh();
            initReaderComboBox();
        }
        catch (NumberFormatException ex){
            showError(ERROR_FIELD_FORMAT);
        }
    }
    /**
     * Обработчик события удаления читателя
     * @param event событие
     */
    @FXML
    void deleteReader(ActionEvent event) {
        System.out.println("deleteReader");
        int selectedIndex = tableReaders.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Reader r = readerData.get(selectedIndex);
            deleteObjectFromDB(r);
            
            //Обновление вида
            readerData = FXCollections.observableArrayList(this.getReadersList());
            tableReaders.setItems(readerData);
            tableReaders.refresh();
            initReaderComboBox();
            cbReaders.getSelectionModel().clearSelection();
            cbReaders2.getSelectionModel().clearSelection();
            usingData = FXCollections.observableArrayList();
            tableBooksReader.setItems(usingData);
            tableBooksReader.refresh();
        } else {
            showError(ERROR_NON_SELECTED);
        }
    }
    /**
     * Обновляет вкладку
     */
    @FXML
    void updateReadersTab() {
        System.out.println("updateReadersTab");
        readerData = FXCollections.observableArrayList(this.getReadersList());
        tableReaders.setItems(readerData);
        tableReaders.refresh();
    }
    /**
     * Обработчик события добавления использования книги
     * @param event событие
     */
    @FXML
    void addUsing(ActionEvent event) {
        System.out.println("addUsing");
        String s = cbReaders.getValue();
        if (s != null) {
            int selectedIndex = tableBooks.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                LocalDate ld = datePicker.getValue();
                if (ld != null) {
                    Date d = new Date(ld.toEpochDay()*86400000);
                    Book b = bookData.get(selectedIndex);
                    Reader r = mapReaderCB.get(s);
                    Using using = new Using(b, r, d);
                    if (b.getQuantity() > getUsingListByBook(b).size()) {
                        Boolean bool = true;
                        List<Using> ul = getUsingListByReader(r);
                        for (Using ub : ul) {
                            if (Objects.equals(ub.getIdBook().getId(), b.getId())) {
                                bool = false;
                            }
                        }
                        if (bool) {
                            //Нужно гарантировать выполнение запроса сохранения объекта в первую очередь!!!
                            saveObjectToDB(using);
                            //refresh
                            usingData = FXCollections.observableArrayList(getUsingListByReader(r));
                            tableBooksReader.setItems(usingData);
                            tableBooksReader.refresh();
                            
                            if (cbReaders.getValue() == cbReaders2.getValue()) {
                                readerData = FXCollections.observableArrayList(this.getReadersList());
                                tableReaders.setItems(readerData);
                                tableReaders.refresh();
                            }
                        } else {
                            showError("У читателя есть эта книга"); 
                        }
                    } else {
                       showError("Книги нет в наличии"); 
                    }
                } else {
                    showError("Выберите дату");
                }
            } else {
                showError("Выберите книгу");
            }
        } else {
            showError("Выберите читателя");
        }
    }
    /**
     * Обработчик события удаления использования книги
     * @param event событие
     */
    @FXML
    void deleteUsing(ActionEvent event) {
        System.out.println("deleteUsing");
        String s = cbReaders2.getValue();
        if (s != null) {
            Reader r = mapReaderCB.get(s);
            int selectedIndex = tableBooksReader.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                Using using = usingData.get(selectedIndex);
                //TODO: Проверка наличия книг на руках
                deleteObjectFromDB(using);
                //refresh
                usingData.remove(using);
                tableBooksReader.refresh();
                readerData = FXCollections.observableArrayList(this.getReadersList());
                tableReaders.setItems(readerData);
                tableReaders.refresh();
                
                Calendar Current_Calendar = Calendar.getInstance();
                java.util.Date Current_Date = Current_Calendar.getTime();
                Date now = new Date(Current_Date.getTime());
                Long delta = now.getTime() - using.getDateReturn().getTime();
                System.out.println(delta);
                if (delta > 0) {
                    showError("Вы опаздали, ваш штраф: " + delta/86400000 + " руб.");
                }
            } else {
                showError("Выберите книгу");
            }
        } else {
            showError("Выберите читателя");
        }
    }
    /**
     * Обновляет вкладку
     */
    @FXML
    void updateUsingsTab() {
        System.out.println("updateUsingsTab");
        String s = cbReaders2.getValue();
        if (s != null) {
            Reader r = mapReaderCB.get(s);
            usingData = FXCollections.observableArrayList(getUsingListByReader(r));
        } else {
            usingData = FXCollections.observableArrayList();
        }
        tableBooksReader.setItems(usingData);
        tableBooksReader.refresh();
    }
    /**
     * Обработчик события выбора читателя из выпадающего списка 
     * (генерирует список книг на руках выбранного читателя)
     * @param event событие
     */
    @FXML
    void selectReader2(ActionEvent event) {
        System.out.println("selectReader2");
        String s = cbReaders2.getValue();
        if (s != null) {
            usingData = FXCollections.observableArrayList(getUsingListByReader(mapReaderCB.get(s)));
        } else {
            usingData = FXCollections.observableArrayList();
        }
        
        //Обновление вида
        tableBooksReader.setItems(usingData);
    }
    /**
     * Обработчик события выбора читателя 
     * (ничего не делает)
     * @param event событие
     */
    @FXML
    void selectReader(ActionEvent event) {
    }
    /**
     * Обработчик события кнопки поиска всех книг
     * (генерирует список всех книг в библиотеке)
     * @param event событие
     */
    @FXML
    void findAll(ActionEvent event) {
        bookData = FXCollections.observableArrayList(this.getBooksList());
        
        //Обновление вида
        tableBooks.setItems(bookData);
    }
    /**
     * Обработчик события кнопки поиска книг по автору
     * (генерирует список книг удовлетворяющих полю автор)
     * @param event событие
     */
    @FXML
    void findByAuthor(ActionEvent event) {
        String author = findAuthorTextField.getText();
        String s = cbReaders.getValue();
        if (s != null) {
            Integer age = mapReaderCB.get(s).getAge();
            bookData = FXCollections.observableArrayList(this.getBookListByAuthor(age, author));
            
            //Обновление вида
            tableBooks.setItems(bookData);
        } else {
            showError("Выберите читателя");
        }
    }
    /**
     * Обработчик события кнопки поиска книг по жанру
     * (генерирует список книг удовлетворяющих полю жанр)
     * @param event событие
     */
    @FXML
    void findByGenre(ActionEvent event) {
        String genre = findGenreTextField.getText();
        String s = cbReaders.getValue();
        if (s != null) {
            Integer age = mapReaderCB.get(s).getAge();
            bookData = FXCollections.observableArrayList(this.getBookListByGenre(age, genre));
            tableBooks.setItems(bookData);
        } else {
            showError("Выберите читателя");
        }

    }
    //===================================================================
    /**
     * Получает весь список использования книг читателями
     * @return List<Using> список использования
     */
    private List<Using> getUsingList() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        List<Using> using = session.createCriteria(Using.class).list();
        session.getTransaction().commit();
        session.close();
        return using;
    }
    /**
     * Получает список использования книг определенным читателем
     * @param reader
     * @return List<Using> список использования
     */
    private List<Using> getUsingListByReader(Reader reader) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Using using where :reader = using.idReader")
            .setInteger("reader", reader.getId());
        List<Using> using = query.list();
        session.getTransaction().commit();
        session.close();
        return using;
    }
    /**
     * Получает список использования определенной книги читателями
     * @param book
     * @return List<Using> список использования
     */
    private List<Using> getUsingListByBook(Book book) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Using using where :book = using.idBook")
            .setInteger("book", book.getId());
        List<Using> using = query.list();
        session.getTransaction().commit();
        session.close();
        return using;
    }
    /**
     * Получает список книг
     * @return список книг
     */
    private List<Book> getBooksList(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        List<Book> books = session.createCriteria(Book.class).list();
        session.getTransaction().commit();
        session.close();
        return books;
    }
    /**
     * Получает список книг по автору и возрасту
     * @param age
     * @param author
     * @return список книг
     */
    public List<Book> getBookListByAuthor(Integer age, String author) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Book book where :age >= book.age and :author like book.author")
            .setInteger("age", age)
            .setString("author", author);
        List<Book> books = query.list();
        session.getTransaction().commit();
        session.close();
        return books;
    }
    /**
     * Получает список книг по жанру и возрасту
     * @param age
     * @param genre
     * @return список книг
     */
    public List<Book> getBookListByGenre(Integer age, String genre) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Book book where :age >= book.age and :genrе like book.genre")
            .setInteger("age", age)
            .setString("genrе", genre);
        List<Book> books = query.list();
        session.getTransaction().commit();
        session.close();
        return books;
    }
    /**
     * Получает список читателей
     * @return список читателей
     */
    private List<models.Reader> getReadersList(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        List<models.Reader> readers = session.createCriteria(models.Reader.class).list();
        session.getTransaction().commit();
        session.close();
        return readers;
    }
    
    //===================================================================
    /**
     * Сохраняет книгу в БД
     */
    private void saveObjectToDB(Object o){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(o);
        session.getTransaction().commit();
        session.close();
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
    
    //===================================================================
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
     * @return true/false
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
    
    //===================================================================
    /**
     * Заполняет выпадающие списки читателями
     * @return void
     */
    private void initReaderComboBox() {
        if (readerData == null)
            return;
        Iterator iterator = readerData.iterator();
        ObservableList<String> readerList = FXCollections.observableArrayList();
        while (iterator.hasNext()) {
            Reader r = (Reader) iterator.next();
            String s = r.getId() + ": " + r.getFamilyName() + " " + r.getName() + " " + r.getFatherName();
            readerList.add(s);
            mapReaderCB.put(s, r);
        }
        cbReaders2.setItems(readerList);
        cbReaders.setItems(readerList);
    }
    /**
     * Инициализирует стартовыми значениями компоненты приложения
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Book
        idBook.setCellValueFactory(new PropertyValueFactory<>("id"));
        author.setCellValueFactory(new PropertyValueFactory<>("author"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        genre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        age.setCellValueFactory(new PropertyValueFactory<>("age"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
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
        tableReaders.setItems(readerData);
        
        //Using
        idUsingCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorUsingCol.setCellValueFactory(x -> new ReadOnlyObjectWrapper(x.getValue().getIdBook().getAuthor()));
        titleUsingCol.setCellValueFactory(x -> new ReadOnlyObjectWrapper(x.getValue().getIdBook().getTitle()));
        genreUsingCol.setCellValueFactory(x -> new ReadOnlyObjectWrapper(x.getValue().getIdBook().getGenre()));
        ageUsingCol.setCellValueFactory(x -> new ReadOnlyObjectWrapper(x.getValue().getIdBook().getAge()));
        dateUsingCol.setCellValueFactory(new PropertyValueFactory<>("dateReturn"));
        tableBooksReader.setItems(usingData);
        
        //init combobox
        initReaderComboBox();        
    }
}
