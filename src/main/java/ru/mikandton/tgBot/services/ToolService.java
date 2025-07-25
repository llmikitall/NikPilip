package ru.mikandton.tgBot.services;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.mikandton.tgBot.entities.*;
import ru.mikandton.tgBot.repositories.*;

import java.util.List;

@Service
@Scope("prototype")
public class ToolService {

    private final ClientOrderRepository clientOrderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ClientRepository clientRepository;

    public ToolService(ClientOrderRepository clientOrderRepository, OrderProductRepository orderProductRepository, ProductRepository productRepository, CategoryRepository categoryRepository, ClientRepository clientRepository) {
        this.clientOrderRepository = clientOrderRepository;
        this.orderProductRepository = orderProductRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.clientRepository = clientRepository;
    }

    private Long userId;

    public void setUserId(Long userId){
        this.userId = userId;
    }

    /**
     * Ищет Product с указанным названием.
     * @param nameProduct название продукта
     * @return Product
     */
    @Tool(name="searchProductByName", description = "Ищет продукт по названию среди всех продуктов")
    public Product searchProductByName(String nameProduct){
        return productRepository.findByName(nameProduct);
    }

    /**
     * Ищет все Product в базе данных.
     * @return List[Product]
     */
    @Tool(name="searchAllProduct", description = "Ищет все возможные продукты")
    public List<Product> searchAllProduct(){
        return productRepository.findAll();
    }

    /**
     * Ищет все Category в базе данных.
     * @return List[Category]
     */
    @Tool(name="searchAllCategory", description = "Ищет все категории")
    public List<Category> searchAllCategory(){
        return categoryRepository.findAll();
    }

    /**
     * Ищет все Product в указанной категории в базе данных.
     * @param nameCategory имя категории;
     * @return List[Product]
     */
    @Tool(name="searchAllProductByCategory", description = "Ищет все возможные продукты выбранной категории")
    public List<Product> searchAllProductByCategory(String nameCategory){
        return productRepository.findByCategoryName(nameCategory);
    }

    /**
     * Добавляет количество продуктов в заказ (OrderProduct), если заказ существовал, иначе - создаёт новый.
     * @param name название продукта;
     * @param countProduct количество продуктов;
     * @return String
     */
    @Tool(name="addProductOrder", description = "Добавляет продукт и их количество в заказ")
    public String addProductOrder(String name, int countProduct){

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

    /**
     * Считает стоимость всех OrderProduct, привязанных к текущему ClientOrder.
     * Меняет статус на 1 и создаёт новый ClientOrder со статусом 0.
     * @return String
     */
    @Tool(name = "placeClientOrderAi", description = "Оформление заказа пользователя")
    public String placeClientOrderAi(){

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

    /**
     * Ищет все OrderProduct привязанные к текущему ClientOrder.
     * @return List[OrderProduct]
     */
    @Tool(name = "orderProductClient", description = "Список продуктов в заказе")
    public List<OrderProduct> orderProductClient(){
        Client client = clientRepository.findByExternalId(userId);
        ClientOrder clientOrder = clientOrderRepository.findMaxClientOrderByClientId(client.getId());

        return orderProductRepository.findByClientOrder(clientOrder);
    }

    /**
     * Изменяет address Client в базе данных.
     * @return String
     */
    @Tool(name="addAddress", description = "Сохраняет адрес клиента")
    public String addAddress(String address){

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

    /**
     * Изменяет phoneNumber Client в базе данных.
     * @return String
     */
    @Tool(name="addPhone", description = "Сохраняет номер телефона клиента")
    public String addPhone(String phone){

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
