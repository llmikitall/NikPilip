package ru.mikandton.tgBot.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.mikandton.tgBot.entities.Product;

import java.util.List;

@RepositoryRestResource (collectionResourceRel = "products", path = "products")
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);


    @Query("""
            SELECT p
            FROM OrderProduct op
            JOIN op.product p
            GROUP BY p
            ORDER BY COUNT(op.clientOrder) DESC;
            """)
    List<Product> findPopularProducts(Pageable pageable);
}
