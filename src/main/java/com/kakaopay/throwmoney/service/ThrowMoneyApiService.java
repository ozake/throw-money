package com.kakaopay.throwmoney.service;

import com.kakaopay.throwmoney.common.exception.ApiException;
import com.kakaopay.throwmoney.common.support.Identification.Identify;
import com.kakaopay.throwmoney.dto.request.TokenRequest;
import com.kakaopay.throwmoney.dto.request.ThrowRequest;
import com.kakaopay.throwmoney.dto.response.PickUpResponse;
import com.kakaopay.throwmoney.dto.response.ReceviedDetailsDto;
import com.kakaopay.throwmoney.dto.response.ThrowDetailsResponse;
import com.kakaopay.throwmoney.dto.response.ThrowResponse;
import com.kakaopay.throwmoney.dto.type.common.YesNoType;
import com.kakaopay.throwmoney.dto.vo.PickUpDto;
import com.kakaopay.throwmoney.dto.vo.ThrowMoneyDto;
import com.kakaopay.throwmoney.model.jpa.entity.PickupEntity;
import com.kakaopay.throwmoney.model.jpa.entity.ThrowMoneyEntity;
import com.kakaopay.throwmoney.model.jpa.service.ThrowAndPickupService;
import com.kakaopay.throwmoney.model.redis.token.entity.PickUpTokenEntity;
import com.kakaopay.throwmoney.model.redis.token.entity.ThrowReadTokenEntity;
import com.kakaopay.throwmoney.model.redis.token.service.TokenRedisService;
import com.kakaopay.throwmoney.util.random.RandomGenerator;
import com.kakaopay.throwmoney.util.token.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
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
        // 토큰+roomId, 토큰+roomId+userId 를 키로 생성된 jwt 토큰을 레디스에 저장
        tokenRedisService.savePickUpToken(key, pickupJwt);
        tokenRedisService.saveThrowReadToken(readKey, throwReadJwt);
        return ThrowResponse.builder()
                .token(token)
                .build();
    }

    public List<PickupEntity> initPickup(ThrowRequest throwRequest, Long throwMoneyId) {
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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PickUpResponse pickUpMoney(TokenRequest tokenRequest) {
        PickUpResponse pickUpResponse;
        String key = tokenRequest.getToken() + identify.getRoomId().toString();
        Optional<PickUpTokenEntity> pickUpTokenEntityOptional = tokenRedisService.findByKeyByPickUpToken(key);
        PickUpTokenEntity pickUpTokenEntity = pickUpTokenEntityOptional.orElseThrow(() -> new ApiException("유효한 대화방이 아니거나, 유효기간이 만료 되었습니다.", HttpStatus.BAD_REQUEST, 400));
        String jwt = pickUpTokenEntity.getToken();
        if (jwtTokenProvider.validateToken(jwt)) {
            if (jwtTokenProvider.getUserId(jwt).equals(identify.getUserId())) {
                throw new ApiException("뿌린 본인은 받을 수 없습니다.", HttpStatus.BAD_REQUEST, 400);
            }

            Long throwMoneyId = jwtTokenProvider.getThrowId(jwt);

            Optional<ThrowMoneyEntity> throwMoneyEntityOptional = throwAndPickupService.findById(throwMoneyId);

            ThrowMoneyEntity throwMoneyEntity = throwMoneyEntityOptional.orElseThrow(() -> new ApiException("조회에 실패하였습니다. 다시시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR, 500));

            List<PickupEntity> pickupEntityList = throwMoneyEntity.getPickupEntityList();
            Boolean isReceived = false;
            if (pickupEntityList.size() > 0) {
                isReceived = pickupEntityList.stream()
                        .anyMatch(pickupEntity -> {
                            if (ObjectUtils.isEmpty(pickupEntity.getUserId())) {
                                return false;
                            } else {
                                return pickupEntity.getUserId().equals(identify.getUserId());
                            }
                        });
            }

            if (isReceived) {
                throw new ApiException("이미 받으셨습니다.", HttpStatus.BAD_REQUEST, 400);
            } else {
                PickupEntity pickupEntity = pickupEntityList.stream()
                        .filter(entity -> entity.getIsReceived().equals(YesNoType.NO))
                        .max(Comparator.comparingLong(PickupEntity::getAmount))
                        .orElseThrow(() -> new ApiException("이미 모든 받기가 끝났습니다.", HttpStatus.BAD_REQUEST, 400));
                pickupEntity.setUserId(identify.getUserId());
                pickupEntity.setIsReceived(YesNoType.YES);
                pickupEntity = throwAndPickupService.savePickup(pickupEntity);

                pickUpResponse = PickUpResponse.builder()
                        .amount(pickupEntity.getAmount())
                        .build();
            }
        } else {
            throw new ApiException("유효기간이 지난 받기 요청입니다.", HttpStatus.BAD_REQUEST, 400);
        }
        return pickUpResponse;
    }

    public ThrowDetailsResponse getThrowDetails(String token) {
        String readKey = token + identify.getRoomId().toString() + identify.getUserId().toString();
        Optional<ThrowReadTokenEntity> throwReadTokenEntityOptional = tokenRedisService.findByKeyByThrowReadToken(readKey);
        ThrowReadTokenEntity throwReadTokenEntity = throwReadTokenEntityOptional.orElseThrow(() -> new ApiException("본인이 아니거나, 유효기간이 지난 요청입니다.", HttpStatus.BAD_REQUEST, 400));
        String jwt = throwReadTokenEntity.getToken();
        if (jwtTokenProvider.validateToken(jwt)) {
            if (!jwtTokenProvider.getUserId(jwt).equals(identify.getUserId())) {
                throw new ApiException("본인이 아니면 조회 할 수 없습니다.", HttpStatus.BAD_REQUEST, 400);
            }
            Long throwMoneyId = jwtTokenProvider.getThrowId(jwt);
            Optional<ThrowMoneyEntity> throwMoneyEntityOptional = throwAndPickupService.findById(throwMoneyId);
            ThrowMoneyEntity throwMoneyEntity = throwMoneyEntityOptional.orElseThrow(() -> new ApiException("조회에 실패하였습니다. 다시시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR, 500));
            List<PickupEntity> pickupEntityList = throwAndPickupService.findByPickupListOrderByAmountDesc(throwMoneyId, YesNoType.YES);
            AtomicReference<Long> amount = new AtomicReference<>(0L);
            List<ReceviedDetailsDto> receviedDetailsDtoList = pickupEntityList.stream()
                    .map(pickupEntity -> {
                        amount.set(amount.get() + pickupEntity.getAmount());
                        return ReceviedDetailsDto.builder()
                                .amount(pickupEntity.getAmount())
                                .userId(pickupEntity.getUserId())
                                .build();
                    }).collect(Collectors.toList());
            return ThrowDetailsResponse.builder()
                    .throwAmount(throwMoneyEntity.getAmount())
                    .receviedAmount(amount.get())
                    .receviedDetailsDtoList(receviedDetailsDtoList)
                    .throwTime(jwtTokenProvider.getIssuedAt(jwt))
                    .build();

        } else {
            throw new ApiException("유효기간이 지난 받기 요청입니다.", HttpStatus.BAD_REQUEST, 400);
        }
    }
}
