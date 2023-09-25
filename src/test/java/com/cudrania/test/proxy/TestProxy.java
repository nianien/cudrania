package com.cudrania.test.proxy;

import com.cudrania.core.proxy.Interceptor;
import com.cudrania.core.proxy.ProxyFactory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

public class TestProxy {

    @Test
    public void test() {
        ITask u = (ITask) ProxyFactory.proxy(new Task(), (Interceptor) null);
        u.doIt();

        final ConnectorManager manager = new ConnectorManager();
        create(manager).close();
        System.out.println("===============");
        create(manager, (manager1, connector) -> manager1.close(connector)).close();
    }

    public Connector create(ConnectorManager manager) {
        return (Connector) ProxyFactory.proxy(manager.getConnector(), new ConnectorHandler(manager));
    }

    public Connector create(final ConnectorManager manager, final ConnectorCloser connectorCloser) {
        Connector connector = manager.getConnector();
        return (Connector) ProxyFactory.proxy(connector, (proxy, target, method, args) -> {

            if (method.getName().equals("close") && method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
                connectorCloser.close(manager, target);
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
        });
    }
}
