package com.kakaopay.throwmoney.model.redis.token.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@RedisHash(value = "pickup", timeToLive = 600000L)
public class PickUpTokenEntity extends TokenBaseEntity implements Serializable {
    private static final long serialVersionUID = -284417035873841565L;
}
