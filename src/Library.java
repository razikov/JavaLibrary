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

a) добавление книги или читателя;+
b) удаление книги или читателя;+
c) подбор книги для данного читателя по заданному жанру или автору (с учетом возраста читателя);
d) выдачу книги читателю, при этом должно учитываться ко-личество экземпляров книги, оставшихся в библиотеке;
e) возврат книги, за не вовремя сданную книгу рассчитывает-ся штраф (например, 1 рубль за каждый день);
f) вывод списка книг, упорядоченного по фамилии автора;+
g) вывод списка книг, упорядоченного по названию.+
*/


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author razikov
 */
public class Library extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("views/Aplication.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
