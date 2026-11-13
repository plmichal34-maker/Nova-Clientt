package dev.novaclient.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NovaLog {
    private static final Logger LOG = LoggerFactory.getLogger("NovaClient");

    private NovaLog() {}

    public static void info(String msg) { LOG.info(msg); }

    public static void error(String msg, Throwable t) { LOG.error(msg, t); }
}
