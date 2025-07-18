package ru.mikandton.tgBot.services;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mikandton.tgBot.entities.*;
import ru.mikandton.tgBot.repositories.*;

import java.util.List;
import java.util.Optional;

@Service
public class ToolService {

    @Autowired
    private ClientOrderRepository clientOrderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ClientRepository clientRepository;


    // Для каждого потока своя переменная (Не без помощи интернета...)
    private final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    public void setCurrentUserId(Long userId){
        currentUserId.set(userId);
    }
    public void clear(){
        currentUserId.remove();
    }


    @Tool(name="searchProductByName", description = "Ищет продукт по названию среди всех продуктов")
    public Product searchProductByName(String nameProduct){
        return productRepository.findByName(nameProduct);
    }


    @Tool(name="searchAllProduct", description = "Ищет все возможные продукты")
    public List<Product> searchAllProduct(){
        return productRepository.findAll();
    }


    @Tool(name="searchAllCategory", description = "Ищет все категории")
    public List<Category> searchAllCategory(){
        return categoryRepository.findAll();
    }


    @Tool(name="searchAllProductByCategory", description = "Ищет все возможные продукты выбранной категории")
    public List<Product> searchAllProductByCategory(String nameCategory){
        return productRepository.findByCategoryName(nameCategory);
    }


    @Tool(name="addProductOrder", description = "Добавляет продукт и их количество в заказ")
    public String addProductOrder(String name, int countProduct){
        Long userId = currentUserId.get();

        Product product = productRepository.findByName(name);
        if (product == null) {
            return "Такого продукта нет в списке... Может Вы имели в виду...";
        }

        // Поиск новейшего заказа клиентом (Client.id + status = 0)
        Client client = clientRepository.findByExternalId(userId);
        ClientOrder clientOrder = clientOrderRepository.findMaxClientOrderByClientId(client.getId());

        // Поиск существующего OrderProduct на основе ClientOrder и Product
        OrderProduct orderProduct = orderProductRepository.findOrderProductByProductAndClientOrder(
                product.getId(),
                clientOrder.getId());

        // Пусто? Не беда! Создаём новое сполна!
        if (orderProduct == null){
            orderProduct = new OrderProduct();
            orderProduct.setClientOrder(clientOrder);
            orderProduct.setCountProduct(countProduct);
            orderProduct.setProduct(product);
        }
        // Уже был такой заказ? Тогда увеличиваем "Количество"!
        else
            orderProduct.setCountProduct(orderProduct.getCountProduct() + countProduct);
        orderProductRepository.save(orderProduct);
        return "Заказ успешно добавлен!";
    }



    @Tool(name = "placeClientOrderAi", description = "Оформление заказа пользователя")
    public String placeClientOrderAi(){

        Long userId = currentUserId.get();

        Client client = clientRepository.findByExternalId(userId);
        StringBuilder text;
        if (client.getAddress().equals("-") || client.getPhoneNumber().equals("-")){
            text = new StringBuilder("Для оформления заказа, укажите, пожалуйста:\n");
            if(client.getPhoneNumber().equals("-"))
                text.append("   - Номер телефона (/setphone [Телефон])\n");
            if(client.getAddress().equals("-"))
                text.append("   - Адрес проживания (/setaddress [Адрес])\n");
            return text.toString();
        }

        ClientOrder clientOrder = clientOrderRepository.findMaxClientOrderByClientId(client.getId());

        List<OrderProduct> orderProducts = orderProductRepository.findByClientOrder(clientOrder);

        text = new StringBuilder(String.format("[ID заказа: %s]\n", clientOrder.getId()));

        double total = 0.0;
        int index = 1;
        for (OrderProduct orderProduct: orderProducts){
            Product product = orderProduct.getProduct();
            text.append(String.format("\n   %d) %s: [Кол-во: %s] [Стоимость: %.2f]\n",
                    index++,
                    product.getName(),
                    orderProduct.getCountProduct(),
                    orderProduct.getCountProduct()*product.getPrice()));

            total += product.getPrice()*orderProduct.getCountProduct();
        }

        text.append(String.format("\nК оплате: %.2f\n", total));
        text.append(String.format("Доставка в: %s\nНомер телефона: %s", client.getAddress(), client.getPhoneNumber()));

        clientOrder.setStatus(1);
        clientOrder.setTotal(total);
        clientOrderRepository.save(clientOrder);

        clientOrder = new ClientOrder();
        clientOrder.setClient(client);
        clientOrder.setStatus(0);
        clientOrder.setTotal(0.0);
        clientOrderRepository.save(clientOrder);

        return text.toString();
    }


    @Tool(name = "orderProductClient", description = "Список продуктов в заказе")
    public List<OrderProduct> orderProductClient(){
        Long userId = currentUserId.get();
        Client client = clientRepository.findByExternalId(userId);
        ClientOrder clientOrder = clientOrderRepository.findMaxClientOrderByClientId(client.getId());

        return orderProductRepository.findByClientOrder(clientOrder);
    }


    @Tool(name="addAddress", description = "Сохраняет адрес клиента")
    public String addAddress(String address){
        Long userId = currentUserId.get();

        Client client = clientRepository.findByExternalId(userId);

        String text;
        if (address.length() > 400 || address.length() < 25)
            text = "Некорректный адрес! Пример ввода: г. Ялта, ул. Гагарина, д.29, кв.12";
        else {
            client.setAddress(address);
            clientRepository.save(client);
            text = "Адрес успешно заменён!";
        }
        return text;
    }

    @Tool(name="addPhone", description = "Сохраняет номер телефона клиента")
    public String addPhone(String phone){
        Long userId = currentUserId.get();

        Client client = clientRepository.findByExternalId(userId);

        String text;
        if(phone.length() != 15){
            text = "Некорректный номер! Пример ввода: +7(978)457-1049";
        }
        else{
            client.setPhoneNumber(phone);
            clientRepository.save(client);
            text = "Номер телефона успешно заменён!";
        }
        return text;
    }



}
