package com.fireboxsys.supermarkets.repository;

import com.fireboxsys.supermarkets.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository <Branch, Long>{
}
