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

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Получение всех Category которые принадлежат к категории parent
     * @param parent Category
     * @return List[Category]
     */
    public List<Category> getCategoryByParent(Category parent){
        return categoryRepository.findByParent(parent);
    }

    /**
     * Получение всех Category
     * @return List[Category]
     */
    public List<Category> getAllCategory(){
        return categoryRepository.findAll();
    }

}
