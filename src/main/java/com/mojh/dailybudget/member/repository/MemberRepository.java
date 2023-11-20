package com.mojh.dailybudget.member.repository;

import com.mojh.dailybudget.member.domain.Member;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {

    Optional<Member> findByAccountId(String accountId);
    boolean existsByAccountId(String accountId);

}
