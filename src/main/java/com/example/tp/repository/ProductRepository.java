package com.example.tp.repository;

import com.example.tp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Recherche par nom (contient, insensible à la casse)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Recherche par prix inférieur ou égal
    List<Product> findByPriceLessThanEqual(Double price);
}
