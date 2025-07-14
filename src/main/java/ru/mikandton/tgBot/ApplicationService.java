package ru.mikandton.tgBot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mikandton.tgBot.entities.ClientOrder;
import ru.mikandton.tgBot.entities.OrderProduct;
import ru.mikandton.tgBot.entities.Product;
import ru.mikandton.tgBot.repositories.ClientOrderRepository;
import ru.mikandton.tgBot.repositories.OrderProductRepository;
import ru.mikandton.tgBot.repositories.ProductRepository;

import java.util.ArrayList;
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
        List <ClientOrder> clientOrders = clientOrderRepository.findByClientId(id);
        List <Product> products = new ArrayList<>();
        for (ClientOrder clientOrder : clientOrders) {
            List<OrderProduct> orderProducts = orderProductRepository.findByClientOrderId(clientOrder.getId());
            for (OrderProduct orderProduct : orderProducts){
                /* Проверка на уникальность. Предположительно именно её не пропускает jenkins.
                        В тестах я использую случай, если вдруг я сделал два заказа с одинаковым продуктом. */
                // if(!products.contains(orderProduct.getProduct()))
                products.add(orderProduct.getProduct());
            }
        }
        return products;
    }

    /* getTopPopularProducts - Количество самых популярных Product, заказанные клиентами (первые limit).
                Не считаю "количество" продуктов в заказе OrderProduct, так как я роллы продаю поштучными...*/
    public List<Product> getTopPopularProducts(Integer limit){
        // Альтернатива limit в SQL
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findPopularProducts(pageable);
    }
}
