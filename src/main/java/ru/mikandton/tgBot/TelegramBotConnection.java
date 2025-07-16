package ru.mikandton.tgBot;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import ru.mikandton.tgBot.entities.*;
import ru.mikandton.tgBot.services.*;

import java.util.List;
import java.util.Optional;


@Service
public class TelegramBotConnection {
    private final TelegramBot bot = new TelegramBot("7578084200:AAG8rp-8D_StzPC4GqGk5whl2CLPQmcrbzE");

    // Не знаю куда бы запихать такой огромный кусок кода... Help...
    private final ClientService clientService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final ClientOrderService clientOrderService;
    private final OrderProductService orderProductService;

    public TelegramBotConnection(ClientService clientService, CategoryService categoryService, ProductService productService, ClientOrderService clientOrderService, OrderProductService orderProductService) {
        this.clientService = clientService;
        this.categoryService = categoryService;
        this.productService = productService;
        this.clientOrderService = clientOrderService;
        this.orderProductService = orderProductService;
    }

    @PostConstruct
    public void start(){
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::processUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void processUpdate(Update update) {

        // CallBack обработчик Inline-кнопок:
        if(update.callbackQuery() != null){
            AnswerCallbackQuery answer = new AnswerCallbackQuery(update.callbackQuery().id());
            // Почему Optional? Некоторые личности умеют подменивать callback.data. Проверка на корректность ID.
            Optional<Product> product = productService.getProductById(Long.parseLong(update.callbackQuery().data()));
            if (product.isEmpty()) {
                bot.execute(answer);
                return;
            }
            // Поиск новейшего заказа клиентом (Client.id + status = 0)
            Client client = clientService.getClientByExternalId(update.callbackQuery().from().id());
            ClientOrder clientOrder = clientOrderService.getMaxClientOrderByClientId(client.getId());

            // Поиск существующего OrderProduct на основе ClientOrder и Product
            OrderProduct orderProduct = orderProductService.findOrderProductByClientOrderIdAndProductId(
                    product.get().getId(),
                    clientOrder.getId()
            );
            // Пусто? Не беда! Создаём новое сполна!
            if (orderProduct == null){
                orderProduct = new OrderProduct();
                orderProduct.setClientOrder(clientOrder);
                orderProduct.setCountProduct(1);
                orderProduct.setProduct(product.get());
            }
            // Уже был такой заказ? Тогда увеличиваем "Количество" на +1.
            else
                orderProduct.setCountProduct(orderProduct.getCountProduct() + 1);
            orderProductService.saveOrderProduct(orderProduct);
            bot.execute(answer);
            return;
        }

        // Обработчик текстовых сообщений
        // Поиск клиента в базе данных по chat.id
        Client client = clientService.getClientByExternalId(update.message().chat().id());
        if (client == null){
            // Создание нового пользователя и заказа
            System.out.println("[!] Новый клиент... обработка данных...");
            createClient(client = new Client(), update.message());
            createClientOrder(client);
            System.out.println("[+] Создание пользователя прошла успешно!");
        }
        // Логирование сообщений для проверки корректной работы
        System.out.printf("[%s] %s%n", client.getExternalId(), update.message().text());

        SendMessage message;
        // Величайший костыль... Нужны хендлеры на считывание команд...
        if(update.message().text().contains("/setaddress")){
            String address = update.message().text().replace("/setaddress ", "");
            client.setAddress(address);
            clientService.saveClient(client);
            message = new SendMessage(client.getExternalId(), "Адрес успешно заменён!");
        } else if (update.message().text().contains("/setphone")) {
            String phone = update.message().text().replace("/setphone ", "");
            client.setPhoneNumber(phone);
            clientService.saveClient(client);
            message = new SendMessage(client.getExternalId(), "Номер телефона успешно заменён!");
        }
        else {
            // Почему Switch, а не if? Для эдакой расширяемости... И так понятнее...
            switch (update.message().text()) {
                case "/start", "[Назад в главное меню]" ->
                        message = createButton(null, client.getExternalId(), "[Главное меню]");
                case "[Оформить заказ]" -> message = placeClientOrder(client);
                default -> {
                    // Получаем все категории и ищем в этом списке хотя бы одно совпадение.
                    List<Category> categories = categoryService.getAllCategory();
                    Optional<Category> parent = categories.stream()
                            .filter(category -> update.message().text().equals(category.getName()))
                            .findFirst();
                    // Если такой объект был найден -> открываем новое меню:
                    if (parent.isPresent())
                        message = createButton(parent.get(), client.getExternalId(), String.format("Меню [%s]:", parent.get().getName()));
                        // Любые другие сообщения, не связанные с командами и кнопками
                    else
                        message = new SendMessage(client.getExternalId(), "Нажимайте лучше на кнопочки...");
                }
            }
        }
        bot.execute(message);
    }

    private SendMessage createButton(Category parent, Long clientId, String text){
        // Получаем все подкатегории parent-категории
        List <Category> categories = categoryService.getCategoryByParent(parent);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup("[Оформить заказ]");

        // Есть хотя бы одна категория?
        if (!categories.isEmpty()){
            // Да! Тогда создаём кнопки категорий.
            for (Category category: categories)
                keyboard.addRow(new KeyboardButton(category.getName()));
            // Если parent == null (то есть высшая категория), то не добавляем кнопку "[Назад в главное меню]"
            if (parent != null)
                keyboard.addRow("[Назад в главное меню]");

            keyboard.resizeKeyboard(true);

            return new SendMessage(clientId, text).replyMarkup(keyboard);
        }
        else {
            // Нет! Тогда мы дошли до финальной "подкатегории", где не категории нужно показывать, а продукты!
            // Составляем Inline кнопочки!
            return createInline(clientId, parent);
        }
    }

    private SendMessage createInline(Long clientId, Category category){
        StringBuilder text = new StringBuilder();
        text.append(String.format("[%s]\n", category.getName()));

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<Product> products = productService.getProductsByParent(category.getId());
        for (Product product : products){
            text.append(String.format("   - [%s] - %s;\n", product.getName(), product.getDescription()));
            keyboard.addRow(new InlineKeyboardButton(
                    String.format("[%s] %.2f руб.", product.getName(), product.getPrice()))
                    .callbackData(String.format("%d", product.getId())
            ));
        }
        return new SendMessage(clientId, text.toString()).replyMarkup(keyboard);
    }


    private SendMessage placeClientOrder(Client client){
        if (client.getAddress().equals("-") || client.getPhoneNumber().equals("-")){
            return requestMissingData(client);
        }
        ClientOrder clientOrder = clientOrderService.getMaxClientOrderByClientId(client.getId());
        List<OrderProduct> orderProducts = orderProductService.findOrderProductsByClientOrder(clientOrder);
        StringBuilder text = new StringBuilder(String.format("[ID заказа: %s]\n", clientOrder.getId()));
        double total = 0.0;
        for (OrderProduct orderProduct: orderProducts){
            Product product = orderProduct.getProduct();
            text.append(String.format("\n   - %s: [Кол-во: %s] [Стоимость: %.2f]\n",
                    product.getName(),
                    orderProduct.getCountProduct(),
                    orderProduct.getCountProduct()*product.getPrice()));
            total += product.getPrice()*orderProduct.getCountProduct();
        }
        text.append(String.format("\nК оплате: %.2f\n", total));
        text.append(String.format("Доставка в: %s\nНомер телефона: %s", client.getAddress(), client.getPhoneNumber()));

        clientOrder.setStatus(1);
        clientOrder.setTotal(total);
        clientOrderService.saveClientOrder(clientOrder);
        createClientOrder(client);
        return new SendMessage(client.getExternalId(), text.toString());
    }


    private SendMessage requestMissingData(Client client){
        StringBuilder text = new StringBuilder("Для оформления заказа, укажите, пожалуйста:\n");
        if(client.getPhoneNumber().equals("-"))
            text.append("   - Номер телефона (/setphone [Телефон])\n");
        if(client.getAddress().equals("-"))
            text.append("   - Адрес проживания (/setaddress [Адрес])\n");
        return new SendMessage(client.getExternalId(), text.toString());
    }


    private void createClient(Client client, Message message){
        client.setExternalId(message.chat().id());

        // Проверка для избежания проблемы: если у пользователя только одно имя, было бы -> "Никита "
        if (message.chat().lastName() == null)
            client.setFullName(message.chat().firstName());
        else
            client.setFullName(String.format("%s %s",
                    message.chat().firstName(),
                    message.chat().lastName()));

        client.setPhoneNumber("-");
        client.setAddress("-");

        clientService.saveClient(client);
    }

    private void createClientOrder(Client client){
        ClientOrder clientOrder = new ClientOrder();
        clientOrder.setClient(client);
        clientOrder.setStatus(0);
        clientOrder.setTotal(0.0);
        clientOrderService.saveClientOrder(clientOrder);
    }

}