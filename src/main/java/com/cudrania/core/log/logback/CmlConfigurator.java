package com.cudrania.core.log.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.Status;

/**
 * Logback默认配置，代替logback.xml中的配置<p/>
 * <pre>
 * &lt;conversionRule conversionWord="CML" converterClass="com.cudrania.core.log.logback.FQCNConverter"/&gt;
 * </pre>
 *
 * @author : skyfalling
 * @created : 2023/11/3, 星期五
 * Copyright (c) 2004-2029 All Rights Reserved.
 **/
public class CmlConfigurator implements Configurator {

    {
        PatternLayout.DEFAULT_CONVERTER_MAP.put("CML", FQCNConverter.class.getName());
    }

    @Override
    public ExecutionStatus configure(LoggerContext loggerContext) {
        return null;
    }

    @Override
    public void setContext(Context context) {

    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void addStatus(Status status) {

    }

    @Override
    public void addInfo(String s) {

    }

    @Override
    public void addInfo(String s, Throwable throwable) {

    }

    @Override
    public void addWarn(String s) {

    }

    @Override
    public void addWarn(String s, Throwable throwable) {

    }

    @Override
    public void addError(String s) {

    }

    @Override
    public void addError(String s, Throwable throwable) {

    }
}
