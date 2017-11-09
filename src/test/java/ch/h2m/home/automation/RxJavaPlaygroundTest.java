package ch.h2m.home.automation;

import org.junit.jupiter.api.Test;

import java.util.Calendar;

import ch.h2m.home.automation.entity.HueDimmerState;
import io.reactivex.Observable;
import io.reactivex.Single;

import static org.junit.jupiter.api.Assertions.*;

class RxJavaPlaygroundTest {

    @Test
    void operationDistinctUntilChanged() throws InterruptedException {

        Calendar cal1 = Calendar.getInstance();
        Thread.sleep(200);
        Calendar cal2 = Calendar.getInstance();

        Observable<HueDimmerState> hueDimmerStateObservable = Observable.fromArray(
                new HueDimmerState(10, cal1),
                new HueDimmerState(12, cal1),
                new HueDimmerState(12, cal1),
                new HueDimmerState(12, cal2),
                new HueDimmerState(12, cal2),
                new HueDimmerState(10, cal1)
        );

        Single<Long> count = hueDimmerStateObservable
                .distinctUntilChanged()
                .count();

        assertEquals(4, count.blockingGet().intValue());

    }


}