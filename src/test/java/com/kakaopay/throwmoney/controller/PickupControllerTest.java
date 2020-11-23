package com.kakaopay.throwmoney.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.throwmoney.common.exception.ApiException;
import com.kakaopay.throwmoney.common.support.Identification.Identify;
import com.kakaopay.throwmoney.dto.request.ThrowRequest;
import com.kakaopay.throwmoney.dto.request.TokenRequest;
import com.kakaopay.throwmoney.dto.response.ThrowResponse;
import com.kakaopay.throwmoney.dto.type.http.CustomHeaderType;
import com.kakaopay.throwmoney.service.ThrowMoneyApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PickupControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired
    private ThrowMoneyApiService throwMoneyApiService;

    @Autowired
    private Identify identify;

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
    @DisplayName("뿌리기당 한 사용자는 한번만 검증 ")
    public void pickUpTest() {
        ThrowRequest throwRequest = new ThrowRequest();
        throwRequest.setAmount(amount);
        throwRequest.setMemberCnt(memberCnt);
        identify.setUserId(userId);
        identify.setRoomId(roomId);
        ThrowResponse throwResponse = throwMoneyApiService.throwMoney(throwRequest);

        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setToken(throwResponse.getToken());
        identify.setUserId(1234L);
        throwMoneyApiService.pickUpMoney(tokenRequest);

        assertThatThrownBy(() -> throwMoneyApiService.pickUpMoney(tokenRequest))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("이미 받으셨습니다.");
    }

    @Test
    @DisplayName("자신이 뿌리기한 건은 자신이 받을 수 없는지 검증 ")
    public void pickUpTest2() {
        ThrowRequest throwRequest = new ThrowRequest();
        throwRequest.setAmount(amount);
        throwRequest.setMemberCnt(memberCnt);
        identify.setUserId(userId);
        identify.setRoomId(roomId);
        ThrowResponse throwResponse = throwMoneyApiService.throwMoney(throwRequest);

        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setToken(throwResponse.getToken());

        assertThatThrownBy(() -> throwMoneyApiService.pickUpMoney(tokenRequest))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("뿌린 본인은 받을 수 없습니다.");
    }

    @Test
    @DisplayName("같은 대화방에서만 받기 가능 검증 ")
    public void pickUpTest3() {
        ThrowRequest throwRequest = new ThrowRequest();
        throwRequest.setAmount(amount);
        throwRequest.setMemberCnt(memberCnt);
        identify.setUserId(userId);
        identify.setRoomId(roomId);
        ThrowResponse throwResponse = throwMoneyApiService.throwMoney(throwRequest);

        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setToken(throwResponse.getToken());
        identify.setUserId(1234L);
        identify.setRoomId(7768L);

        assertThatThrownBy(() -> throwMoneyApiService.pickUpMoney(tokenRequest))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("유효한 대화방이 아니거나, 유효기간이 만료 되었습니다.");
    }

    @Test
    @DisplayName("토큰 요청 검증 ")
    public void pickUpTest4() throws Exception {
        // given
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setToken("1");

        // When
        final ResultActions actions =
                mockMvc.perform(post("/v1/pickup")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .header(CustomHeaderType.USER_ID.getValue(), "123")
                        .header(CustomHeaderType.ROOM_ID.getValue(), "345")
                        .content(objectMapper.writeValueAsString(tokenRequest)));

        // then
        actions
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isInternalServerError())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE));
    }
}
