/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author skylo
 */
@Slf4j
@Getter @Setter
@NoArgsConstructor
public class AddressEntry {
    private static int count = 0;
    
    private int id;
    private String name;
    private String email;
    private String phone;
    
    public static int getNextId(){
        return ++count;
    }
    
    public AddressEntry(String name, String email, String phone){
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
