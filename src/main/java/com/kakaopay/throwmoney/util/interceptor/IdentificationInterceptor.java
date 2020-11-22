package com.kakaopay.throwmoney.util.interceptor;

import com.kakaopay.throwmoney.common.exception.ApiException;
import com.kakaopay.throwmoney.common.support.Identification.Identify;
import com.kakaopay.throwmoney.dto.type.http.CustomHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.handler.DispatcherServletWebRequest;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class IdentificationInterceptor extends WebRequestHandlerInterceptorAdapter {

    @Autowired
    private Identify identify;

    /**
     * Create a new WebRequestHandlerInterceptorAdapter for the given WebRequestInterceptor.
     *
     * @param requestInterceptor the WebRequestInterceptor to wrap
     */
    public IdentificationInterceptor(WebRequestInterceptor requestInterceptor) {
        super(requestInterceptor);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
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
        return true;
    }
}
