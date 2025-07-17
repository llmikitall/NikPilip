package ru.mikandton.tgBot;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mikandton.tgBot.entities.Category;
import ru.mikandton.tgBot.entities.Product;
import ru.mikandton.tgBot.repositories.*;

@Service
@Transactional
public class InitDataBase {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;


    @PostConstruct
    public void init(){
        if (categoryRepository.count() != 0){
            System.out.println("[+] В базе данных есть поля.");
            return;
        }
        System.out.println("[+] Заполнение базы данных...");

        Category parent;
        Category category;

        category = saveCategory("Пицца", null);
        saveProduct("Маргарита", category, "Томатный соус, моцарелла, базалик", 520.00);
        saveProduct("Пепперони", category, "Томатный соус, острая колбаса, сыр", 510.00);
        saveProduct("Гавайская", category, "Курица, ананас, соус, сыр", 530.00);

        parent = saveCategory("Роллы", null);
        category = saveCategory("Классические роллы", parent);
        saveProduct("Филадельфия", category, "Лосось, сливочный сыр, огурец, рис", 50.00);
        saveProduct("Калифорния", category, "Краб, авокадо, огурец, икра", 55.00);
        saveProduct("Цезарь", category, "Курица, салат, соус, сыр", 45.00);
        category = saveCategory("Запеченные роллы", parent);
        saveProduct("Тайский", category, "Запеченный лосось, огурец, спайси-соус", 60.00);
        saveProduct("Горячий дракон", category, "Угорь, сыр, соус, унаги", 55.00);
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
        saveProduct("Чизбургер", category, "Говядина, сыр, салат, соус", 450.00);
        saveProduct("Чикенбургер", category, "Курица, сыр, помидор, соус", 400.00);
        saveProduct("Дабл бургер", category, "Две котлеты, сыр, бекон", 600.00);
        category = saveCategory("Острые бургеры", parent);
        saveProduct("Дьявол", category, "Острая курица, перец халапеньо, соус", 600.00);
        saveProduct("Файр", category, "Говядина, острый сыр, чили", 700.00);
        saveProduct("Спайси чикен", category, "Курица, халапеньо, острый майонез", 650.00);

        parent = saveCategory("Напитки", null);
        category = saveCategory("Газированные напитки", parent);
        saveProduct("Кола", category, "Классическая газировка", 150.00);
        saveProduct("Фанта", category, "Апельсиновый вкус", 140.00);
        saveProduct("Спрайт", category, "Лимон-лайм", 130.00);
        category = saveCategory("Энергетические напитки", parent);
        saveProduct("Red Bull", category, "Классический энергетик", 200.00);
        saveProduct("Burn", category, "С кисло-сладким вкусом", 180.00);
        saveProduct("Monster", category, "Большая банка, разные вкусы", 190.00);
        category = saveCategory("Соки", parent);
        saveProduct("Яблочный", category, "Освежающий сок", 110.00);
        saveProduct("Апельсиновый", category, "С мякотью", 120.00);
        saveProduct("Мультифрукт", category, "Микс фруктов", 100.00);
        category = saveCategory("Другие", parent);
        saveProduct("Чай (холодный)", category, "Липтон, персик/лимон", 110.00);
        saveProduct("Минералка", category, "Без газа/газированная", 120.00);
        saveProduct("Молочный коктейль", category, "Ваниль, шоколад, клубника", 100.00);
        System.out.println("[+]       ...прошло успешно! (Обработчиков исключений никаких нет :d)");
    }

    private void saveProduct(String name, Category category, String description, Double price){
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setDescription(description);
        product.setPrice(price);
        productRepository.save(product);
    }


    private Category saveCategory(String name, Category parent){
        Category category = new Category();
        category.setName(name);
        category.setParent(parent);
        return categoryRepository.save(category);
    }
}
