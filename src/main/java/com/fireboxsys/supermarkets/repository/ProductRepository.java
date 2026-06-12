package com.fireboxsys.supermarkets.repository;

import com.fireboxsys.supermarkets.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository <Product, Long>{

    @Query("SELECT d.product FROM Sale s JOIN s.saleDetails d GROUP BY d.product ORDER BY SUM (d.quantity) DESC")
    List<Product> findTopProducts(Pageable pageable);

}
