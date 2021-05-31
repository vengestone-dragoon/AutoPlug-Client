/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.autoplug.client.network.online.connections;

import com.osiris.autoplug.client.configs.WebConfig;
import com.osiris.autoplug.client.minecraft.Server;
import com.osiris.autoplug.client.network.online.SecondaryConnection;
import com.osiris.autoplug.client.utils.NonBlockingPipedInputStream;
import com.osiris.autoplug.core.logger.AL;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;


/**
 * Read the InputStreams of AutoPlug and the Minecraft server and
 * send it to the AutoPlug server when the user is online.
 * Note that
 */
public class OnlineConsoleSendConnection extends SecondaryConnection {
    private static BufferedWriter bw;
    private static final NonBlockingPipedInputStream.WriteLineEvent<String> action = line -> {
        try {
            send(line);
        } catch (Exception e) {
            AL.warn("Failed to send message to online console!", e);
        }
    };

    public OnlineConsoleSendConnection() {
        super((byte) 2);  // Each connection has its own auth_id.
    }

    public static synchronized void send(String message) throws Exception {
        if (!message.contains("\n")) {
            bw.write(message + "\n");
        } else {
            bw.write(message);
        }
        bw.flush();

        AL.debug(OnlineConsoleSendConnection.class, "SENT LINE: " + message);
    }

    @Override
    public boolean open() throws Exception {
        try {
            if (new WebConfig().online_console_send.asBoolean()) {
                super.open();
                if (bw == null) {
                    Socket socket = getSocket();
                    socket.setSoTimeout(0);
                    bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    while (Server.NB_PIPED_IN == null)
                        Thread.sleep(1000); // Wait until we got a server outputstream

                    Server.NB_PIPED_IN.actionsOnWriteLineEvent.add(action);
                }
                AL.debug(this.getClass(), "Online-Console-SEND connected.");
                send("Online-Console-SEND connected at " + new Date() + ".");
                return true;
            } else {
                AL.debug(this.getClass(), "Online-Console-SEND functionality is disabled.");
                return false;
            }
        } catch (Exception e) {
            AL.warn(e, "There was an error connecting to the Online-Console.");
        }

        return true;
    }

    @Override
    public void close() throws IOException {

        try {
            Server.NB_PIPED_IN.actionsOnWriteLineEvent.remove(action);
        } catch (Exception ignored) {
        }

        try {
            if (bw != null)
                bw.close();
        } catch (Exception e) {
            AL.warn("Failed to close writer.", e);
        }

        try {
            super.close();
        } catch (Exception e) {
            AL.warn("Failed to close connection.", e);
        }

        bw = null;
    }
}