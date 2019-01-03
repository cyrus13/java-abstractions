/**
 * Copyright 2018 Cyrus13
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
package net.anastasakis.util.function;

import net.anastasakis.util.Numbers;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * This class monitors a {@link Predicate} with the ability to retry until its value is {@code true}.
 *
 * The main usage for this abstraction is cases when we need to wait until a condition is satisfied
 * but the framework we use does not support even based notifications. In such cases it is customary
 * to poll until the condition we want is satisfied.
 *
 * For example assume that you use a third-party library that sets a variable to a specific value and
 * you need to wait until that value is set before you proceed.
 *
 * @author  Cyrus13
 */
public class PollingPredicateMonitor<T>{

    private final Predicate<T> delegage;
    private final long pollingInterval;
    private final TimeUnit pollingUnit;

    /**
     * Constructs a new instance of a {@code PollingPredicateMonitor}
     *
     * @param delegage the {@link Predicate} which to test
     * @param pollingInterval the polling interval
     * @param pollingUnit
     * @throws NullPointerException if the {@code delegate} or the {@code pollingUnit} are null
     * @throws IllegalArgumentException if the {@code pollingInterval} is 0 or negative
     */
    public PollingPredicateMonitor(Predicate<T> delegage, long pollingInterval, TimeUnit pollingUnit) {
        Objects.requireNonNull(delegage,"Predicate cannot be Null");
        Objects.requireNonNull(pollingUnit,"Polling unit cannot be Null");
        Numbers.requirePossitive(pollingInterval,"PollingInterval needs to be positive");
        this.delegage = delegage;
        this.pollingInterval = pollingInterval;
        this.pollingUnit = pollingUnit;
    }

    /**
     * Cause the thread to wait until the Predicate becomes {@code true} or the thread is interrupted.
     *
     * @param t the input argument to the {@link Predicate}
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public void awaitUntilTrue(T t) throws InterruptedException{
        checkForInterruptedAndThrowException();
        if (!delegage.test(t)){
           doSleepForPollingInterval();
        }
    }

    /**
     * Causes the thread to wait until:
     * 1) the predicate becomes {@code true} OR
     * 2) the timeout in the provided {@link TimeUnit} elapses OR
     * 3) the thread was Interrupted in which case an {@link InterruptedException} will be thrown
     *
     * Note:
     *
     * @param t the input argument to the {@link Predicate}
     * @param timeOut the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @return {@code true} if the predicate returned {@code true}.
     *         {@code false} if the timeout elapsed without the predicate returning true.
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public boolean awaitUntilTrue(T t, long timeOut, TimeUnit unit) throws InterruptedException{
        if (delegage.test(t)){
            return true;
        }

        final long now = System.currentTimeMillis();
        final long terminationTime = now + unit.toMillis(timeOut);

        while (System.currentTimeMillis() < terminationTime){
            checkForInterruptedAndThrowException();
            if (delegage.test(t)){
                return true;
            }
           doSleepForPollingInterval();
        }

        return false;
    }

    void checkForInterruptedAndThrowException() throws InterruptedException {
        if (Thread.interrupted()){
            throw new InterruptedException();
        }
    }

    void doSleepForPollingInterval() throws InterruptedException {
        pollingUnit.sleep(pollingInterval);
    }
}
