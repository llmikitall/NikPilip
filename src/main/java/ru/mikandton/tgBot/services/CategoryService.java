package ru.mikandton.tgBot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mikandton.tgBot.entities.Category;
import ru.mikandton.tgBot.repositories.CategoryRepository;
import ru.mikandton.tgBot.repositories.ProductRepository;

import java.util.List;

@Service
@Transactional
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getCategoryByParent(Category parent){
        return categoryRepository.findByParent(parent);
    }

    public List<Category> getAllCategory(){
        return categoryRepository.findAll();
    }

}
