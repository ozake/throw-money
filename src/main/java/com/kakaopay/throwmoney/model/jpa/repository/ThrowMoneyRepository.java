package com.kakaopay.throwmoney.model.jpa.repository;

import com.kakaopay.throwmoney.model.jpa.entity.ThrowMoneyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThrowMoneyRepository extends JpaRepository<ThrowMoneyEntity, Long> {
}
