package com.codegym.service.product;

import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.service.IGeneralService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService extends IGeneralService<Product> {
	Page<Product> findAll (Pageable pageable);
	Page<Product> findAllByNameContaining (String name, Pageable pageable);
	Page<Product> findAllByCategory (Category category, Pageable pageable);
}
