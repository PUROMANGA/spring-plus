# SPRING PLUS

# LEVEL1의 해설

service를 보니 전체 Transactional을 적용시킨 걸 확인하였습니다. 그런데 read-only다보니 읽는 것만 가능해 실질적으로 saveTodo는 작동하지 않았습니다.
이것은 Transactional의 특징 때문에 이루어집니다.

Transactional은 해당 메소드가 실행되면 영속성으로 등록되고, 1차 캐시로 저장됩니다.
여기서 1차 캐시는 빠르게 불러오기 위해 영속성 컨텍스트 내부에 엔티티를 보관하는 저장소입니다.

그리고 Transactional을 read-only로 설정하게 된다면 마지막 더티 체킹 작업이 이루어지지 않으면서 오류가 발생하게 됩니다.

혹시 제가 애매하게 알고 있는 부분이나 부가 해설이 있으시다면 꼭 좀 부탁드리겠습니다!

# LEVEL2의 해설

급하게 또 변경이 생겨 추가를 하게 되었군요... 어쨌든 JWT를 구성하는 요소에 nickname을, entity에 컬럼을 추가해주고, dto에도 추가해줍니다.
그리고 nicknmae이 같은 게 있을 때 예외 처리를 해주었습니다만 이미 entity에서 unique = true로 처리해서 같은 nickname은 애초에 입력이 안 될텐데(email도 마찬가지)... 어쨌든 email도 예외처리를 던져주어서 저도 던지게 되었습니다.