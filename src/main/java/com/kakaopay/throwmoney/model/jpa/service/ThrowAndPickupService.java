package com.kakaopay.throwmoney.model.jpa.service;

import com.kakaopay.throwmoney.common.support.mapper.MapperSupport;
import com.kakaopay.throwmoney.dto.type.common.YesNoType;
import com.kakaopay.throwmoney.dto.vo.PickUpDto;
import com.kakaopay.throwmoney.dto.vo.ThrowMoneyDto;
import com.kakaopay.throwmoney.model.jpa.entity.PickupEntity;
import com.kakaopay.throwmoney.model.jpa.entity.ThrowMoneyEntity;
import com.kakaopay.throwmoney.model.jpa.repository.PickupRepository;
import com.kakaopay.throwmoney.model.jpa.repository.ThrowMoneyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ThrowAndPickupService {

    private final ThrowMoneyRepository throwMoneyRepository;

    private final PickupRepository pickupRepository;

    public ThrowAndPickupService(ThrowMoneyRepository throwMoneyRepository, PickupRepository pickupRepository) {
        this.throwMoneyRepository = throwMoneyRepository;
        this.pickupRepository = pickupRepository;
    }

    public ThrowMoneyEntity saveThrowMoney(ThrowMoneyDto throwMoneyDto) {
        return throwMoneyRepository.save(MapperSupport.map(throwMoneyDto, ThrowMoneyEntity.class));
    }

    public PickupEntity savePickup(PickUpDto pickUpDto) {
        return pickupRepository.save(MapperSupport.map(pickUpDto, PickupEntity.class));
    }

    public PickupEntity savePickup(PickupEntity pickupEntity) {
        return pickupRepository.save(pickupEntity);
    }

    public List<PickupEntity> saveAllPickup(List<PickUpDto> pickUpDtoList) {
        return pickupRepository.saveAll(MapperSupport.mapAll(pickUpDtoList, PickupEntity.class));
    }

    public List<PickupEntity> findByPickupListOrderByAmountDesc(Long throwMoneyId, YesNoType isReceived) {
        return pickupRepository.findByThrowMoneyIdAndIsReceivedOrderByAmountDesc(throwMoneyId, isReceived);
    }

    public Optional<PickupEntity> findFirstByPickUpOrderbyAmountDesc(Long throwMoneyId, YesNoType isReceived) {
        return pickupRepository.findFirstByThrowMoneyIdAndIsReceivedOrderByAmountDesc(throwMoneyId, isReceived);
    }

    public Optional<ThrowMoneyEntity> findById(Long id) {
        return throwMoneyRepository.findById(id);
    }
}
