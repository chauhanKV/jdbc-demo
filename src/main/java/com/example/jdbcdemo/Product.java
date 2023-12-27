package com.example.jdbcdemo;

import lombok.*;
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long ID;
    private String name;
    private Double cost;
}
