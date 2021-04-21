package com.cudrania.test.proxy;

import com.cudrania.core.proxy.AbstractProxyHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author skyfalling
 */
public class ConnectorHandler extends AbstractProxyHandler {

    private ConnectorManager manager;

    public ConnectorHandler(ConnectorManager manager) {
        this.manager = manager;
    }

    @Override
    public Object proxy(Object target, Method method, Object... args) {
        if (method.getName().equals("close")) {
            close(manager, (Connector) target);
            return null;
        }
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void close(ConnectorManager manager, Connector connector) {
        manager.close(connector);
    }
}
