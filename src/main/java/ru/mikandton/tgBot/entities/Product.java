package ru.mikandton.tgBot.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Category category;

    @Column (length = 50, nullable = false, unique = true)
    private String name;

    @Column (length = 400, nullable = false)
    private String description;

    @Column (nullable = false)
    private Double price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }


    @Override
    public int hashCode(){
        return Objects.hash(id);
    }


}
