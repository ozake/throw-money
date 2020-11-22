package com.kakaopay.throwmoney.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.throwmoney.dto.request.ThrowRequest;
import com.kakaopay.throwmoney.dto.type.http.CustomHeaderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ThrowControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("뿌리기를 요청 리퀘스트 검증테스")
    public void throwRequestTest() throws Exception {

        // given
        ThrowRequest throwRequest = new ThrowRequest();
        throwRequest.setAmount(-1);
        throwRequest.setMemberCnt(0);

        // When
        final ResultActions actions =
                mockMvc.perform(post("/v1/throw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(CustomHeaderType.USER_ID.getValue(), "123")
                        .header(CustomHeaderType.ROOM_ID.getValue(), "345")
                        .content(objectMapper.writeValueAsString(throwRequest)));

        // then
        actions
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }
}
