package ru.mikandton.tgBot.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mikandton.tgBot.entities.ClientOrder;
import ru.mikandton.tgBot.repositories.ClientOrderRepository;

@Service
@Transactional
public class ClientOrderService {

    @Autowired
    ClientOrderRepository clientOrderRepository;

    public void saveClientOrder(ClientOrder clientOrder){
        clientOrderRepository.save(clientOrder);
    }

    public ClientOrder getMaxClientOrderByClientId(Long id){
        return clientOrderRepository.findMaxClientOrderByClientId(id);
    }
}
