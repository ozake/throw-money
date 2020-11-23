# 카카오페이 머니 뿌리기 

### GITHUB URL : https://github.com/ozake/throw-money

프로젝트 구성
-------------

 - Java 1.8
 - Spring boot 2.4
 - Mysql 5.7
 - Redis
 - docker
 
 실행방법
 -------------
    1. docker를 설치한다.(docker가 이미 있다면 스킵)
    2.  $ docker-compose up
    3. http://localhost:8080/swagger-ui.html#/로 접속한다
  
  핵심 문제 해결 전략
  -------------
- 토큰 발급
> - 랜덤한 난수를 생성하여 아스키 코드로 소문자, 대문자, 숫자를 만들어 낸다.
> - 난수 생성시 seed값으로 시간을 이용하는 Random클래스 대신, SecureRandom클래스를 사용
> - 시간을 이용한 Random의 경우 같은 시간을 사용하면 같은 난수가 나온다. 
- 뿌리기
> - token 값 + roomId로 만든 키값으로 redis에 조회하여 같은 값이 있을경우 새로운 token을 생성한다.
> - 조합된 키값으로 redis에 저장시 TTL을 10분으로 하여 10분간 생성된 경우를 체크 하게 된다.
> - redis에 저장될 value에는 생성시간, 만료시간, 뿌리기 테이블 키값, 토큰, userId, roomId 를 담은 Jwt(JSON web Token)를 생성하여 저장한다.
> - Jwt는 받기용 Jwt와 조회용 Jwt를 생성한다.
- 받기
> - token값과 roomId로 조합하여 redis에서 조회하여 해당하는 jwt를 가져온다.
> - jwt의 유효기간을 체크하여 10분 여부를 체크하며, 유효할 경우 뿌리기 테이블의 키값을 꺼내어 테이블에서 조회한다.
> - 해당 jwt의 userId를 이용하여 뿌리기 본인이 받기하는경우 실패를 반환한다.
> - 동시성 문제를 위해 트랜잭션의 isolation level을 REPEATABLE_READ를 적용
> - 중복 받기는 application에서 filter을 통해 검증
- 조회
> - 조회용 jwt를 통해 7일의 만료시간으로 기간을 체크한다.
> - 조회용 jwt를 얻기위한 키값으로 토큰값 + userId + roomId를 사용하여 뿌리기한 본인이 아닐경우 체크한다.

#### 클라이언트용도가 아닌데 왜 JWT를 쓰나요?
    처음 계획은 3자리 토큰의 유니크성을 보완하고자 하는 맵핑용 토큰을 생각. 
    해당 토큰에 정보도 같이 담기면 조회가 편할꺼라 생각하여 적용.
    그러나 받기용, 조회용을 나누고 보니 DB에 저장하기도 애매하여 설계 변경.
    Redis에 저장할 내용이 굳이 jwt가 아닌 일반적인 JSON 데이터여도 무방할것으로 보임.
    