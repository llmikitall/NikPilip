package ru.mikandton.tgBot;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mikandton.tgBot.entities.*;
import ru.mikandton.tgBot.repositories.*;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class FillingTests {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private ClientOrderRepository clientOrderRepository;

    @Test
    void createCategoryAndProducts(){

        List<Product> products = new ArrayList<>();
        Category parent;
        Category category;

        category = saveCategory("Пицца", null);
        saveProduct("Маргарита", category, "Томатный соус, моцарелла, базалик", 520.00);
        saveProduct("Пепперони", category, "Томатный соус, острая колбаса, сыр", 510.00);
        products.add(saveProduct("Гавайская", category, "Курица, ананас, соус, сыр", 530.00));

        parent = saveCategory("Роллы", null);
        category = saveCategory("Классические роллы", parent);
        saveProduct("Филадельфия", category, "Лосось, сливочный сыр, огурец, рис", 50.00);
        saveProduct("Калифорния", category, "Краб, авокадо, огурец, икра", 55.00);
        saveProduct("Цезарь", category, "Курица, салат, соус, сыр", 45.00);
        category = saveCategory("Запеченные роллы", parent);
        saveProduct("Тайский", category, "Запеченный лосось, огурец, спайси-соус", 60.00);
        products.add(saveProduct("Горячий дракон", category, "Угорь, сыр, соус, унаги", 55.00));
        saveProduct("Сливочный краб", category, "Краб, сыр, сайонез, икра", 65.00);
        category = saveCategory("Сладкие роллы", parent);
        saveProduct("Банан-шоколад", category, "Банан, нутелла, кокос", 55.00);
        saveProduct("Клубничный", category, "Клубника, сливочный сыр, мед", 45.00);
        saveProduct("Яблочные", category, "Яблоко, корица, карамель", 65.00);
        category = saveCategory("Наборы", parent);
        saveProduct("Сет \"Самурай\"", category, "24 ролла: Филадельфия, Калифорния", 1060.00);
        saveProduct("Сет \"Император\"", category, "24 ролла: запечённые + классика", 1120.00);
        saveProduct("Сет \"Делюкс\"", category, "36 ролла: микс всех видов", 1780.00);

        parent = saveCategory("Бургеры", null);
        category = saveCategory("Классические бургеры", parent);
        products.add(saveProduct("Чизбургер", category, "Говядина, сыр, салат, соус", 450.00));
        products.add(saveProduct("Чикенбургер", category, "Курица, сыр, помидор, соус", 400.00));
        saveProduct("Дабл бургер", category, "Две котлеты, сыр, бекон", 600.00);
        category = saveCategory("Острые бургеры", parent);
        saveProduct("Дьявол", category, "Острая курица, перец халапеньо, соус", 600.00);
        saveProduct("Файр", category, "Говядина, острый сыр, чили", 700.00);
        saveProduct("Спайси чикен", category, "Курица, халапеньо, острый майонез", 650.00);

        parent = saveCategory("Напитки", null);
        category = saveCategory("Газированные напитки", parent);
        products.add(saveProduct("Кола", category, "Классическая газировка", 150.00));
        saveProduct("Фанта", category, "Апельсиновый вкус", 140.00);
        saveProduct("Спрайт", category, "Лимон-лайм", 130.00);
        category = saveCategory("Энергетические напитки", parent);
        saveProduct("Red Bull", category, "Классический энергетик", 200.00);
        saveProduct("Burn", category, "С кисло-сладким вкусом", 180.00);
        saveProduct("Monster", category, "Большая банка, разные вкусы", 190.00);
        category = saveCategory("Соки", parent);
        products.add(saveProduct("Яблочный", category, "Освежающий сок", 110.00));
        saveProduct("Апельсиновый", category, "С мякотью", 120.00);
        saveProduct("Мультифрукт", category, "Микс фруктов", 100.00);
        category = saveCategory("Другие", parent);
        saveProduct("Чай (холодный)", category, "Липтон, персик/лимон", 110.00);
        saveProduct("Минералка", category, "Без газа/газированная", 120.00);
        saveProduct("Молочный коктейль", category, "Ваниль, шоколад, клубника", 100.00);

        Client client1 = saveClient(1L,"Пилипенко Никита Михайлович",
                "ул. Великая, д. 21, кв. 1, г. Севастополь", "+79784571049");
        Client client2 = saveClient(2L,"Кузнецов Владислав Вячеславович",
                "ул. Кирова, д. 331, кв. 21, г. Краснодар", "+79787777777");
        Client client3 = saveClient(3L,"Шаматрин Владимир Антонович",
                "ул. Юрия Гагарина, д. 22, кв. 12, г. Мелитополь", "+79787444477");

        ClientOrder clientOrder1_1 = saveClientOrder(client1, 200, 530.00);
        saveOrderProduct(clientOrder1_1, products.get(0), 1);
        saveOrderProduct(clientOrder1_1, products.get(5), 1);

        ClientOrder clientOrder1_2 = saveClientOrder(client1, 102, 640.00);
        saveOrderProduct(clientOrder1_2, products.get(0), 1);
        saveOrderProduct(clientOrder1_2, products.get(5), 1);

        ClientOrder clientOrder2 = saveClientOrder(client2, 102, 850.00);
        saveOrderProduct(clientOrder2, products.get(2), 1);
        saveOrderProduct(clientOrder2, products.get(4), 1);
        saveOrderProduct(clientOrder2, products.get(3), 1);
        saveOrderProduct(clientOrder1_2, products.get(5), 1);

        ClientOrder clientOrder3 = saveClientOrder(client3, 102, 2230.00);
        saveOrderProduct(clientOrder3, products.get(0), 1);
        saveOrderProduct(clientOrder3, products.get(1), 1);
        saveOrderProduct(clientOrder3, products.get(4), 1);
        saveOrderProduct(clientOrder1_2, products.get(5), 1);

    }


    private Product saveProduct(String name, Category category, String description, Double price){
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setDescription(description);
        product.setPrice(price);
        return productRepository.save(product);
    }


    private Category saveCategory(String name, Category parent){
        Category category = new Category();
        category.setName(name);
        category.setParent(parent);
        return categoryRepository.save(category);
    }


    private Client saveClient(Long externalID, String fullName, String address, String phoneNumber){
        Client client = new Client();
        client.setAddress(address);
        client.setExternalId(externalID);
        client.setFullName(fullName);
        client.setPhoneNumber(phoneNumber);
        return clientRepository.save(client);
    }

    private ClientOrder saveClientOrder(Client client, Integer status, Double total){
        ClientOrder clientOrder = new ClientOrder();
        clientOrder.setClient(client);
        clientOrder.setStatus(status);
        clientOrder.setTotal(total);
        return clientOrderRepository.save(clientOrder);
    }

    private void saveOrderProduct(ClientOrder clientOrder, Product product, Integer countProduct){
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setClientOrder(clientOrder);
        orderProduct.setProduct(product);
        orderProduct.setCountProduct(countProduct);
        orderProductRepository.save(orderProduct);
    }
}
