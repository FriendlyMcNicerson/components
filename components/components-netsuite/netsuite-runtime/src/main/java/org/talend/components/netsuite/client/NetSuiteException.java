package org.talend.components.netsuite.client;

/**
 *
 */
public class NetSuiteException extends RuntimeException {

    public NetSuiteException(String message) {
        super(message);
    }

    public NetSuiteException(String message, Throwable cause) {
        super(message, cause);
    }
}
