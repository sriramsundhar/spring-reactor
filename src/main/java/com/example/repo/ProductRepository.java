package com.example.repo;

import com.example.domain.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;


public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
    @Query("SELECT * FROM products WHERE name = :name")
    Flux<Product> findByName(String name);
}
