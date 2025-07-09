package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PointServiceIntegrationTesti {

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
    void 여러번_충전_사용_() {
        long userId = 1L;
        pointService.chargePoint(userId, 5000L);
        pointService.chargePoint(userId, 2500L);
        pointService.usePoint(userId, 1000L);
        pointService.chargePoint(userId, 500L);
        pointService.usePoint(userId, 1500L);

        UserPoint userPoint = pointService.getUserPoint(userId);
        List<PointHistory> histories = pointService.getHistories(userId);

        assertEquals(5, histories.size());
        assertEquals(5500L, userPoint.point());
        assertEquals(TransactionType.CHARGE, histories.get(0).type());
        assertEquals(5000L, histories.get(0).amount());
        assertEquals(TransactionType.CHARGE, histories.get(1).type());
        assertEquals(2500L, histories.get(1).amount());
        assertEquals(TransactionType.USE, histories.get(2).type());
        assertEquals(1000L, histories.get(2).amount());
        assertEquals(TransactionType.CHARGE, histories.get(3).type());
        assertEquals(500L, histories.get(3).amount());
        assertEquals(TransactionType.USE, histories.get(4).type());
        assertEquals(1500L, histories.get(4).amount());
    }
}