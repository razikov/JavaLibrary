/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library;

import java.util.Date;

/**
 *
 * @author razikov
 */
public class Using {
    private Integer id;
    private Integer idBook;
    private Integer idReader;
    private Date dateReturn;
    
    Using(Integer idBook, Integer idReader, Date date) {
        this.idBook = idBook;
        this.idReader = idReader;
        this.dateReturn = date;
    }
    
    public Integer getId() {
        return this.id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getIdBook() {
        return this.idBook;
    }
    public void setIdBook(Integer id) {
        this.idBook = id;
    }
    public Integer getIdReader() {
        return this.idReader;
    }
    public void setIdReader(Integer id) {
        this.idReader = id;
    }
    public Date getDateReturn() {
        return this.dateReturn;
    }
    public void setDateReturn(Date date) {
        this.dateReturn = date;
    }
}
