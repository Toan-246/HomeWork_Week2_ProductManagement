package com.codegym.controller;

import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.service.category.ICategoryService;
import com.codegym.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class CategoryController {
	@Autowired
	private ICategoryService categoryService;
	@Autowired
	private IProductService productService;
	@GetMapping ("categories")
	public ModelAndView showAllCategory (@PageableDefault (value = 10)Pageable pageable){
		ModelAndView modelAndView = new ModelAndView("category/list");
		Page<Category> categories = categoryService.findAll(pageable);
		modelAndView.addObject("categories", categories);
		return modelAndView;
	}
	@GetMapping ("categories/create")
	public ModelAndView showCreateForm (){
		ModelAndView modelAndView = new ModelAndView("/category/create");
		modelAndView.addObject("category", new Category());
		return modelAndView;
	}

	@PostMapping("categories/create")
	public ModelAndView addNewCategory (@ModelAttribute Category category){
		categoryService.save(category);
		return new ModelAndView("redirect:/categories");
	}

	@GetMapping("categories/edit/{id}")
	public ModelAndView showEditForm (@PathVariable Long id){
		Optional<Category> category = categoryService.findById(id);
		if (!category.isPresent()){
			return new ModelAndView("error-404");
		}
		ModelAndView modelAndView = new ModelAndView("/category/edit");
		modelAndView.addObject("category", category.get());
		return modelAndView;
	}
	@PostMapping ("categories/edit")
	public ModelAndView editCategory (@ModelAttribute Category category){
//		if (category == null){
//			return new ModelAndView("error-404");
//		}
		categoryService.save(category);
		return new ModelAndView("redirect:/categories");
	}
	@GetMapping ("categories/delete/{id}")
	public ModelAndView showDeleteForm (@PathVariable Long id){
		Optional<Category> category = categoryService.findById(id);
		if (!category.isPresent()){
			return new ModelAndView("error-404");
		}
		ModelAndView modelAndView = new ModelAndView("/category/delete");
		modelAndView.addObject("category", category.get());
		return modelAndView;
	}

	@PostMapping ("categories/delete")
	public ModelAndView deleteProvince(@ModelAttribute Category category) {
		categoryService.deleteById(category.getId());
		return new ModelAndView("redirect:/categories");
	}
	@GetMapping ("/categories/{id}")
	public ModelAndView viewAllProductByCategory(@PathVariable Long id, @PageableDefault (value = 10)Pageable pageable){
		Optional<Category> category = categoryService.findById(id);
		if (!category.isPresent()){
			return new ModelAndView("error-404");
		}
		ModelAndView modelAndView = new ModelAndView("category/view");
		Page<Product> products = productService.findAllByCategory(category.get(), pageable);
		modelAndView.addObject("category", category.get());
		modelAndView.addObject("products", products);
		return modelAndView;
	}
}
