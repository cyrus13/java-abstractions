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
package net.anastasakis.util.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * This is a Utility class for operations that are interruptible. The exception is swallowed and the interrupt status
 * of the thread flagged appropriately. This allows for more elegant coding that avoids try/catch.
 *
 * In contrast to Guava Uninterruptibles that:
 * <quote>
 *     if a thread is interrupted during such a call, the call continues to block
 *     until the result is available or the timeout elapses, and only then re-interrupts the thread.
 * </quote>
 *
 * the helper methods in this class return immediately if the thread is interrupted.
 *
 * @author Cyrus13
 */
public final class Interruptibles {

    /**
     * Private constructor as this is a utilities class
     */
    private Interruptibles(){}

    /**
     * The semantics of this method are the same as the {@link CountDownLatch#await(long, TimeUnit)}
     * but instead of an {@link InterruptedException} this method returns immediately
     * the value {@code false} if the thread was interrupted.
     *
     * @see CountDownLatch#await(long, TimeUnit)
     *
     * @param latch the latch on which to away
     * @param timeout the maximum time to wait
     * @param unit the time unit of the {@code timeout} argument
     * @return  {@code true} if the count reached zero and
     *          {@code false} if the waiting time has been reached OR the thread has been interrupted.
     */
    public static boolean awaitOnLatch(CountDownLatch latch, long timeout, TimeUnit unit) {
        try {
            return latch.await(timeout, unit);
        } catch (InterruptedException e) {
            // Reset the flag
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public static void sleep(long duration, TimeUnit unit) {
        try {
            unit.sleep(duration);
        } catch (InterruptedException e) {
            // Reset the flag
            Thread.currentThread().interrupt();
        }
    }
}
