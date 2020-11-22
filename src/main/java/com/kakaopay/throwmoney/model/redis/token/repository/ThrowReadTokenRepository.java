package com.kakaopay.throwmoney.model.redis.token.repository;

import com.kakaopay.throwmoney.model.redis.token.entity.ThrowReadTokenEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThrowReadTokenRepository extends CrudRepository<ThrowReadTokenEntity, String> {
}
