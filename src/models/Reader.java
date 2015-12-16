/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.Set;

/**
 *
 * @author razikov
 */
public class Reader {
    //фамилию, имя, отчество, возраст, список книг с указанием даты возврата. 
    private Integer id;
    private String fName; //Имя
    private String lName; //Фамилия
    private String sName; //Отчество
    private Integer age;
    private Set<Using> list;
    
    public Reader() {
    }

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getfName() {
        return this.fName;
    }
    
    public void setfName(String fName) {
        this.fName = fName;
    }
    
    public String getlName() {
        return this.lName;
    }
    
    public void setlName(String lName) {
        this.lName = lName;
    }
    
    public String getsName() {
        return this.sName;
    }
    
    public void setsName(String sName) {
        this.sName = sName;
    }
    
    public Integer getAge() {
        return this.age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public Set<Using> getList() {
        return this.list;
    }
    
    public void setList(Set<Using> list) {
        this.list = list;
    }
}
