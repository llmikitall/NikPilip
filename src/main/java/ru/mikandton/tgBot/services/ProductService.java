package ru.mikandton.tgBot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mikandton.tgBot.entities.Product;
import ru.mikandton.tgBot.repositories.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProductsByParent(Long categoryId){
        return productRepository.findByCategoryId(categoryId);
    }

    public Optional<Product> getProductById(Long id){
        return productRepository.findById(id);
    }

}
