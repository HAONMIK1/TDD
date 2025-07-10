package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PointServiceTest {

    private UserPointTable userPointTable;
    private PointHistoryTable pointHistoryTable;
    private PointService pointService;

    @BeforeEach
    void setUp() {
        userPointTable = new UserPointTable();
        pointHistoryTable = new PointHistoryTable();
        pointService = new PointService(userPointTable, pointHistoryTable);
    }

    @Test
    void 포인트조회_성공() {
        // Given
        long userId = 1L;
        long amount = 5000L;
        userPointTable.insertOrUpdate(userId,amount);

        // When
        UserPoint result = pointService.getUserPoint(userId);

        //Then
        assertEquals(amount, result.point());
    }
    @Test
    void 포인트충전_성공() {
        // Given
        long userId = 3L;
        long amount = 5000L;

        //When
        UserPoint result =  pointService.chargePoint(userId,amount);
        List<PointHistory> histories = pointService.getHistories(userId);

        //Then
        assertEquals(amount, result.point());
        assertEquals(userId, histories.get(0).userId());
        assertEquals(amount, histories.get(0).amount());
        assertEquals(TransactionType.CHARGE, histories.get(0).type());
    }
    @Test
    void 포인트_사용_성공() {
        // Given
        long userId = 3L;
        long amount1 = 5000L;
        long amount2 = 3000L;

        //When
        pointService.chargePoint(userId,amount1);
        UserPoint result =  pointService.usePoint(userId,amount2);
        List<PointHistory> histories = pointService.getHistories(userId);

        //Then
        assertEquals(amount1 - amount2, result.point());
        assertEquals(userId, histories.get(1).userId());
        assertEquals(amount2, histories.get(1).amount());
        assertEquals(TransactionType.USE, histories.get(1).type());
    }
    @Test
    void 포인트내역조회_성공() {
        // Given
        long userId = 3L;
        pointService.chargePoint(userId,5000L);

        // When
        List<PointHistory> result = pointService.getHistories(userId);

        // Then
        assertEquals(1, result.size());
        assertEquals(userId,result.get(0).userId());
        assertEquals(5000L,result.get(0).amount());
        assertEquals(TransactionType.CHARGE,result.get(0).type());
    }
    @Test
    void 최소충전_포인트미만_예외발생() {
        //Given
        long userId = 3L;
        long amount = 1L;

        //When & Then
        assertThatThrownBy(() -> pointService.chargePoint(userId, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PointPolicy.ERROR_MIN_CHARGE);
    }
    @Test
    void 최대충전포인트_초과_예외발생() {
        //Given
        long userId = 3L;
        long amount = 100_001L;

        //When & Then
        assertThatThrownBy(() -> pointService.chargePoint(userId, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PointPolicy.ERROR_MAX_CHARGE);
    }
    @Test
    void 최대포인트_충전_성공() {
        // Given
        long userId = 1L;
        for(int i=0 ; i<9 ; i++) {
            pointService.chargePoint(userId, 100_000L);
        }

        //When
        UserPoint result = pointService.chargePoint(userId, 100_000L);

        // Then
        assertEquals(1_000_000L, result.point());
    }
    @Test
    void 최대보유포인트_초과_예외발생() {
        // Given
        long userId = 1L;
        for(int i=0 ; i<10 ; i++) {
            pointService.chargePoint(userId, 100_000L);
        }
        // When & Then
        assertThatThrownBy(() -> pointService.chargePoint(userId, 100_000L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(PointPolicy.ERROR_MAX_BALANCE);
    }
    @Test
    void 음수_충전_예외() {
        // Given & When & Then
        assertThatThrownBy(() -> pointService.chargePoint(1L, -100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PointPolicy.ERROR_MIN_CHARGE);
    }
    @Test
    void 포인트_0원_사용_예외() {
        // Given & When
        pointService.chargePoint(1L, 1000L);

        // Then
        assertThatThrownBy(() -> pointService.usePoint(1L, 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PointPolicy.ERROR_MIN_USE);
    }
    @Test
    void 음수_사용_예외() {
        // Given & When
        pointService.chargePoint(1L, 1000L);

        // Then
        assertThatThrownBy(() -> pointService.usePoint(1L, -100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PointPolicy.ERROR_MIN_USE);
    }
    @Test
    void 포인트잔고_부족_예외발생() {
        // Given & When
        pointService.chargePoint(1L, 500L);

        // Then
        assertThatThrownBy(() -> pointService.usePoint(1L, 1000L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(PointPolicy.ERROR_BALANCE);
    }
}