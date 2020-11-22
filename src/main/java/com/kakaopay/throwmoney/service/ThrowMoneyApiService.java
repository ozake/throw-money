package com.kakaopay.throwmoney.service;

import com.kakaopay.throwmoney.util.random.RandomGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThrowMoneyApiService {

    @Autowired
    private RandomGenerator randomGenerator;

    public String generateToken() {
        return randomGenerator.generateSecureRandomToken();
    }
}
