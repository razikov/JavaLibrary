/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
4. Написать программу, моделирующую учет книг в библиотеке. 
Сведения о каждой книге содержат: фамилию и инициалы автора, название, номер, жанр, возраст читателя, количество книг в библиотеке. 
Сведения о каждом читателе содержат: фамилию, имя, отчество, возраст, список книг с указанием даты возврата. 
Программа должна создавать список книг и список читателей. 
Начальное формирование данных осуществляется (НЕ из файла), при вводе в программу. 
С помощью меню необходимо обеспечить следующие функции:

a) добавление книги или читателя;
b) удаление книги или читателя;
c) подбор книги для данного читателя по заданному жанру или автору (с учетом возраста читателя);
d) выдачу книги читателю, при этом должно учитываться ко-личество экземпляров книги, оставшихся в библиотеке;
e) возврат книги, за не вовремя сданную книгу рассчитывает-ся штраф (например, 1 рубль за каждый день);
f) вывод списка книг, упорядоченного по фамилии автора;
g) вывод списка книг, упорядоченного по названию.
*/
package library;

import java.util.Date;
import java.util.Set;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 *
 * @author razikov
 */
public class Library extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        SessionFactory sessionFactory;
        
        Configuration configuration = new Configuration();
        configuration.configure("library/hibernate.cfg.xml");

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Book book = new Book();
        book.setId(1);
        book.setAuthor("author");
        book.setTitle("title");
        book.setIsbn("1abs3");
        book.setGenre("genre");
        book.setAge(20);
        book.setQuantity(5);
        session.save(book);
        session.getTransaction().commit();
        session.close();
        
        Session session1 = sessionFactory.openSession();
        session1.beginTransaction();
        Book book1 = new Book();
        book1.setId(2);
        book1.setAuthor("author2");
        book1.setTitle("title2");
        book1.setIsbn("1abs3");
        book1.setGenre("genre2");
        book1.setAge(20);
        book1.setQuantity(3);
        session1.save(book1);
        session1.getTransaction().commit();
        session1.close();
        
        Session session2 = sessionFactory.openSession();
        session2.beginTransaction();
        Reader reader = new Reader();
        reader.setId(1);
        reader.setFName("author");
        reader.setLName("title");
        reader.setSName("1abs3");
        reader.setAge(20);
        Using u1 = new Using(1, 1, new Date());
        Using u2 = new Using(2, 1, new Date());
        Set<Using> list = null;
        list.add(u1);
        list.add(u2);
        reader.setList(list);
        session2.save(reader);
        session2.getTransaction().commit();
        session2.close();
        
        sessionFactory.close();
        
        launch(args);
                
    }
}