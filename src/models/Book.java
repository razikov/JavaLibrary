/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;


/**
 *
 * @author razikov
 */
public class Book {
    private Integer id;
    private String author;
    private String title;
    private String isbn;
    private String genre;
    private Integer age;
    private Integer quantity;
    
    public Book() {
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getAuthor() {
        return this.author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getIsbn() {
        return this.isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public String getGenre() {
        return this.genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public Integer getAge() {
        return this.age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public Integer getQuantity() {
        return this.quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    /**
     * Проверка модели
     * @return true, если модель прошла проверку и false в противном случае
     */
    public Boolean validate() {
        boolean valid = true;
        
        valid &= author != null && author.length() <= 255;
        valid &= title != null && title.length() <= 255;
        valid &= genre != null && genre.length() <= 255;
        valid &= age != null && age >= 3 && age <= 150;
        valid &= quantity != null && quantity >= 0 && quantity <= 10000;
        
        return valid;
    }
}
