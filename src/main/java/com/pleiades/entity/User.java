package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String accessToken;

    @Column(nullable = false, unique = true)
    private String naverId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String birthDate;

//    @Column(nullable = true)
//    private String email;
//
//    @Column(nullable = true)
//    private String phoneNumber;
//
//    @Column(nullable = true)
//    private String address;
//
//    @Column(nullable = true)
//    private String profileImage;
//

}
