package ru.mikandton.tgBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.mikandton.tgBot.entities.ClientOrder;
import ru.mikandton.tgBot.entities.OrderProduct;

import java.util.List;

@RepositoryRestResource (collectionResourceRel = "order-products", path = "order-products")
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List <OrderProduct> findByClientOrder(ClientOrder clientOrder);

    @Query("""
            SELECT op
            FROM OrderProduct op
            WHERE op.clientOrder.id = :coid AND product.id = :pid
            """)
    OrderProduct findOrderProductByProductAndClientOrder(@Param("pid") Long pid, @Param("coid") Long coid);
}
