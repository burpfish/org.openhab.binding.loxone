/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.loxone.core;

import org.openhab.binding.loxone.core.LxJsonApp3.LxJsonControl;

/**
 * An InfoOnlyAnalog type of control on Loxone Miniserver.
 * <p>
 * According to Loxone API documentation, this control covers analog virtual states only. This control does not send any
 * commands to the Miniserver. It can be used to read a formatted representation of an analog virtual state.
 *
 * @author Pawel Pieczul - initial commit
 *
 */
public class LxControlInfoOnlyAnalog extends LxControl {

    /**
     * A name by which Miniserver refers to analog virtual state controls
     */
    private static final String TYPE_NAME = "infoonlyanalog";
    /**
     * InfoOnlyAnalog state with current value
     */
    private static final String STATE_VALUE = "value";
    /**
     * InfoOnlyAnalog state with error value
     */
    @SuppressWarnings("unused")
    private static final String STATE_ERROR = "error";

    private String format;

    /**
     * Create InfoOnlyAnalog control object.
     *
     * @param client
     *            communication client used to send commands to the Miniserver
     * @param uuid
     *            control's UUID
     * @param json
     *            JSON describing the control as received from the Miniserver
     * @param room
     *            room to which control belongs
     * @param category
     *            category to which control belongs
     */
    LxControlInfoOnlyAnalog(LxWsClient client, LxUuid uuid, LxJsonControl json, LxContainer room, LxCategory category) {
        super(client, uuid, json, room, category);
        if (json.details != null && json.details.format != null) {
            format = json.details.format;
        } else {
            format = "%.2f";
        }
    }

    /**
     * Check if control accepts provided type name from the Miniserver
     *
     * @param type
     *            name of the type received from Miniserver
     * @return
     *         true if this control is suitable for this type
     */
    public static boolean accepts(String type) {
        return type.toLowerCase().equals(TYPE_NAME);
    }

    /**
     * Obtain current value of an analog virtual state, expressed in a format configured on the Miniserver
     *
     * @return
     *         string for the value of the state or null if current value is not compatible with this control
     */
    public String getFormattedValue() {
        LxControlState state = getState(STATE_VALUE);
        if (state != null) {
            return String.format(format, state.getValue());
        }
        return null;
    }
}
