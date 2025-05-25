/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

/**
 *
 * @author jiye
 */
@Entity
@Table(name = "addrbook")
@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class AddressEntry {

    @Id
    private String email;

    private String name;
    private String phone;
    private String category;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String username;

    public AddressEntry(String email, String name, String phone, String category) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.category = category;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
