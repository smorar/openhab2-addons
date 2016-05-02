/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homematic.internal.misc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.openhab.binding.homematic.internal.model.HmDatapointInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes a callback method either immediately or after a given delay for a datapoint.
 *
 * @author Gerhard Riegler - Initial contribution
 */
public class DelayedExecuter {
    private static final Logger logger = LoggerFactory.getLogger(DelayedExecuter.class);

    private Map<HmDatapointInfo, Timer> delayedEvents = new HashMap<HmDatapointInfo, Timer>();

    /**
     * Executes a callback method either immediately or after a given delay.
     */
    public void start(final HmDatapointInfo dpInfo, final double delay, final DelayedExecuterCallback callback)
            throws IOException, HomematicClientException {
        if (delay > 0.0) {
            synchronized (DelayedExecuter.class) {
                logger.debug("Delaying event for {} seconds: '{}'", delay, dpInfo);

                Timer timer = delayedEvents.get(dpInfo);
                if (timer != null) {
                    timer.cancel();
                }

                timer = new Timer();
                delayedEvents.put(dpInfo, timer);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        logger.debug("Executing delayed event for '{}'", dpInfo);
                        delayedEvents.remove(dpInfo);
                        try {
                            callback.execute();
                        } catch (Exception ex) {
                            logger.error(ex.getMessage(), ex);
                        }
                    }
                }, (long) (delay * 1000));
            }
        } else {
            callback.execute();
        }
    }

    /**
     * Stops all delayed events.
     */
    public void stop() {
        for (Timer timer : delayedEvents.values()) {
            timer.cancel();
        }
        delayedEvents.clear();
    }

    /**
     * Callback interface for the {@link DelayedExecuter}.
     *
     * @author Gerhard Riegler - Initial contribution
     */
    public interface DelayedExecuterCallback {

        public void execute() throws IOException, HomematicClientException;

    }

}