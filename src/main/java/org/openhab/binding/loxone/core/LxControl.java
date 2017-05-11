/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.loxone.core;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * A control of Loxone Miniserver.
 * <p>
 * It represents a control object on the Miniserver. Controls can represent an input, functional block or an output of
 * the Miniserver, that is marked as visible in the Loxone UI. Controls can belong to a {@link LxContainer} room and a
 * {@link LxCategory} category.
 *
 * @author Pawel Pieczul
 *
 */
public abstract class LxControl {

    /**
     * Command string used to set control's state to ON
     */
    public final static String CMD_ON = "On";
    /**
     * Command string used to set control's state to OFF
     */
    public final static String CMD_OFF = "Off";

    private String name;
    private LxContainer room;
    private LxCategory category;
    private Map<String, LxControlState> states = null;

    LxUuid uuid;
    LxWsClient socketClient;

    /**
     * Create a Miniserver's control object.
     *
     * @param client
     *            websocket client to facilitate communication with Miniserver
     * @param uuid
     *            UUID of this control
     * @param name
     *            Human readable name of this control
     * @param room
     *            Room that this control belongs to
     * @param category
     *            Category that this control belongs to
     * @param states
     *            A map of control's possible states with state UUID as a key
     */
    LxControl(LxWsClient client, LxUuid uuid, String name, LxContainer room, LxCategory category,
            Map<String, LxControlState> states) {
        socketClient = client;
        this.uuid = uuid;
        update(name, room, category, states);
    }

    /**
     * Operate a Miniserver's control object.
     * <p>
     * Checks if command is compatible with control and sends it.
     *
     * @param operation
     *            String describing particular operation to perform. It should be obtained from child class
     *            implementation.
     * @return
     *         true is operation succeeded, false is operation is not compatible with control
     * @throws IOException
     *             when something went wrong (depends on communication interface)
     */
    abstract public boolean operate(String operation) throws IOException;

    /**
     * Gets state object of given name, if exists
     *
     * @param name
     *            name of state object
     * @return
     *         state object
     */
    public LxControlState getState(String name) {
        if (states.containsKey(name)) {
            return states.get(name);
        }
        return null;
    }

    /**
     * Obtain control's name
     *
     * @return
     *         Human readable name of control
     */
    public String getName() {
        return name;
    }

    /**
     * Obtain UUID of this control
     *
     * @return
     *         UUID
     */
    public LxUuid getUuid() {
        return uuid;
    }

    /**
     * Obtain room that this control belongs to
     *
     * @return
     *         Control's room
     */
    public LxContainer getRoom() {
        return room;
    }

    /**
     * Obtain category of this control
     *
     * @return
     *         Control's category
     */
    public LxCategory getCategory() {
        return category;
    }

    /**
     * Compare UUID's of two controls -
     *
     * @param object
     *            Object to compare with
     * @return
     *         true if UUID of two objects are equal
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (object.getClass() != getClass()) {
            return false;
        }
        LxControl c = (LxControl) object;
        return Objects.equals(c.getUuid(), getUuid());
    }

    /**
     * Update Miniserver's control in runtime.
     *
     * @param name
     *            New human readable name
     * @param room
     *            New room that this control belongs to
     * @param category
     *            New category that this control belongs to
     * @param states
     *            a map of control states with state name as a key
     */
    void update(String name, LxContainer room, LxCategory category, Map<String, LxControlState> states) {
        this.name = name;
        this.room = room;
        this.category = category;
        this.states = states;
        for (LxControlState state : states.values()) {
            state.setControl(this);
        }
        uuid.setUpdate(true);
        room.addOrUpdateControl(this);
        category.addOrUpdateControl(this);
    }

}
