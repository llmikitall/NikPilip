package ru.mikandton.tgBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.mikandton.tgBot.entities.ClientOrder;
import ru.mikandton.tgBot.entities.Product;

import java.sql.ClientInfoStatus;
import java.util.List;

@RepositoryRestResource (collectionResourceRel = "client-orders", path = "client-orders")
public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {
    List<ClientOrder> findByClientId(Long id);

    @Query("""
            SELECT co
            FROM ClientOrder co
            WHERE co.client.id = :id AND status = 0
            """)
    ClientOrder findMaxClientOrderByClientId(@Param("id") Long id);
}
