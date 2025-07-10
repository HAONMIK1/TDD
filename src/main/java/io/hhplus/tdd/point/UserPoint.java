package io.hhplus.tdd.point;

public class UserPoint {

    private final long id;
    private final long point;
    private final long updateMillis;

    public UserPoint(long id, long point, long updateMillis) {
        this.id = id;
        this.point = point;
        this.updateMillis = updateMillis;
    }
    public long id() { return id; }
    public long point() { return point; }
    public long updateMillis() { return updateMillis; }

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPoint charge(long amount) {
        if (amount < PointPolicy.MIN_CHARGE_POINT)
            throw new IllegalArgumentException(PointPolicy.ERROR_MIN_CHARGE);

        if (amount > PointPolicy.MAX_CHARGE_POINT)
            throw new IllegalArgumentException(PointPolicy.ERROR_MAX_CHARGE);

        if (this.point + amount > PointPolicy.MAX_BALANCE)
            throw new IllegalStateException(PointPolicy.ERROR_MAX_BALANCE);

        return new UserPoint(id, point + amount, System.currentTimeMillis());
    }

    public UserPoint use(long amount) {
        if (amount <= PointPolicy.MIN_USE_POINT) throw new IllegalArgumentException(PointPolicy.ERROR_MIN_USE);
        if (this.point < amount)
            throw new IllegalStateException(PointPolicy.ERROR_BALANCE);
        return new UserPoint(id, point - amount, System.currentTimeMillis());
    }

}