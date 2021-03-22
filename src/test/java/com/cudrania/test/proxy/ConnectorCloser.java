package com.cudrania.test.proxy;

/**
 * @author skyfalling
 */
public interface ConnectorCloser {
    public void close(ConnectorManager manager, Connector connector);
}
