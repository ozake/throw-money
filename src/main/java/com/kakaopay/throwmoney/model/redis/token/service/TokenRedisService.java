package com.kakaopay.throwmoney.model.redis.token.service;

import com.kakaopay.throwmoney.model.redis.token.entity.PickUpTokenEntity;
import com.kakaopay.throwmoney.model.redis.token.entity.ThrowReadTokenEntity;
import com.kakaopay.throwmoney.model.redis.token.repository.PickUpTokenRepository;
import com.kakaopay.throwmoney.model.redis.token.repository.ThrowReadTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TokenRedisService {

    @Autowired
    private PickUpTokenRepository pickUpTokenRepository;

    @Autowired
    private ThrowReadTokenRepository throwReadTokenRepository;

    public PickUpTokenEntity savePickUpToken(String key, String jwtToken) {
        PickUpTokenEntity pickUpTokenEntity = new PickUpTokenEntity();
        pickUpTokenEntity.setId(key);
        pickUpTokenEntity.setToken(jwtToken);
        return pickUpTokenRepository.save(pickUpTokenEntity);
    }

    public ThrowReadTokenEntity saveThrowReadToken(String key, String jwtToken) {
        ThrowReadTokenEntity throwReadTokenEntity = new ThrowReadTokenEntity();
        throwReadTokenEntity.setId(key);
        throwReadTokenEntity.setToken(jwtToken);
        return throwReadTokenRepository.save(throwReadTokenEntity);
    }

    public Optional<PickUpTokenEntity> findByKeyByPickUpToken(String key) {
        return pickUpTokenRepository.findById(key);
    }

    public Optional<ThrowReadTokenEntity> findByKeyByThrowReadToken(String key) {
        return throwReadTokenRepository.findById(key);
    }

    public void deletePickUpToken(PickUpTokenEntity pickUpTokenEntity) {
        pickUpTokenRepository.delete(pickUpTokenEntity);
    }

    public void deleteThrowReadToken(ThrowReadTokenEntity throwReadTokenEntity) {
        throwReadTokenRepository.delete(throwReadTokenEntity);
    }

}
