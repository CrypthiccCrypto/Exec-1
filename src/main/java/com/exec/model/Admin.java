package com.exec.model;

import java.util.*;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("Admin")
public class Admin extends User {
    
    public Map<String,Integer> Announcements;

    public Admin(String roll_no, String name, String email){
        super(roll_no,name,email);
        this.Announcements = new  HashMap<String, Integer>();
    }

}
