package com.kakaopay.throwmoney.util.filter;

import com.kakaopay.throwmoney.common.exception.ApiException;
import com.kakaopay.throwmoney.common.support.Identification.Identify;
import com.kakaopay.throwmoney.dto.type.http.CustomHeaderType;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IdentificationFilter extends OncePerRequestFilter {

    private Identify identify;

    public IdentificationFilter(Identify identify) {
        this.identify = identify;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // TODO 전처리
        String userId = request.getHeader(CustomHeaderType.USER_ID.getValue());
        String roomId = request.getHeader(CustomHeaderType.ROOM_ID.getValue());
        if (ObjectUtils.isEmpty(userId) | ObjectUtils.isEmpty(roomId)) {
            throw new ApiException("사용자 식별값 부재", HttpStatus.BAD_REQUEST, 400);
        } else {
            try {
                identify.setUserId(Long.parseLong(userId));
                identify.setRoomId(Long.parseLong(roomId));
            } catch (NumberFormatException e) {
                throw new ApiException("잘못된 식별값", HttpStatus.BAD_REQUEST, 400);
            }
        }
        filterChain.doFilter(request, response);
        // TODO 후처리
    }
}
