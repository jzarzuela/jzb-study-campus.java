/*
 * Copyright (c) 2002-2008 Gargoyle Software Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include the following acknowledgment:
 *
 *       "This product includes software developed by Gargoyle Software Inc.
 *        (http://www.GargoyleSoftware.com/)."
 *
 *    Alternately, this acknowledgment may appear in the software itself, if
 *    and wherever such third-party acknowledgments normally appear.
 * 4. The name "Gargoyle Software" must not be used to endorse or promote
 *    products derived from this software without prior written permission.
 *    For written permission, please contact info@GargoyleSoftware.com.
 * 5. Products derived from this software may not be called "HtmlUnit", nor may
 *    "HtmlUnit" appear in their name, without prior written permission of
 *    Gargoyle Software Inc.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GARGOYLE
 * SOFTWARE INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gargoylesoftware.htmlunit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;

import com.gargoylesoftware.htmlunit.javascript.host.Window;

/**
 * <span style="color:red">INTERNAL API - SUBJECT TO CHANGE AT ANY TIME - USE AT YOUR OWN RISK.</span><br/>
 *
 * This is a class that provides thread handling services to internal clients
 * as well as exposes some of the status of these threads to the public API.
 *
 * @version $Revision: 2905 $
 * @author Brad Clarke
 * @author Marc Guillemot
 * @author Daniel Gredler
 */
public class ThreadManager {

    /** Logging support. */
    private static final Log LOG = LogFactory.getLog(ThreadManager.class);

    /** Map of threads, keyed on thread ID. Use a tree map for deterministic iteration. */
    private Map<Integer, Thread> threadMap_ = Collections.synchronizedMap(new TreeMap<Integer, Thread>());

    private final WebWindow window_;

    ThreadManager(final WebWindow window) {
        window_ = window;
    }

    /**
     * @return the number of tracked threads
     */
    public int activeCount() {
        return threadMap_.size();
    }

    /**
     * Threads are given numeric IDs in JavaScript and that number is
     * stored here.
     */
    private int nextThreadID_ = 1;

    /**
     * HtmlUnit threads are started at a higher priority than the priority
     * of the first thread to ask for HtmlUnit thread handling services. Assuming
     * there are no users screwing with their own thread priority after starting
     * a test this should be enough to encourage background threads to execute
     * quicker than the test they were started from, if possible.
     */
    private static final int PRIORITY = Math.min(Thread.MAX_PRIORITY, Thread
            .currentThread()
            .getPriority() + 1);

    /**
     * Gets the next thread ID in a threadsafe way.
     * @return the next ID
     */
    private synchronized int getNextThreadId() {
        return nextThreadID_++;
    }

    /**
     * <span style="color:red">INTERNAL API - SUBJECT TO CHANGE AT ANY TIME - USE AT YOUR OWN RISK.</span><br/>
     *
     * Starts a new job on a background thread. Threads started by this method
     * are always considered daemon threads and will not prevent the JVM from
     * shutting down. For our purposes the JUnit test is the only thread that
     * matters and the background threads are just there to keep the HtmlUnit
     * objects as up to date as possible.
     *
     * @param job the job to start
     * @param label a job description
     * @return the ID of the new thread, suitable for use in JavaScript and required
     * when calling {@link #stopThread(int)}
     */
    public int startThread(final Runnable job, final String label) {
        final int myThreadID = getNextThreadId();
        final Thread newThread = new Thread(job, "HtmlUnit Managed Thread #" + myThreadID + ": " + label) {
            @Override
            public void run() {
                try {
                    super.run();
                }
                finally {
                    threadMap_.remove(new Integer(myThreadID));
                }
            }
        };
        newThread.setPriority(PRIORITY);
        newThread.setDaemon(true);
        threadMap_.put(new Integer(myThreadID), newThread);
        newThread.start();
        return myThreadID;
    }

    /**
     * <span style="color:red">INTERNAL API - SUBJECT TO CHANGE AT ANY TIME - USE AT YOUR OWN RISK.</span><br/>
     *
     * Stops a thread that was started in this thread manager.
     *
     * Note: this does not immediately stop the thread, only interrupt
     * it and remove it from being tracked by this manager. The thread
     * is responsible for handling being interrupted properly and shutting
     * itself down.
     *
     * @param threadID the ID of the thread to stop
     */
    public void stopThread(final int threadID) {
        final Thread thread = threadMap_.get(new Integer(threadID));
        if (thread != null) {
            thread.interrupt();
        }
    }

    /**
     * <span style="color:red">INTERNAL API - SUBJECT TO CHANGE AT ANY TIME - USE AT YOUR OWN RISK.</span><br/>
     *
     * Removes the job with the given id for schedule.
     * @param id the ID of the job to remove
     */
    // this should totally replace stopThread in the future
    public void removeJob(final int id) {
        stopThread(id);
    }

    /**
     * Wait for any executing background threads to complete.
     *
     * @param maxWaitMillis the maximum time that should be waited, in milliseconds
     * This is not an exact time but will be fairly close.
     * @return true if all threads expired in the specified time
     */
    public boolean joinAll(long maxWaitMillis) {
        for (Thread t = getNextThread(); t != null && maxWaitMillis > 0; t = getNextThread()) {
            final long before = System.currentTimeMillis();
            try {
                LOG.debug("Trying to join: " + t);
                t.join(maxWaitMillis);
            }
            catch (final InterruptedException e) {
                throw new RuntimeException("Thread " + t + " interrupted.", e);
            }
            maxWaitMillis = maxWaitMillis - (System.currentTimeMillis() - before);
        }
        return threadMap_.size() == 0;
    }

    /**
     * Gets the next thread in the thread map in a threadsafe way. This method returns <tt>null</tt>
     * if there are no more threads in the thread map.
     *
     * @return the next thread in the thread map
     */
    private Thread getNextThread() {
        final Thread thread;
        synchronized (threadMap_) {
            final Iterator<Thread> i = threadMap_.values().iterator();
            if (i.hasNext()) {
                thread = i.next();
            }
            else {
                thread = null;
            }
        }
        return thread;
    }

    /**
     * <span style="color:red">INTERNAL API - SUBJECT TO CHANGE AT ANY TIME - USE AT YOUR OWN RISK.</span><br/>
     * Attempts to stop running threads.
     */
    public void interruptAll() {
        synchronized (threadMap_) {
            for (final Integer id : new HashSet<Integer>(threadMap_.keySet())) {
                stopThread(id.intValue());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ThreadManager: " + threadMap_;
    }

    /**
     * <span style="color:red">INTERNAL API - SUBJECT TO CHANGE AT ANY TIME - USE AT YOUR OWN RISK.</span><br/>
     *
     * Registers a JavaScript code to be executed at the given time.
     * @param codeToExec the string or function to execute
     * @param timeout the time to wait before to executed the code
     * @param description the job description
     * @return the ID of the registered job, suitable for use in JavaScript and required
     * when calling {@link #removeJob(int)}
     */
    public int registerJob(final Object codeToExec, final int timeout, final String description) {
        final JavaScriptBackgroundJob job = createJavaScriptBackgroundJob(codeToExec, timeout, false, description);
        return startThread(job, description);
    }

    /**
     * <span style="color:red">INTERNAL API - SUBJECT TO CHANGE AT ANY TIME - USE AT YOUR OWN RISK.</span><br/>
     *
     * Registers a JavaScript code to be executed at the given intervals.
     * @param codeToExec the string or function to execute
     * @param interval the time to wait between two executions
     * @param description the job description
     * @return the ID of the registered job, suitable for use in JavaScript and required
     * when calling {@link #removeJob(int)}
     */
    public int registerRecurringJob(final Object codeToExec, final int interval, final String description) {
        final JavaScriptBackgroundJob job = createJavaScriptBackgroundJob(codeToExec, interval, true, description);
        return startThread(job, description);
    }
    
    /**
     * Makes the job object for setTimeout and setInterval
     *
     * @param codeToExec either a Function or a String of the JavaScript code
     * @param timeout time to wait
     * @param thisWindow the window to associate the thread with
     * @param loopForever if the thread should keep looping (setTimeout vs setInterval)
     * @return the job
     */
    private JavaScriptBackgroundJob createJavaScriptBackgroundJob(final Object codeToExec,
            final int timeout, final boolean loopForever, final String label) {
        final Window thisWindow = (Window) window_.getScriptObject();
        if (codeToExec == null) {
            throw Context.reportRuntimeError("Function not provided");
        }
        else if (codeToExec instanceof String) {
            final String scriptString = (String) codeToExec;
            return new JavaScriptBackgroundJob(thisWindow, timeout, scriptString, loopForever, label);
        }
        else if (codeToExec instanceof Function) {
            final Function scriptFunction = (Function) codeToExec;
            return new JavaScriptBackgroundJob(thisWindow, timeout, scriptFunction, loopForever, label);
        }
        else {
            throw Context.reportRuntimeError("Unknown type for function");
        }
    }
}
