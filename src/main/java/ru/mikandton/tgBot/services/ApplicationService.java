package ru.mikandton.tgBot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mikandton.tgBot.entities.ClientOrder;
import ru.mikandton.tgBot.entities.Product;
import ru.mikandton.tgBot.repositories.ClientOrderRepository;
import ru.mikandton.tgBot.repositories.OrderProductRepository;
import ru.mikandton.tgBot.repositories.ProductRepository;

import java.util.List;

@Service
@Transactional
public class ApplicationService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientOrderRepository clientOrderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    // getProductsByCategoryID - Список Product которые есть в категории ID
    public List<Product> getProductsByCategoryId(Long categoryId){
        return productRepository.findByCategoryId(categoryId);
    }

    // getClientOrders - Список всех ClientOrder клиента
    public List<ClientOrder> getClientOrders(Long id){
        return clientOrderRepository.findByClientId(id);
    }

    // getClientProduct - Список Product заказанных клиентом.
    public List<Product> getClientProduct(Long id){
        return productRepository.findByClientId(id);
    }

    // getTopPopularProducts - Количество самых популярных Product, заказанные клиентами (первые limit).
    public List<Product> getTopPopularProducts(Integer limit){
        // Альтернатива limit в SQL
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findPopularProducts(pageable);
    }
}
