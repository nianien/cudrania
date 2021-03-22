package com.cudrania.test.proxy;

/**
 * @author skyfalling
 */
public class ConnectorManager {

    public void close(Connector conn) {
        System.out.println("close by Manager");
        conn.close();
    }

    public Connector getConnector() {
        return new ConnectorImpl();
    }
}
