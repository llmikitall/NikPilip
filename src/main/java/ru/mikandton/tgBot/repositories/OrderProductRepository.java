package ru.mikandton.tgBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.mikandton.tgBot.entities.OrderProduct;

import java.util.List;

@RepositoryRestResource (collectionResourceRel = "order-products", path = "order-products")
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List <OrderProduct> findByClientOrderId(Long id);
}
