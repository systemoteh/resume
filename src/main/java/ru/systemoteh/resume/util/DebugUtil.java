package ru.systemoteh.resume.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class DebugUtil {
    public static void turnOnShowSQL() {
        Logger sqlLogger = (Logger) LoggerFactory.getLogger("org.hibernate.SQL");
        sqlLogger.setLevel(Level.DEBUG);
        Logger descLogger = (Logger) LoggerFactory.getLogger("org.hibernate.type.descriptor.sql.BasicBinder");
        descLogger.setLevel(Level.TRACE);
    }

    public static void turnOffShowSQL() {
        Logger sqlLogger = (Logger) LoggerFactory.getLogger("org.hibernate.SQL");
        sqlLogger.setLevel(Level.INFO);
        Logger descLogger = (Logger) LoggerFactory.getLogger("org.hibernate.type.descriptor.sql.BasicBinder");
        descLogger.setLevel(Level.INFO);
    }
}
