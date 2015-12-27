/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author razikov
 */
public class Reader {
    //фамилию, имя, отчество, возраст, список книг с указанием даты возврата. 
    private Integer id;
    private String name; //Имя
    private String familyName; //Фамилия
    private String fatherName; //Отчество
    private Integer age;
    private Set<Using> list = new HashSet<Using>();
    
    public Reader() {
    }

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String n) {
        this.name = n;
    }
    
    public String getFamilyName() {
        return this.familyName;
    }
    
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
    
    public String getFatherName() {
        return this.fatherName;
    }
    
    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
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
