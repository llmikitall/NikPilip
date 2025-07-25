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

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Поиск всех Product указанной Category.id
     * @param categoryId Category.id
     * @return List[Product]
     */
    public List<Product> getProductsByParent(Long categoryId){
        return productRepository.findByCategoryId(categoryId);
    }

    /**
     * Посик Product по его id
     * @param id Product.id
     * @return Optional[Product]
     */
    public Optional<Product> getProductById(Long id){
        return productRepository.findById(id);
    }

}
