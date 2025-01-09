package com.pleiades.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Clothes {
    private int id;
    private String name;
    private String url;
    private String attribute;
    private int price;
}
