/*
 * Copyright (c) 2021-2022 Osiris-Team.
 * All rights reserved.
 *
 * This software is copyrighted work, licensed under the terms
 * of the MIT-License. Consult the "LICENSE" file for details.
 */

package com.osiris.autoplug.client.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * GlobalData, which is always static and used frequently in this project
 */
public class GD {
    // TODO make all of these not static and deprecate this class
    public static final String OFFICIAL_WEBSITE = "https://autoplug.one/";
    public static final String AUTHOR = "Osiris Team";
    @NotNull
    public static String VERSION = "AutoPlug-Client - v(ERROR RETRIEVING VERSION)";
    public static File WORKING_DIR;
    public static File PLUGINS_DIR;
    public static File DOWNLOADS_DIR;
    @Nullable
    public static File SERVER_JAR = null; // Gets set in UpdaterConfig

    static {
        WORKING_DIR = new File(System.getProperty("user.dir"));
        PLUGINS_DIR = new File(System.getProperty("user.dir") + "/plugins");
        DOWNLOADS_DIR = new File(System.getProperty("user.dir") + "/autoplug/downloads");
        try {
            VERSION = "AutoPlug-Client - " + new UtilsJar().getThisJarsAutoPlugProperties().getProperty("version");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
