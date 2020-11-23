package com.kakaopay.throwmoney.service;

import com.kakaopay.throwmoney.common.support.Identification.Identify;
import com.kakaopay.throwmoney.dto.request.ThrowRequest;
import com.kakaopay.throwmoney.dto.response.ThrowResponse;
import com.kakaopay.throwmoney.dto.type.common.YesNoType;
import com.kakaopay.throwmoney.model.jpa.entity.PickupEntity;
import com.kakaopay.throwmoney.model.jpa.entity.ThrowMoneyEntity;
import com.kakaopay.throwmoney.model.jpa.repository.PickupRepository;
import com.kakaopay.throwmoney.model.jpa.repository.ThrowMoneyRepository;
import com.kakaopay.throwmoney.model.redis.token.entity.PickUpTokenEntity;
import com.kakaopay.throwmoney.model.redis.token.service.TokenRedisService;
import com.kakaopay.throwmoney.util.random.RandomGenerator;
import com.kakaopay.throwmoney.util.token.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ThrowServiceTest {

    @Autowired
    private ThrowMoneyApiService throwMoneyApiService;

    @Autowired
    private ThrowMoneyRepository throwMoneyRepository;

    @Autowired
    private PickupRepository pickupRepository;

    @Autowired
    private RandomGenerator randomGenerator;

    @Autowired
    private Identify identify;

    @Autowired
    private TokenRedisService tokenRedisService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private long amount;
    private int memberCnt;
    private long userId;
    private long roomId;

    @BeforeEach
    public void init() {
        this.amount = 70000;
        this.memberCnt = 6;
        this.userId = 7375;
        this.roomId = 787812;
    }

    @Test
    @DisplayName("토큰은 3자리 문자열로 구성되며 예측이 불가능한지 검증 ")
    public void throwTokenTest() {
        // When
        // 병렬로 스트림을 생성하여 시간에 관계 없이 난수가 나오는지 검증
        Stream<String> testStream = Stream.generate(() -> randomGenerator.generateSecureRandomToken()).parallel().limit(100);
        List<String> testArr = testStream.collect(Collectors.toList());
        String sampleTxt = testArr.get(12);
        testArr.remove(12);

        List<String> thenArr = testArr.stream().filter(s -> s.equals(sampleTxt)).collect(Collectors.toList());
        List<String> sizeTestArr = testArr.stream().filter(s -> s.length() != 3).collect(Collectors.toList());

        //then
        assertThat(thenArr.size() == 0);
        assertThat(sizeTestArr.size() == 0);
    }

    @Test
    @DisplayName("뿌리기 요청건에 대한 토큰을 발급하고 응답값으로 내려줍니다.")
    public void throwMoneyTest() {
        // when
        ThrowRequest throwRequest = new ThrowRequest();
        throwRequest.setAmount(amount);
        throwRequest.setMemberCnt(memberCnt);
        identify.setUserId(userId);
        identify.setRoomId(roomId);
        ThrowResponse throwResponse = throwMoneyApiService.throwMoney(throwRequest);

        // then
        assertThat(throwResponse.getToken());
    }

    @Test
    @DisplayName("뿌릴 금액을 인원수에 맞게 분배하여 저장합니다.")
    public void throwMoneyDivedTest() {
        // when
        ThrowRequest throwRequest = new ThrowRequest();
        throwRequest.setAmount(amount);
        throwRequest.setMemberCnt(memberCnt);
        identify.setUserId(userId);
        identify.setRoomId(roomId);
        ThrowResponse throwResponse = throwMoneyApiService.throwMoney(throwRequest);

        String key = throwResponse.getToken() + identify.getRoomId().toString();
        Optional<PickUpTokenEntity> pickUpTokenEntityOptional = tokenRedisService.findByKeyByPickUpToken(key);
        PickUpTokenEntity pickUpTokenEntity = pickUpTokenEntityOptional.orElseThrow(() -> new AssertionError("Test failed"));
        String jwt = pickUpTokenEntity.getToken();

        List<PickupEntity> pickupEntityList = pickupRepository.findByThrowMoneyIdAndIsReceivedOrderByAmountDesc(jwtTokenProvider.getThrowId(jwt), YesNoType.NO);
        long sumAmount = pickupEntityList.stream().map(pickupEntity -> pickupEntity.getAmount()).mapToLong(Long::longValue).sum();

        // then
        assertThat(memberCnt == pickupEntityList.size());
        assertThat(amount == sumAmount);
    }




}
