package com.fireboxsys.supermarkets.repository;

import com.fireboxsys.supermarkets.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository <Sale, Long>{
}
