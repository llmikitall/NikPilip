package ru.mikandton.tgBot.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import ru.mikandton.tgBot.InitPrompt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OpenAiService {

    private final ObjectProvider<ToolService> toolService;
    private final ChatClient chatClient;

    public OpenAiService(ObjectProvider<ToolService> toolService, ChatClient.Builder chatClientBuilder) {
        this.toolService = toolService;
        this.chatClient = chatClientBuilder.build();
    }

    // ConcurrentHashMap - потокобезопасный, чтобы случайно не блокировался
    private final Map<Long, List<Message>> historyAll = new ConcurrentHashMap<>();

    public String processMessage(Long userId, String message){

        // Если для chatId будет существовать свой List<>, то он его вернёт, иначе создаст новый и его вернёт.
        List<Message> history = historyAll.computeIfAbsent(userId, cid -> new ArrayList<>(10));

        history.addLast(new UserMessage(message));
        if (history.size() > 10)
            history.removeFirst();

        // Создание новых инстансов с помощью prototype и провайдера
        ToolService tools = toolService.getObject();
        tools.setUserId(userId);

        String prompt = InitPrompt.prompt;
        if (prompt == null)
            return "Лучше понажимайте кнопки...";

        return chatClient.prompt()
                .system(prompt)
                .user(message)
                .messages(history)
                .toolCallbacks(ToolCallbacks.from(tools))
                .call()
                .content();

    }

}
