package ru.mikandton.tgBot.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.mikandton.tgBot.entities.Category;

import java.util.List;

@RepositoryRestResource (collectionResourceRel = "categories", path = "categories")
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParent(Category parent);
}
