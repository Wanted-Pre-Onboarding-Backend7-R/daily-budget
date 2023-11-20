package com.mojh.daliybudget.auth.repository;


import com.mojh.daliybudget.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findByChainId(String chainId);

}
