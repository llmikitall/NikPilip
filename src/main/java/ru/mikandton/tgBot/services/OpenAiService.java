package ru.mikandton.tgBot.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OpenAiService {

    private final ToolService toolService;
    private final ChatClient chatClient;

    public OpenAiService(ChatClient.Builder chatClientBuilder, ToolService toolService) {
        this.chatClient = chatClientBuilder.build();
        this.toolService = toolService;
    }

    // ConcurrentHashMap - потокобезопасный, чтобы случайно не блокировался
    private final Map<Long, List<Message>> historyAll = new ConcurrentHashMap<>();

    public String processMessage(Long userId, String message){

        // Если для chatId будет существовать свой List<>, то он его вернёт, иначе создаст новый и его вернёт.
        List<Message> history = historyAll.computeIfAbsent(userId, cid -> new ArrayList<>(10));

        history.addLast(new UserMessage(message));
        if (history.size() > 10)
            history.removeFirst();
        toolService.setCurrentUserId(userId);
        try{
            return chatClient.prompt()
                    .system("""
                Ты — ассистент кафе NikPilip:
                Популярные продукты:
                - Пицца: Гавайская, Пепперони
                - Роллы: Филадельфия
                - Напитки: Яблочный, Кока-кола
                Остальные продукты ищи в searchProductByName, если спрашивают про конкретный продукт.
                Если не получилось найти конкретный продукт, поищи схожие продукты в searchAllProduct.
                Все категории находятся в searchAllCategory. Все продукты в одной категории находятся в searchAllProductByCategory.
                2) Не оформляй заказ, пока не убедишься в том, что он готов оформить. Используй placeClientOrderAi.
                3) Отвечай на вопросы про продукты и категории.
                4) Перед оформлением заказа, проверяй наличие продуктов в его заказе orderProductClient.
                5) Когда он просит добавить продукт в заказ, используй addProductOrder.
                6) Не заводи других бесед не касающихся твоей работы
                Язык: русский. Стиль: дружелюбный.
            """)
                    .user(message)
                    .messages(history)
                    .tools(toolService)
                    .call()
                    .content();
        } finally {
            toolService.clear();
        }

    }

}
