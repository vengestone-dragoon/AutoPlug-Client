/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.autoplug.client.network.online;

import com.osiris.autoplug.client.network.utils.ClientAuthenticationAtServer;
import com.osiris.autoplug.core.logger.AL;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * This class was built to be extended.
 * Can only be created if the MainConnection already was established.
 * If not the server will reject the connection.
 * Also this connection needs to be authenticated using the server key.
 * Active when the user is logged in on the website.
 */
public class SecondaryConnection {
    private byte auth_id; // Very important to identify the connection
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public SecondaryConnection(byte auth_id) {
        this.auth_id = auth_id;
    }

    public boolean open() throws Exception {
        ClientAuthenticationAtServer auth = new ClientAuthenticationAtServer(auth_id);
        if (auth.isSuccess()){
            socket = auth.getSocket();
            in = auth.getIn();
            out = auth.getOut();
        }
        return true;
    }

    public boolean close(){
        try{
            if (in!=null) in.close();
            if (out!=null) out.close();
            if (socket.isConnected()) socket.close();
        } catch (Exception e) {
            AL.warn(this.getClass(), e,"Failed to stop/close connection.");
            return false;
        }
        return true;
    }

    public boolean isConnected(){
        if (socket==null || socket.isClosed()) return false;
        else return true;
    }

    public byte getAuthId() {
        return auth_id;
    }

    public Socket getSocket() {
        return socket;
    }

    public InputStream getIn() {
        return in;
    }

    public OutputStream getOut() {
        return out;
    }
}
