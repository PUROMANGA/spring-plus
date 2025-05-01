# 개요

개인적으로 트러블슈팅을 잘 적지 않습니다, 그 이유는 보다보면 다 이해되고 웬만하면 외워져서 같은 실수를 잘 반복하지 않아서 그런 것 같습니다.
하지만 이번에는 회사에서 상사에게 보고서를 작성한다는 느낌으로 제 오류를 분석해 보았습니다.

귀중한 시간 내주셔서 읽어주시면 정말 감사할 것 같습니다.

# LEVEL2의 해설

## 6. JPA Cascade

사실 필살기, CascadeType.ALL을 사용하고 싶었습니다만... 여기에서는 그걸 원하지는 않아보여 PERSIST를 사용하게 되었습니다.
그건 둘째치고, 과제에 있는

- 할 일을 새로 저장할 시, 할 일을 생성한 유저는 담당자로 자동 등록되어야 합니다.
- JPA의 `cascade` 기능을 활용해 할 일을 생성한 유저가 담당자로 등록될 수 있게 해주세요.

를 읽었을 때, 솔직히 '아 이거 내부에서 cascade는 어떻게 작동하는 거지?' 라는 의문이 들었습니다.
키워드는 머리에 떠올랐습니다, 영속성, 영속성 전이. 그런데 이게 어떻게 돌아가는지 궁금해서 챗gpt한테 db형식으로 출력해보라고 했습니다.

그러니까 cascade 설정이 안되면 todo가 저장되는 과정 중에 todo는 저장되는데 영속성 전이가 안 되서 따로 상태를 관리해야한다는 불편함을 발견하였습니다.

일단은 이 정도로 알고 넘어가려 하는데 더 자세한 지식의 공유가 가능하다면 꼭 좀 부탁드립니다.

## 7. N+1

많은 수강생들이 진정으로 N+1문제를 이해하고 있지 않다고 저는 생각합니다.
일단 그건 저도 크게 다르지 않다고 생각합니다(저는 자만하지 않습니다!)

그래서 초반에 이 문제를 보고, Repository를 봤는데 큰 문제는 없어보였습니다.
그리고 나서 Service를 봐도 일단은 문제가 안 보였고(코드적 오류), 그래서 과제를 확인해보니 user를 조회하고 있더군요.

????????
그래서 다시 한 번 확인했습니다, 그러니까 특정 todo_id에 달려있는 commentList를 하나 씩 꺼내서 거기에 getUser을 하고 있더군요.
아, 솔직히 답답했습니다.

이러면 todo_id : 1에 달려있는 comment_id : 1의 유저 불러오고, comment_id : 2의 유저 불러오고, 이런 비효율적인 상황이 발생하게 됩니다.
(사실은 코드를 봤을 때부터 구려보였습니다.)

문제 해결 방법은 간단하다고 느꼈습니다만 조금 더 구체적으로 생각해보기로 하였습니다.

왜 JOIN USER을 했는데 이런 결과가 나왔을까? @Transactional도 붙여줘서 영속성도 적용됐고, LAZY로 연관관계 맺어서 프록시로 가져올 거고, 애시당초에 1차 캐시로 등록되서 거기서 USER 가져오는 거 아닌가?

이러한 질문의 본질적이고 이론적인 대답은 튜터님에게 듣고 싶습니다.

참고로 제 추론 + gpt에게 질문을 해서 정리를 한 정보로 말씀을 드리자면, user를 그냥 join하면 1차 캐시에 등록이 안 되서 이러한 문제가 발생을 한다고 하네요.
Lazy로 proxy설정을 했는데, 이때 getUser을 원래는 1차 캐시에서 가져와야하는데 등록이 안 되서 DB까지 갔다온다고 합니다!

그리고 여기 @Transactional(readOnly = true) 빼먹어서 넣어뒀습니다, 영속성 유지가 안되고 있더라구요.

## 8. QueryDSL

QueryDSL, 솔직히 말씀드리자면 저는 다른 사람들 코드도 많이 봤고 제 식으로 적어서... 자신은 많이 없습니다.
혹시나 이러한 표현보다는 이게 더 낫다, 이런 식으로 작성하는 게 훨씬 속도적인 면에서 빠르다, 이런 어드바이스가 있으면 꼭 좀 부탁드리겠습니다!

## 9. Spring Security

어우, 이걸 과제로 내시다니, 너무 힘들게 했습니다.
제 개인적인 생각인데 스프링 시큐리티는 gpt 돌리지 않는 이상 많은 수강생들이 힘들어할 것 같습니다.

일단 제가 시큐리티를 작성한 방식에 대해서 말씀 드리겠습니다.

우선적으로 UserDetail을 구현하려고 했고, 구현한 다음은 UserDeatil 서비스를 구현해서 loadUserByUsername을 구현하였습니다.

그 후 제가 제일 좋아하는 UsernamePasswordAuthenticationToken과 AuthenticationManager, SecurityContextHolder를 구현하였습니다.
UsernamePasswordAuthenticationToken에는 util에서 필요한 정보를 담아주고, Authentication을 활용해주기 위해서 필터에서

Authentication authentication = jwtUtil.getAuthentication(bearerJwt);
SecurityContextHolder.getContext().setAuthentication(authentication);

이런식으로 토큰에서 인가를 빼서 SecurityContextHolder에 넣어줬습니다. SecurityContextHolder는 인증객체를 저장해주는 역할을 하는 녀석으로서 Authentication과 SecurityContextHolder만 사용해도 Principal로 더 정확한 정보를 꺼내올 수 있기 때문에 저는 매우 애용하려고 노력하고 있습니다.



