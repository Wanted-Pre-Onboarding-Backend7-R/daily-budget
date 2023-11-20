package com.mojh.daliybudget.member.repository;

import com.mojh.daliybudget.member.domain.Member;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {

    Optional<Member> findByAccountId(String accountId);
    boolean existsByAccountId(String accountId);

}
