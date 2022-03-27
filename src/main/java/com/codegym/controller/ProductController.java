package com.codegym.controller;

import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.model.ProductForm;
import com.codegym.service.category.ICategoryService;
import com.codegym.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
public class ProductController {
	@Value("${file-upload}")
	private String uploadPath;
	@Autowired
	private IProductService productService;

	@Autowired
	private ICategoryService categoryService;

	@ModelAttribute("categories")
	public Iterable<Category> categories (){
		return categoryService.findAll();
	}

	@GetMapping ("/products")
	public ModelAndView showAllProduct (@RequestParam(name = "q") Optional<String> q, @PageableDefault(value =10) Pageable pageable){
		Page<Product> products;
		if (q.isPresent()){
			products = productService.findAllByNameContaining(q.get(), pageable);
		}
		else {
			products = productService.findAll(pageable);
		}
		ModelAndView modelAndView = new ModelAndView("/product/list");
		modelAndView.addObject("products", products);
		return modelAndView;
	}
	@GetMapping ("/products/create")
	public ModelAndView showCreateForm (){
		ModelAndView modelAndView= new ModelAndView("/product/create");
		modelAndView.addObject("product", new ProductForm());
		return modelAndView;
	}
	@PostMapping ("/products/create")
	public ModelAndView createProduct (@ModelAttribute ProductForm productForm){
		String fileName = productForm.getImage().getOriginalFilename();
		long currentTime = System.currentTimeMillis();
		fileName = currentTime+fileName;
		try {
			FileCopyUtils.copy(productForm.getImage().getBytes(), new File(uploadPath + fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Product product = new Product(productForm.getId(), productForm.getName(), productForm.getPrice(), productForm.getDescription(), fileName, productForm.getCategory());
		productService.save(product);
		return new ModelAndView("redirect:/products");
	}
	@GetMapping("/products/edit/{id}")
	public ModelAndView showEditForm (@PathVariable Long id){
		Optional<Product> product = productService.findById(id);
		if (!product.isPresent()){
			return new ModelAndView("error-404");
		}
		else {
			ModelAndView modelAndView = new ModelAndView("/product/edit");
			modelAndView.addObject("product", product.get());
			return modelAndView;
		}
	}
	@PostMapping ("/products/edit/{id}")
	public ModelAndView editProduct (@PathVariable Long id, @ModelAttribute ProductForm productForm){
		Optional<Product> product = productService.findById(id);
		if (product.isPresent()){
			Product oldProduct = product.get();
			MultipartFile img = productForm.getImage();
			if (img.getSize()!=0){
				String filename = img.getOriginalFilename();
				long currentTime = System.currentTimeMillis();
				filename = currentTime + filename;
				oldProduct.setImage(filename);
				try {
					FileCopyUtils.copy(productForm.getImage().getBytes(), new File(uploadPath + filename));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			oldProduct.setName(productForm.getName());
			oldProduct.setPrice(productForm.getPrice());
			oldProduct.setDescription(productForm.getDescription());
			oldProduct.setCategory(productForm.getCategory());
			productService.save(oldProduct);
			return new ModelAndView("redirect:/products");
		}
		return new ModelAndView("error-404");
	}
	@GetMapping ("/products/delete/{id}")
	public ModelAndView showDeleteForm (@PathVariable Long id){
		Optional<Product> product = productService.findById(id);
		if (!product.isPresent()){
			return new ModelAndView("error-404");
		}
		else {
			ModelAndView modelAndView = new ModelAndView("/product/delete");
			modelAndView.addObject("product", product.get());
			return modelAndView;
		}
	}
	@PostMapping ("/products/delete/{id}")
	public ModelAndView deleteProduct (@PathVariable Long id){
		Optional<Product>product = productService.findById(id);
		if (!product.isPresent()){
			return new ModelAndView("error-404");
		}
		else {
			File file = new File(uploadPath + product.get().getImage());
			if (file.exists()){
				file.delete();
			}
			productService.deleteById(id);
			return new ModelAndView("redirect:/products");
		}
	}
	@GetMapping("/products/{id}")
	public ModelAndView viewProduct (@PathVariable Long id){

		Optional<Product> product = productService.findById(id);
		if (!product.isPresent()){
			return new ModelAndView("error-404");
		}
		ModelAndView modelAndView = new ModelAndView("product/view");
		modelAndView.addObject("product", product.get());
		return modelAndView;
	}
}
