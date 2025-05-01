# SPRING PLUS

#LEVEL1의 해설

service를 보니 전체 Transactional을 적용시킨 걸 확인하였습니다. 그런데 read-only다보니 읽는 것만 가능해 실질적으로 saveTodo는 작동하지 않았습니다.
이것은 Transactional의 특징 때문에 이루어집니다.

Transactional은 해당 메소드가 실행되면 영속성으로 등록되고, 1차 캐시로 저장됩니다.
여기서 1차 캐시는 빠르게 불러오기 위해 영속성 컨텍스트 내부에 엔티티를 보관하는 저장소입니다.

그리고 Transactional을 read-only로 설정하게 된다면 마지막 더티 체킹 작업이 이루어지지 않으면서 오류가 발생하게 됩니다.