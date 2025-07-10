package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserPointTest {

    @Test
    void 최소충전_포인트미만_예외발생() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 1000L, System.currentTimeMillis());
        long amount = 1L;

        // When & Then
        assertThatThrownBy(() -> userPoint.charge(amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PointPolicy.ERROR_MIN_CHARGE);
    }

    @Test
    void 최대충전포인트_초과_예외발생() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 1000L, System.currentTimeMillis());
        long amount = 100_001L;

        // When & Then
        assertThatThrownBy(() -> userPoint.charge(amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PointPolicy.ERROR_MAX_CHARGE);
    }

    @Test
    void 최대보유포인트_초과_예외발생() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 1_000_000L, System.currentTimeMillis());

        // When & Then
        assertThatThrownBy(() -> userPoint.charge(100L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(PointPolicy.ERROR_MAX_BALANCE);
    }

    @Test
    void 음수_충전_예외() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 1000L, System.currentTimeMillis());

        // When & Then
        assertThatThrownBy(() -> userPoint.charge(-100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PointPolicy.ERROR_MIN_CHARGE);
    }

    @Test
    void 포인트_0원_사용_예외() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 1000L, System.currentTimeMillis());

        // When & Then
        assertThatThrownBy(() -> userPoint.use(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PointPolicy.ERROR_MIN_USE);
    }

    @Test
    void 음수_사용_예외() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 1000L, System.currentTimeMillis());

        // When & Then
        assertThatThrownBy(() -> userPoint.use(-100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PointPolicy.ERROR_MIN_USE);
    }

    @Test
    void 포인트_부족_예외발생() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 500L, System.currentTimeMillis());

        // When & Then
        assertThatThrownBy(() -> userPoint.use(1000L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(PointPolicy.ERROR_BALANCE);
    }

    @Test
    void 포인트_비어있을때_통과() {
        // Given & When
        UserPoint empty = UserPoint.empty(99L);

        // Then
        assertEquals(99L, empty.id());
        assertEquals(0L, empty.point());
    }

}