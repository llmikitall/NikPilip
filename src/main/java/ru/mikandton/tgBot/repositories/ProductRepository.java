package ru.mikandton.tgBot.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.mikandton.tgBot.entities.Product;

import java.util.List;

@RepositoryRestResource (collectionResourceRel = "products", path = "products")
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByCategoryName(String name);

    @Query("""
            SELECT p
            FROM OrderProduct op
            JOIN op.clientOrder co
            JOIN op.product p
            WHERE co.client.id = :id
            """)
    List<Product> findByClientId(@Param("id") Long id);

    @Query("""
            SELECT p
            FROM OrderProduct op
            JOIN op.product p
            GROUP BY p
            ORDER BY SUM(op.countProduct) DESC
            """)
    List<Product> findPopularProducts(Pageable pageable);

    Product findByName(String name);
}
