package com.kakaopay.throwmoney.model.jpa.repository;

import com.kakaopay.throwmoney.dto.type.common.YesNoType;
import com.kakaopay.throwmoney.model.jpa.entity.PickupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PickupRepository extends JpaRepository<PickupEntity, Long> {

    List<PickupEntity> findByThrowMoneyIdAndIsReceivedOrderByAmountDesc(Long throwMoneyId, YesNoType isReceived);

    Optional<PickupEntity> findFirstByThrowMoneyIdAndIsReceivedOrderByAmountDesc(Long throwMoneyId, YesNoType isReceived);
}
