# 로그인 토큰 사용
- 자동 로그인 구현을 위해 JWT를 이용해 Access Token, Refresh Token을 발급하여 사용한다.
### ❓Access Token만 사용하면 안 되는가?
  - 로그인 여부는 유효한 Access Token을 가지고 있는지 여부로 판단하기 때문에, 
    **Access Token의 유효 기간을 무한정으로 길게 설정해야만 자동 로그인이 가능해 진다.**
  - 만약 유효 기간이 매우 긴 Access Token을 탈취당하는 경우 해커가 자유롭게 로그인을 할 수 있게 된다.
  - 이를 방지하기 위해, **Access Token의 유효 기간을 짧게 두고 Refresh Token을 사용해서 Access Token을 갱신해 로그인을 유지 시키는 방식을 채택했다.**
  - Refresh Token도 탈취의 위험에서 자유로울 수는 없으므로 **Sliding Expiration** 방식을 채택해 조건에 따라 갱신한다.

## 토큰 정책
1. 로그인하면 Access Token과 Refresh Token을 발급한다. 이 때, Refresh Token은 DB에 저장한다.
2. 클라이언트에서 요청을 보낼 때마다 Header에 Access Token을 담아 서버에 요청한다.
   1. 만료된 Access Token으로 요청하는 경우
      - 서버에서 401 응답 리턴 
      - 401 응답을 받은 클라이언트는 Access Token과 Refresh Token을 같이 보냄
        - 만료기간이 지났다는 것을 확인해 바로 refresh 요청을 보냄
        -  refresh 요청을 받은 서버는 만료된 Access Token을 통해 회원 정보를 뽑아내고, 그 정보와 매치되는 Refresh Token을 db에서 가져온다.
5.  가져온 Refresh Token을 클라이언트로부터 받은 Refresh Token과 일치하는지 확인한다.
6.a. 일치한다면, 새로운 Access Token을 발급해준다.
6.b. 일치하지 않는다면, Refresh Token이 유효하지 않은 것이니 401 응답을 보내고 클라이언트는 재 로그인을 하게된다.

# Access Token 재발급 시 Refresh Token도 재발급하는 것이 좋을까?
## ✅ Refresh Token 재발급 여부를 결정하는 기준
Access Token이 만료되었을 때 Refresh Token을 재발급할지 여부는 보안성과 UX(사용자 경험) 간의 균형을 고려해야 한다.

1. Refresh Token을 재발급하는 경우
   - 보안 강화 
     - Refresh Token의 사용 횟수를 최소화할 수 있음.
     - 일정한 주기로 새로운 Refresh Token을 제공하여 탈취 위험을 줄일 수 있음.
   - 만료 기간 연장 가능
     - Refresh Token의 만료 기한을 자동으로 갱신할 수 있어 장기간 로그인 유지 가능.
2. 기존 Refresh Token을 유지하는 경우
   - 불필요한 리프레시 토큰 교체 방지
     - Refresh Token이 자주 바뀌면 DB 업데이트가 많아지고, 클라이언트가 최신 Refresh Token을 관리하는 부담이 생김.
   - 탈취 위험이 증가할 가능성 있음
     - Refresh Token이 자주 교체되면 공격자가 이전 Refresh Token을 탈취했을 경우 무효화하기 어려울 수 있음.

## ✅ 추천 전략: "Sliding Expiration" 방식 적용
   대부분의 보안 시스템에서는 "Sliding Expiration" 방식을 사용한다.

   즉, Access Token이 만료될 때 Refresh Token을 검증하고, 일정 조건에 따라 Refresh Token을 갱신하는 방식이다.

### 권장 로직
| 상황                                    | Access Token 재발급 | Refresh Token 재발급 |
|---------------------------------------|------------------|-------------------|
| Access Token이 만료됨, Refresh Token이 유효함 | ✅ 발급             | 🔄 특정 조건 충족 시 재발급 |
| Refresh Token이 만료됨                    | ❌ 재로그인 요구        | ❌ 재로그인 요구         |
| Refresh Token이 탈취됨 (의심)               | ❌ 재로그인 요구        | ❌ 재로그인 요구         |



### 구체적인 구현 방식
1. Refresh Token의 남은 유효 기간이 일정 이하일 때만 재발급
   - 예: Refresh Token의 유효 기간이 30일인데, 7일 이하로 남으면 새로 발급
   - 이 방식은 보안을 유지하면서도 불필요한 토큰 재발급을 최소화할 수 있음.
2. Refresh Token이 갱신되면 기존 Refresh Token은 무효화
   - DB에서 해당 사용자의 기존 Refresh Token을 갱신
   - 이전 Refresh Token을 사용한 요청은 거부

## ✅ 최종 결론: Refresh Token을 항상 재발급할 필요는 없음!
1. Access Token을 재발급할 때마다 Refresh Token을 교체할 필요는 없다.
**2. Refresh Token의 유효 기간이 일정 이하로 남아있을 때만 새로 발급하는 방식이 가장 이상적이다.**
3. Refresh Token을 교체할 경우 기존 Refresh Token을 무효화하는 조치(DB 업데이트 등)를 반드시 수행해야 한다.
4. Refresh Token이 만료되었거나 탈취 가능성이 있는 경우, 재로그인을 강제하는 것이 보안상 가장 안전하다.

**즉, Access Token을 재발급할 때는 기본적으로 Refresh Token을 유지하고, 필요할 때만 새로 발급하는 것이 가장 합리적이다. 🚀**