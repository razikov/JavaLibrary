/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.sql.Date;

/**
 *
 * @author razikov
 */
public class Using {
    private Integer id;
    private Book idBook;
    private Reader idReader;
    private Date dateReturn;
    
    public Using(Book idBook, Reader idReader, Date ms) {
        this.idBook = idBook;
        this.idReader = idReader;
        this.dateReturn = ms;
    }
    
    public Integer getId() {
        return this.id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Book getIdBook() {
        return this.idBook;
    }
    public void setIdBook(Book id) {
        this.idBook = id;
    }
    public Reader getIdReader() {
        return this.idReader;
    }
    public void setIdReader(Reader id) {
        this.idReader = id;
    }
    public Date getDateReturn() {
        return this.dateReturn;
    }
    public void setDateReturn(Date date) {
        this.dateReturn = date;
    }
}
