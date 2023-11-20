package com.mojh.dailybudget.auth.repository;


import com.mojh.dailybudget.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findByChainId(String chainId);

}
