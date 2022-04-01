package com.exec.model;

import java.util.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("AspiringCandidate")
public class AspiringCandidate{
    
    @Id
    public String roll_no;
    public String name;
    public String email;
    public List<String> Seconders;
    public List<String> Proposers;
    public String manifesto;
    public String post;
    
    public AspiringCandidate(String roll_no, String name, String email, List<String> Seconders, List<String> Proposers, String manifesto, String post) {
        this.roll_no = roll_no;
        this.name = name;
        this.email = email;
        this.Seconders = Seconders;
        this.Proposers = Proposers;
        this.manifesto = manifesto;
        this.post = post;
    }

}
