package com.kakaopay.throwmoney.model.redis.token.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public abstract class TokenBaseEntity {
    @Id
    private String id;

    private String token;
}
