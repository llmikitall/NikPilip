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
    @Autowired
    OrderProductRepository orderProductRepository;

    public void saveOrderProduct(OrderProduct orderProduct){
        orderProductRepository.save(orderProduct);
    }

    public OrderProduct findOrderProductByClientOrderIdAndProductId(Long pid, Long coid){
        return orderProductRepository.findOrderProductByProductAndClientOrder(pid, coid);
    }

    public List<OrderProduct> findOrderProductsByClientOrder(ClientOrder clientOrder){
        return orderProductRepository.findByClientOrder(clientOrder);
    }

}
