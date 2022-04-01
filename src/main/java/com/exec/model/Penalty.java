package com.exec.model;

import java.sql.Timestamp;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Penalty")
public class Penalty {

    @Id
    public String id;

    public String role ;
    public String name;
    public String roll_no;
    public String fine;
    public String level;
    public String part;
    public String remark; // Put remark = "None" if no additional remarks.

    public Penalty(String role, String name, String roll_no, String fine, String level, String part, String remark){
        this.id = generate_hash();
        this.role = role;
        this.name = name;
        this.roll_no = roll_no;
        this.fine = fine;
        this.level = level;
        this.part = part;
        this.remark = remark;
    }

    private String generate_hash()
    {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String input = timestamp.toString();

        try {
  
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } 
  
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
