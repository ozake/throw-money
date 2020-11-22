package com.kakaopay.throwmoney.model.redis.token.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@RedisHash(value = "throwRead", timeToLive = 604800000L)
public class ThrowReadTokenEntity extends TokenBaseEntity implements Serializable {
    private static final long serialVersionUID = 8741830946280519604L;
}
