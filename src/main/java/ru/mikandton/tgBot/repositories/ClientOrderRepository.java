package ru.mikandton.tgBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.mikandton.tgBot.entities.ClientOrder;

import java.util.List;

@RepositoryRestResource (collectionResourceRel = "client-orders", path = "client-orders")
public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {
    List<ClientOrder> findByClientId(Long id);
}
