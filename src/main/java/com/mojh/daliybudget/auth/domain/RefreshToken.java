package com.mojh.daliybudget.auth.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;
import java.util.concurrent.TimeUnit;

@Getter
@RedisHash("refreshToken")
public class RefreshToken {

    @Id
    private String id;

    @Indexed
    private String chainId;

    private String accountId;

    private String tokenId;

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private Long ttl;

    @Builder
    public RefreshToken(String chainId, String accountId, String tokenId, Long ttl) {
        this.chainId = chainId;
        this.accountId = accountId;
        this.tokenId = tokenId;
        this.ttl = ttl;
    }

}

