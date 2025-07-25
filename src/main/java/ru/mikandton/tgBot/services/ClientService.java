package ru.mikandton.tgBot.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mikandton.tgBot.entities.Client;
import ru.mikandton.tgBot.repositories.ClientRepository;

@Service
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Поиск Client с помощью externalId
     * @param externalId Client.externalId
     * @return Client
     */
    public Client getClientByExternalId(Long externalId){
        return clientRepository.findByExternalId(externalId);
    }

    public void saveClient(Client client){
        clientRepository.save(client);
    }
}
