package com.kakaopay.throwmoney.util.random;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class RandomGenerator {
    private SecureRandom secureRandom = new SecureRandom();

    public String generateSecureRandomToken() {
        Stream<Character> pwdStream = Stream.concat(getRandomNumbers(1), Stream.concat(getRandomAlphabets(1, true), getRandomAlphabets(1, false)));
        List<Character> charList = pwdStream.collect(Collectors.toList());
        Collections.shuffle(charList);
        String token = charList.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        return token;
    }

    public Stream<Character> getRandomAlphabets(int count, boolean upperCase) {
        IntStream characters = null;
        if (upperCase) {
            characters = secureRandom.ints(count, 65, 90);
        } else {
            characters = secureRandom.ints(count, 97, 122);
        }
        return characters.mapToObj(data -> (char) data);
    }

    public Stream<Character> getRandomNumbers(int count) {
        IntStream numbers = secureRandom.ints(count, 48, 57);
        return numbers.mapToObj(data -> (char) data);
    }

    public Stream<Character> getRandomSpecialChars(int count) {
        IntStream specialChars = secureRandom.ints(count, 33, 45);
        return specialChars.mapToObj(data -> (char) data);
    }

    public long throwMoneyDivider(long amount, long memberCnt) {
        long min = 1;
        long max = amount - memberCnt + 1;
        return (min == max) ? min : (memberCnt == 1) ? max : secureRandom.longs(1, min, max).sum();
    }
}
