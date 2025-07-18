package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    void 여러번_충전_사용() {
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

    @Test
    void 동일사용자_여러번_동시충전() throws Exception {
        long userId = 1L;
        long amount = 5000L;
        int threadCount = 6;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.chargePoint(userId, amount);
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        UserPoint userPoint = pointService.getUserPoint(userId);
        assertEquals(amount*threadCount, userPoint.point());
    }

    @Test
    void 동일사용자_여러번_동시사용() throws Exception {
        long userId = 1L;
        long amount1 = 5000L;
        long amount2 = 100L;
        int threadCount = 6;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        pointService.chargePoint(userId, amount1);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.usePoint(userId, amount2);
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        UserPoint userPoint = pointService.getUserPoint(userId);
        assertEquals(amount1-amount2*threadCount, userPoint.point());
    }

}