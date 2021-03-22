package com.cudrania.test.proxy;

import com.cudrania.core.proxy.Interceptor;
import com.cudrania.core.proxy.ProxyFactory;
import com.cudrania.core.proxy.ProxyHandler;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestProxy {

    @Test
    public void test() {
        Interceptor interceptor = null;
        ITask u = (ITask) ProxyFactory.proxy(new Task(), interceptor);
        u.doIt();

        final ConnectorManager manager = new ConnectorManager();
        create(manager).close();
        System.out.println("===============");
        create(manager, new ConnectorCloser() {
            @Override
            public void close(ConnectorManager manager, Connector connector) {
                manager.close(connector);
            }
        }).close();
    }

    public Connector create(ConnectorManager manager) {
        return (Connector) ProxyFactory.proxy(manager.getConnector(), new ConnectorHandler(manager));
    }

    public Connector create(final ConnectorManager manager, final ConnectorCloser connectorCloser) {
        Connector connector = manager.getConnector();
        return (Connector) ProxyFactory.proxy(connector, new ProxyHandler(connector) {
            @Override
            public Object proxy(Object target, Method method, Object... args) {

                if (method.getName().equals("close") && method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
                    connectorCloser.close(manager, (Connector) target);
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

        });
    }
}
