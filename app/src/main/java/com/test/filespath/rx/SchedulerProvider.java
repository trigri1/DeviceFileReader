package com.test.filespath.rx;

import io.reactivex.Scheduler;

/**
 * @author goharali
 */

public interface SchedulerProvider {

    Scheduler ui();

    Scheduler computation();

    Scheduler io();

    Scheduler Queue();

}
