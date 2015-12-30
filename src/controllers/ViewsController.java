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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Book;
import models.Reader;
import models.Using;
import models.UsingTable;
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
            tableBooks.refresh();
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
            //TODO: Проверка наличия книг на руках
            deleteObjectFromDB(b);
            //Удаляет книгу из таблицы
            bookData.remove(b);
            tableBooks.refresh();
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
            tableReaders.refresh();
            initReaderComboBox();
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
            //TODO: Проверка наличия книг на руках
            deleteObjectFromDB(r);
            //Удаляет книгу из таблицы
            readerData.remove(r);
            tableReaders.refresh();
            initReaderComboBox();
        } else {
            showError(ERROR_NON_SELECTED);
        }
    }
    
    @FXML
    private void addUsing() {
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
                    //TODO: Проверка наличия книг на руках
                    if (b.getQuantity() > getUsingBook(b).size()) {
                        saveObjectToDB(using);
                        //refresh
                        readerData.remove(r);
                        Set<Using> list = r.getList();
                        list.add(using);
                        r.setList(list);
                        readerData.add(r);
                        tableReaders.refresh();
                        String cbr = cbReaders2.getValue();
                        if (cbr != null & Objects.equals(r, mapReaderCB.get(cbr))) {
                            usingData.add(using);
                            tableBooksReader.refresh();
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
    
    @FXML
    private void deleteUsing() {
        System.out.println("deleteUsing");
        String s = cbReaders2.getValue();
        if (s != null) {
            Reader r = mapReaderCB.get(s);
            int selectedIndex = tableBooksReader.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                Using using = usingData.get(selectedIndex);
                deleteObjectFromDB(using);
                //refresh
                usingData.remove(using);
                tableBooksReader.refresh();
                //readerData.remove(r);
                Set<Using> list = new HashSet<Using>(getUsingListByReader(r));
                r.setList(list);
                //readerData.add(r);
                tableReaders.refresh();
                
                Calendar Current_Calendar = Calendar.getInstance();
                java.util.Date Current_Date = Current_Calendar.getTime();
                Date now = new Date(Current_Date.getTime());
                Long delta = now.getTime() - using.getDateReturn().getTime();
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
            
    @FXML
    private void selectReader2(ActionEvent event) {
        System.out.println("selectReader2");
        String s = cbReaders2.getValue();
        usingData = FXCollections.observableArrayList(getUsingListByReader(mapReaderCB.get(s)));
        tableBooksReader.setItems(usingData);
    }
    
    @FXML
    private void selectReader(ActionEvent event) {
    }
    
    @FXML
    private void findAll(ActionEvent event) {
        bookData = FXCollections.observableArrayList(this.getBooksList());
        tableBooks.setItems(bookData);
    }
    
    @FXML
    private void findByAuthor(ActionEvent event) {
        String author = findAuthorTextField.getText();
        String s = cbReaders.getValue();
        if (s != null) {
            Integer age = mapReaderCB.get(s).getAge();
            bookData = FXCollections.observableArrayList(this.getBookListByAuthor(age, author));
            tableBooks.setItems(bookData);
        } else {
            showError("Выберите читателя");
        }
    }
    
    @FXML
    private void findByGenre(ActionEvent event) {
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
    /**
     * Получает список книг читателя
     * @return список книг
     */
    private List<Using> getUsingList() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        List<Using> using = session.createCriteria(Using.class).list();
        session.getTransaction().commit();
        session.close();
        return using;
    }

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
    
    private List<Using> getUsingBook(Book book) {
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
     * заполняет таблицу возврата
     * @return список книг
     */
    private void initReturnUsingTable() {
        idUsingCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorUsingCol.setCellValueFactory(x -> new ReadOnlyObjectWrapper(x.getValue().getIdBook().getAuthor()));
        titleUsingCol.setCellValueFactory(x -> new ReadOnlyObjectWrapper(x.getValue().getIdBook().getTitle()));
        genreUsingCol.setCellValueFactory(x -> new ReadOnlyObjectWrapper(x.getValue().getIdBook().getGenre()));
        ageUsingCol.setCellValueFactory(x -> new ReadOnlyObjectWrapper(x.getValue().getIdBook().getAge()));
        dateUsingCol.setCellValueFactory(new PropertyValueFactory<>("dateReturn"));
        // заполняем таблицу данными
        tableBooksReader.setItems(usingData);
    }
    /**
     * Получает список книг из базы данных
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
     * Получает список книг из базы данных
     * @return список книг
     */
    public List<Book> getBookListByAuthor(Integer age, String author) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Book book where :age = book.age and :author like book.author")
            .setInteger("age", age)
            .setString("author", author);
        List<Book> books = query.list();
        session.getTransaction().commit();
        session.close();
        return books;
    }
    /**
     * Получает список книг из базы данных
     * @return список книг
     */
    public List<Book> getBookListByGenre(Integer age, String genre) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Book book where :age = book.age and :genrе like book.genre")
            .setInteger("age", age)
            .setString("genrе", genre);
        List<Book> books = query.list();
        session.getTransaction().commit();
        session.close();
        return books;
    }
    
    /**
     * Получает список читателей из базы данных
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
    private void initReaderData() {
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
        
        initReaderData();
        //Using
        initReturnUsingTable();
        //init combobox
        initReaderComboBox();        
    }
}
