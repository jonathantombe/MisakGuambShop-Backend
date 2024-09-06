package com.misakguambshop.app.service;

import com.misakguambshop.app.exception.ResourceNotFoundException;
import com.misakguambshop.app.model.Category;
import com.misakguambshop.app.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public Category getCategoryByName(String name) {
        Optional<Category> category = categoryRepository.findByName(name);
        return category.orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + name));
    }

    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("A category with this name already exists");
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, @Valid Category categoryDetails) {
        Category category = getCategoryById(id);

        if (!category.getName().equals(categoryDetails.getName()) && categoryRepository.existsByName(categoryDetails.getName())) {
            throw new IllegalArgumentException("A category with this name already exists");
        }

        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());

        return categoryRepository.save(category);
    }

    public void deleteCategory (Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}

