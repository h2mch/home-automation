package ch.h2m.home.automation;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

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