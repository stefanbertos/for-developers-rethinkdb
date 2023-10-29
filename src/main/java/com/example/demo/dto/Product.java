package com.example.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Product {
    private Long id;

    private String name;

    private String description;

    private Float price;

    private String image;

    private String category;
}
