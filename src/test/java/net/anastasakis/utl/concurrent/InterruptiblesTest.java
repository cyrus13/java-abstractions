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
package net.anastasakis.utl.concurrent;

import net.anastasakis.util.concurrent.Interruptibles;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Test methods for {@link net.anastasakis.util.concurrent.Interruptibles} class.
 *
 * @author Cyrus13
 */
public class InterruptiblesTest {

    @Test
    public void testCountDownLatchReturns() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CountDownLatchWaiter waiter = new CountDownLatchWaiter(countDownLatch);
        final Thread thread = new Thread(waiter);
        thread.start();
        countDownLatch.countDown();
        // Allow for countDown to be notified
        mySleep(20);
        assertTrue(waiter.hasReturnValue());
        assertTrue(waiter.getActualReturnValue());
        assertFalse(thread.isInterrupted());
    }

    @Test
    public void testCountDownLatchTimesOut(){
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CountDownLatchWaiter waiter = new CountDownLatchWaiter(countDownLatch);
        final Thread thread = new Thread(waiter);
        thread.start();
        // Wait more than the latch await time
        mySleep(3_000);
        assertTrue(waiter.hasReturnValue());
        assertFalse(waiter.getActualReturnValue());
        assertFalse(thread.isInterrupted());
    }

    @Test
    public void testCountDownLatchInterrupted() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CountDownLatchWaiter waiter = new CountDownLatchWaiter(countDownLatch);
        final Thread thread = new Thread(waiter);
        thread.start();
        thread.interrupt();
        if (thread.isAlive()) {
            assertTrue(thread.isInterrupted());
        }
        mySleep(10);
        assertTrue(waiter.hasReturnValue());
        assertFalse(waiter.getActualReturnValue());
    }

    @Test
    public void testMySleepFinish(){
        final Sleeper sleeper = new Sleeper();
        final Thread thread = new Thread(sleeper);
        thread.start();
        mySleep(2_100);
        assertTrue(sleeper.isFinishedSleeping());
        assertFalse(thread.isInterrupted());
    }

    @Test
    public void testMySleepInterrupt(){
        final Sleeper sleeper = new Sleeper();
        final Thread thread = new Thread(sleeper);
        thread.start();
        mySleep(30);
        thread.interrupt();
        assertFalse(sleeper.isFinishedSleeping());
        if (thread.isAlive()) {
            assertTrue(thread.isInterrupted());
        }
        // Give some time for the thread to be interrupted,
        // but less than the total sleep time
        mySleep(30);
        assertTrue(sleeper.isFinishedSleeping());
    }

    private static void mySleep(long waitInMillis){
        try {
            Thread.sleep(waitInMillis);
        } catch (InterruptedException e) {
            // This will never happen as this is the test thread
            fail("Interrupted exception thrown during test");
        }
    }

    private static class CountDownLatchWaiter implements Runnable {
        private final CountDownLatch countDownLatch;
        private Optional<Boolean> returnValue = Optional.empty();

        public CountDownLatchWaiter(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            this.returnValue = Optional.of(Interruptibles.awaitOnLatch(countDownLatch,2,TimeUnit.SECONDS));
        }

        public boolean hasReturnValue() {
            return returnValue.isPresent();
        }

        public boolean getActualReturnValue(){
            return returnValue.get();
        }
    }

    private static class Sleeper implements Runnable{
        private boolean finishedSleeping = false;

        @Override
        public void run() {
            Interruptibles.sleep(2,TimeUnit.SECONDS);
            this.finishedSleeping = true;
        }

        public boolean isFinishedSleeping() {
            return finishedSleeping;
        }
    }
}
