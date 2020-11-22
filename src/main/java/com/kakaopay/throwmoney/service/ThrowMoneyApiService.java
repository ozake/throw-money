package com.kakaopay.throwmoney.service;

import com.kakaopay.throwmoney.common.support.Identification.Identify;
import com.kakaopay.throwmoney.dto.request.ThrowRequest;
import com.kakaopay.throwmoney.dto.response.ThrowResponse;
import com.kakaopay.throwmoney.dto.type.common.YesNoType;
import com.kakaopay.throwmoney.dto.vo.PickUpDto;
import com.kakaopay.throwmoney.dto.vo.ThrowMoneyDto;
import com.kakaopay.throwmoney.model.jpa.entity.PickupEntity;
import com.kakaopay.throwmoney.model.jpa.entity.ThrowMoneyEntity;
import com.kakaopay.throwmoney.model.jpa.service.ThrowAndPickupService;
import com.kakaopay.throwmoney.model.redis.token.service.TokenRedisService;
import com.kakaopay.throwmoney.util.random.RandomGenerator;
import com.kakaopay.throwmoney.util.token.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ThrowMoneyApiService {

    @Autowired
    private RandomGenerator randomGenerator;

    @Autowired
    private TokenRedisService tokenRedisService;

    @Autowired
    private Identify identify;

    @Autowired
    private ThrowAndPickupService throwAndPickupService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ThrowResponse throwMoney(ThrowRequest throwRequest) {
        String token = randomGenerator.generateSecureRandomToken();
        String key = token + identify.getRoomId().toString();
        String readKey = token + identify.getRoomId().toString() + identify.getUserId().toString();
        if (tokenRedisService.findByKeyByPickUpToken(key).isPresent()) {
            // 생성된 토큰이 기존 요청한 키와 같을 경우 다시 토큰, 키 생성
            token = randomGenerator.generateSecureRandomToken();
            key = token + identify.getRoomId().toString();
            readKey = token + identify.getRoomId().toString() + identify.getUserId().toString();
        }
            ThrowMoneyDto throwMoneyDto = ThrowMoneyDto.builder()
                    .MemberCnt(throwRequest.getMemberCnt())
                    .amount(throwRequest.getAmount())
                    .userId(identify.getUserId())
                    .roomId(identify.getRoomId())
                    .token(token)
                    .build();
            ThrowMoneyEntity throwMoneyEntity = throwAndPickupService.saveThrowMoney(throwMoneyDto);
            List<PickupEntity> pickupEntityList = initPickup(throwRequest, throwMoneyEntity.getId());
            String pickupJwt = jwtTokenProvider.createTokenForPickUp(key, throwMoneyEntity.getId());
            String throwReadJwt = jwtTokenProvider.createTokenForThrowRead(key, throwMoneyEntity.getId());
            // 토큰+roomId, 토큰+roomId+userId 를 키로 하는 jwt 토큰을 레디스에 저
            tokenRedisService.savePickUpToken(key, pickupJwt);
            tokenRedisService.saveThrowReadToken(readKey, throwReadJwt);
        return ThrowResponse.builder()
                .token(token)
                .build();
    }

    private List<PickupEntity> initPickup(ThrowRequest throwRequest, Long throwMoneyId) {
        Stream<Integer> initStream =
                Stream.iterate(throwRequest.getMemberCnt(), memberCnt -> memberCnt - 1).limit(throwRequest.getMemberCnt());
        AtomicReference<Long> amount = new AtomicReference<>(throwRequest.getAmount());
        List<PickUpDto> pickUpDtoList = initStream.map(member -> {
            Long tmpAmount = randomGenerator.throwMoneyDivider(amount.get(), member);
            amount.set(amount.get() - tmpAmount);
            return PickUpDto.builder()
                    .amount(tmpAmount)
                    .isReceived(YesNoType.NO)
                    .throwMoneyId(throwMoneyId)
                    .build();
        }).collect(Collectors.toList());
        return throwAndPickupService.saveAllPickup(pickUpDtoList);
    }

    public String generateToken() {
        return randomGenerator.generateSecureRandomToken();
    }

    public void pickUpMoney() {

    }
}
