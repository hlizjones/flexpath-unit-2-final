package org.example.controllers;


import org.example.daos.ProductDao;
import org.example.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for products.
 * This class is responsible for handling all HTTP requests related to products.
 */
@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/products")
public class ProductController {
    /**
     * The product data access object.
     */
    @Autowired
    private ProductDao productDao;

    /**
     * Gets all products.
     *
     * @return A list of all products.
     */
    @GetMapping
    public List<Product> getAll() {
        return productDao.getProducts();
    }

    /**
     * Gets a product by id.
     *
     * @param id The id of the product.
     * @return The product with the given id.
     */
    @GetMapping(path = "{id}")
    public Product get(@PathVariable int id) {
        Product product = productDao.getProductById(id);
        if (product == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        return product;
    }

    /**
     * Creates a new product.
     *
     * @param product The product to create.
     * @return The product created.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Product create(@RequestBody Product product) {
        return productDao.createProduct(product);
    }

    /**
     * Updates a product.
     *
     * @param product The new product.
     * @param id The id of the product.
     * @return The updated product.
     */
    @PutMapping(path = "/{id}")
    public Product update(@RequestBody Product product, @PathVariable int id) {
        if (productDao.getProductById(id) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        product.setId(id);
        return productDao.updateProduct(product);
    }

    /**
     * Deletes a product.
     *
     * @param id The id of the product.
     * @return The number of rows affected (1 if a product was deleted, 0 if no product was found).
     */
    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable int id) {
        if(productDao.getProductById(id) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        return productDao.deleteOrder(id);
    }
}
