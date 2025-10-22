package com.demo.sell_card_demo1.repository;

import com.demo.sell_card_demo1.entity.Member;
import com.demo.sell_card_demo1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findMemberByUser(User user);
}
