package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class PointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    private static final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    public ReentrantLock getLock(long id) {
        return lockMap.computeIfAbsent(id, k -> new ReentrantLock(true));
    }

    //포인트 조회
    public UserPoint getUserPoint(long id) {
        return userPointTable.selectById(id);
    }
    //포인트 내역 조회
    public List<PointHistory> getHistories(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }
    //포인트 충전
    public UserPoint chargePoint(long id, long amount) {
        ReentrantLock lock = getLock(id);
        lock.lock();

        try {
            UserPoint current = userPointTable.selectById(id);//포인트 조회
            UserPoint charged = current.charge(amount);//포인트 검증
            UserPoint updated = userPointTable.insertOrUpdate(id, charged.point());//포인트 저장
            pointHistoryTable.insert(id, amount, TransactionType.CHARGE, updated.updateMillis());//히스토리 저장

            return updated;
        }finally {
            lock.unlock();
        }
    }
    //포인트 사용
    public UserPoint usePoint(long id, long amount) {
        ReentrantLock lock = getLock(id);
        lock.lock();

        try {
            UserPoint current = userPointTable.selectById(id);//포인트 조회
            UserPoint used = current.use(amount);//포인트 검증
            UserPoint updated = userPointTable.insertOrUpdate(id, used.point());//포인트 저장
            pointHistoryTable.insert(id, amount, TransactionType.USE, updated.updateMillis());//히스토리 저장

            return updated;
        }finally {
            lock.unlock();
        }
    }
}
