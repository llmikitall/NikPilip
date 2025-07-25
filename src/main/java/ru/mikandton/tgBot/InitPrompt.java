package ru.mikandton.tgBot;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;


@Service
@Transactional
public class InitPrompt {
    /**
     * Инициализация prompt.txt. Переменная хранит SystemPrompt
     */
    public static String prompt;

    @PostConstruct
    private void init() {
        ClassPathResource file = new ClassPathResource("prompt.txt");
        try (InputStream is = file.getInputStream()){
            prompt = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
            System.out.println("[+] Файл prompt.txt успешно загружен!");
        } catch (IOException e) {
            System.out.println("[!] Проблема с файлом /prompt.txt!");
        }
    }
}
