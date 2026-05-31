package io.hhplus.tdd.point;

public class PointPolicy {
    public static final long MAX_BALANCE = 1_000_000L;// 최대잔고
    public static final long MIN_CHARGE_POINT = 100L;// 최소충전
    public static final long MAX_CHARGE_POINT = 100_000L;// 최대충전
    public static final long MIN_USE_POINT = 100L;//최소사용

    public static final String ERROR_MIN_CHARGE = "최소충전금액 미만";
    public static final String ERROR_MAX_CHARGE = "최대충전금액 초과";
    public static final String ERROR_MAX_BALANCE = "최대잔고 초과";
    public static final String ERROR_MIN_USE = "최소사용금액 미만";
    public static final String ERROR_BALANCE = "잔고 부족";
}
