/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.russound.rnet.internal;

import org.eclipse.smarthome.core.library.types.DecimalType;

public class SourceChangeParser implements BusParser {

    public SourceChangeParser() {
    }

    @Override
    public boolean matches(Byte[] bytes) {
        return (bytes[0] == (byte) 0xF0 && bytes[6] == (byte) 0x7f && bytes[7] == (byte) 0x06
                && bytes[12] == (byte) 0x05);
    }

    @Override
    public ZoneStateUpdate process(Byte[] bytes) {
        if (matches(bytes)) {
            return new ZoneStateUpdate(new ZoneId(bytes[1] + 1, bytes[2] + 1), RNetConstants.CHANNEL_ZONESOURCE,
                    new DecimalType(bytes[9] + 1));
        } else {
            return null;
        }
    }
    //
    // @Override
    // public ZoneAction process(Byte[] bytes) {
    // if (matches(bytes)) {
    // return null;// new ZoneAction(RNetConstants.CHANNEL_ZONESOURCE, new DecimalType(bytes[9] + 1));
    // } else {
    // return null;
    // }
    // }

}