/**
 * Copyright (c) 2014-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nibeheatpump.internal.message;

import javax.xml.bind.DatatypeConverter;

import org.openhab.binding.nibeheatpump.internal.NibeHeatPumpException;
import org.openhab.binding.nibeheatpump.internal.protocol.NibeHeatPumpProtocol;

public abstract class NibeHeatPumpBaseMessage implements NibeHeatPumpMessage {

    public static MessageType getMessageType(byte messageType) {
        for (MessageType p : MessageType.values()) {
            if (p.toByte() == messageType) {
                return p;
            }
        }
        return MessageType.UNKNOWN;
    }

    public enum MessageType {
        MODBUS_DATA_READ_OUT_MSG(NibeHeatPumpProtocol.CMD_MODBUS_DATA_MSG),
        MODBUS_READ_REQUEST_MSG(NibeHeatPumpProtocol.CMD_MODBUS_READ_REQ),
        MODBUS_READ_RESPONSE_MSG(NibeHeatPumpProtocol.CMD_MODBUS_READ_RESP),
        MODBUS_WRITE_REQUEST_MSG(NibeHeatPumpProtocol.CMD_MODBUS_WRITE_REQ),
        MODBUS_WRITE_RESPONSE_MSG(NibeHeatPumpProtocol.CMD_MODBUS_WRITE_RESP),

        UNKNOWN(-1);

        private final int msgType;

        MessageType(int msgType) {
            this.msgType = msgType;
        }

        MessageType(byte msgType) {
            this.msgType = msgType;
        }

        public byte toByte() {
            return (byte) msgType;
        }

    }

    public byte[] rawMessage;
    public MessageType msgType = MessageType.UNKNOWN;
    public byte msgId = 0;

    public NibeHeatPumpBaseMessage() {

    }

    public NibeHeatPumpBaseMessage(byte[] data) throws NibeHeatPumpException {
        encodeMessage(data);
    }

    @Override
    public void encodeMessage(byte[] data) throws NibeHeatPumpException {
        data = NibeHeatPumpProtocol.checkMessageChecksumAndRemoveDoubles(data);
        rawMessage = data;
        msgId = data[1];

        byte messageTypeByte = NibeHeatPumpProtocol.getMessageType(data);
        msgType = NibeHeatPumpBaseMessage.getMessageType(messageTypeByte);
    }

    @Override
    public String toString() {
        String str = "";
        str += "Message type = " + msgType;
        return str;
    }

    @Override
    public String toHexString() {
        if (rawMessage == null) {
            return null;
        } else {
            return DatatypeConverter.printHexBinary(rawMessage);
        }
    }

}
