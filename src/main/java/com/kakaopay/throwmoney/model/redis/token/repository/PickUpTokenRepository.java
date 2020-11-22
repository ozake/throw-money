package com.kakaopay.throwmoney.model.redis.token.repository;

import com.kakaopay.throwmoney.model.redis.token.entity.PickUpTokenEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickUpTokenRepository extends CrudRepository<PickUpTokenEntity, String> {
}
