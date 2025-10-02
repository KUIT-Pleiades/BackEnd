package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(of="id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String userName;

    @Column
    private LocalDate birthDate;

    @Column
    private LocalDate createdDate;

    @Column(nullable = true)
    String profileUrl;

    @Column(nullable = true)
    String characterUrl;

    @Column
    private String refreshToken;

    @Column
    @ColumnDefault("0")
    private Long coin;

    @Column
    @ColumnDefault("0")
    private Long stone;

}