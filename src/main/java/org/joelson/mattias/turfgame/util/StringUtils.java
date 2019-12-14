package org.joelson.mattias.turfgame.util;

import java.util.Objects;

public final class StringUtils {
    
    private StringUtils() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }
    
    public static String requireNullOrNonEmpty(String s) throws IllegalArgumentException {
        if (s != null && s.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return s;
    }
    
    public static String requireNullOrNonEmpty(String s, String message) throws IllegalArgumentException{
        if (s != null && s.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return s;
    }
    
    public static String requireNotNullAndNotEmpty(String s) throws NullPointerException, IllegalArgumentException {
        if (Objects.requireNonNull(s).isEmpty()) {
            throw new IllegalArgumentException();
        }
        return s;
    }
    
    public static String requireNotNullAndNotEmpty(String s, String nullMessage, String emptyMessage) throws NullPointerException, IllegalArgumentException {
        if (Objects.requireNonNull(s, nullMessage).isEmpty()) {
            throw new IllegalArgumentException(emptyMessage);
        }
        return s;
    }

    public static String requireNotNullAndNotTrimmedEmpty(String s) throws NullPointerException, IllegalArgumentException {
        if (Objects.requireNonNull(s).trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        return s;
    }
    
    public static String requireNotNullAndNotTrimmedEmpty(String s, String nullMessage, String emptyTrimmedMessage)
            throws NullPointerException, IllegalArgumentException {
        if (Objects.requireNonNull(s, nullMessage).trim().isEmpty()) {
            throw new IllegalArgumentException(emptyTrimmedMessage);
        }
        return s;
    }
}
