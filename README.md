동시성 제어 방식 및 각 적용의 장/단점

1. synchronized 키워드

설명
  Java에서 가장 기본적인 동기화 방법으로, 메서드나 블록에 synchronized를 선언하여 한 번에 한 스레드만 접근하도록 제한합니다. 내부적으로 모니터 락(monitor lock)을 사용해 임계 영역(critical section)을 보호합니다.

장점

  문법이 간단하고 직관적이며 JVM이 기본 지원
  
  별도의 라이브러리 없이 객체 단위 락을 쉽게 구현 가능
  
  기본적인 동기화 요구에 적합

단점

  락 해제 시점을 개발자가 직접 제어할 수 없어, 잘못 사용 시 데드락 위험
  
  락 경쟁이 심하면 전체 성능 저하 발생
  
  락의 공정성(fairness)이 보장되지 않아 스레드가 언제 락을 얻을지 예측 불가
  
  락 범위가 크면 병목 현상 유발 가능

2. java.util.concurrent.locks.ReentrantLock
설명
  Lock 인터페이스 구현체로, synchronized보다 세밀한 락 제어가 가능합니다. 락 획득과 해제 시점을 개발자가 직접 제어하며, 공정성 옵션 설정과 조건 변수(Condition) 지원을 제공합니다.

장점

  공정성(fairness) 옵션으로 락 요청 순서 보장 가능
  
  Condition을 통한 스레드 간 통신 및 대기/신호 기능 제공
  
  락 획득 시도(tryLock()), 타임아웃(tryLock(timeout, unit)) 기능 제공으로 유연성 증가
  
  복잡한 동기화 제어가 필요할 때 적합

단점

  코드 복잡도가 증가하고, 락 해제(unlock())를 보장하기 위해 반드시 try-finally 구조 사용 필요
  
  락 해제를 누락하면 데드락 발생 위험 존재
  
  synchronized보다 문법이 불편하고 예외 처리 코드가 필요

3. java.util.concurrent.atomic 패키지 (Atomic 변수들)
설명
  CAS(Compare-And-Swap) 기반의 원자적 연산을 제공하는 클래스들(AtomicInteger, AtomicLong, AtomicReference 등)로, 락 없이 동시성 제어가 가능합니다.

장점
  
  락을 사용하지 않아 컨텍스트 스위칭 및 락 경합 비용 감소
  
  매우 높은 성능과 낮은 지연 시간 제공
  
  단순 원자 연산(카운터 증가, 플래그 설정 등)에 최적화

단점

  복잡한 연산이나 임계 영역 보호에는 적합하지 않음
  
  여러 변수를 동시에 원자적으로 업데이트하는 복합 작업에 한계
  
  CAS 실패 시 무한 재시도 루프로 인해 CPU 자원 소모 가능
