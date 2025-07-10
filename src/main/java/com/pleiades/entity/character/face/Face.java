//package com.pleiades.entity.character.face;
//
//import com.pleiades.entity.character.Characters;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.hibernate.annotations.OnDelete;
//import org.hibernate.annotations.OnDeleteAction;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "face")
//public class Face {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "hair_name", unique = false)
//    private Hair hair;
//
//    @ManyToOne
//    @JoinColumn(name = "skin_name", unique = false)
//    private Skin skin;
//
//    @ManyToOne
//    @JoinColumn(name = "expression_name", unique = false)
//    private Expression expression;
//
//    @OneToOne(mappedBy = "face", cascade = {CascadeType.REMOVE, CascadeType.REFRESH})
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    Characters characters;
//}
