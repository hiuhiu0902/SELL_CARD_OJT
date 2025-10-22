package com.demo.sell_card_demo1.repository;

import com.demo.sell_card_demo1.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    Branch findBranchByName(String branchName);
}
