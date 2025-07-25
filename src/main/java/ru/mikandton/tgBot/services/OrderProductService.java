package ru.mikandton.tgBot.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mikandton.tgBot.entities.ClientOrder;
import ru.mikandton.tgBot.entities.OrderProduct;
import ru.mikandton.tgBot.repositories.OrderProductRepository;

import java.util.List;

@Service
@Transactional
public class OrderProductService {

    private final OrderProductRepository orderProductRepository;

    public OrderProductService(OrderProductRepository orderProductRepository) {
        this.orderProductRepository = orderProductRepository;
    }


    public void saveOrderProduct(OrderProduct orderProduct){
        orderProductRepository.save(orderProduct);
    }

    /**
     * Поиск конкретного OrderProduct с помощью ClientOrder.id и Product.id
     * @param pid Product.id
     * @param coid ClientOrder.id
     * @return OrderProduct
     */
    public OrderProduct findOrderProductByClientOrderIdAndProductId(Long pid, Long coid){
        return orderProductRepository.findOrderProductByProductAndClientOrder(pid, coid);
    }

    /**
     * Поиск всех OrderProduct привязанные к ClientOrder.
     * @param clientOrder ClientOrder
     * @return List[OrderProduct]
     */
    public List<OrderProduct> findOrderProductsByClientOrder(ClientOrder clientOrder){
        return orderProductRepository.findByClientOrder(clientOrder);
    }

}
