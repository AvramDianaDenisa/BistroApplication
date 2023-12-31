package com.example.bistro.bistroapp.service;

import com.example.bistro.bistroapp.entity.Product;
import com.example.bistro.bistroapp.entity.ProductType;
import com.example.bistro.bistroapp.exception.ProductNotFoundException;
import com.example.bistro.bistroapp.repository.IngredientRepository;
import com.example.bistro.bistroapp.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;
    public ProductServiceImpl(ProductRepository productRepository, IngredientRepository ingredientRepository) {
        this.productRepository = productRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public Product getProductWithIngredients(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("No products found."));
    }

    @Override
    public Product addProduct(Product product) {
        if (!isValidProductType(product.getProductType())) {
            throw new IllegalArgumentException("Tipul de produs nu este permis.");
        }
        return productRepository.save(product);
    }
    @Override

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("No products with this ID found."));
    }
    @Override
    public Product updateProductPrice(Long id, double newPrice) {
        Product product = getProductById(id);
        product.setPrice(newPrice);
        return productRepository.save(product);
    }
    @Override
    public void removeProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
    @Override
    public List<Product> getTopMostWantedProducts(int count) {
        Pageable pageable = PageRequest.of(0, count);
        List<Product> topProducts = productRepository.findTopMostWantedProducts((PageRequest) pageable);

        if (topProducts.isEmpty()) {
            throw new ProductNotFoundException("No products with orders found.");
        }

        return topProducts;
    }

    public boolean isValidProductType(ProductType productType) {
        Set<ProductType> allowedProductTypes = EnumSet.of(
                ProductType.CAKE, ProductType.WAFFLES, ProductType.CROISSANT,
                ProductType.DONUT, ProductType.PASTA, ProductType.PIZZA,
                ProductType.RISOTTO
        );

        return allowedProductTypes.contains(productType);
    }
}

