package com.example.android.camera2basic;

import android.app.Application;
import android.os.Process;
import android.util.Log;

import org.slf4j.LoggerFactory;

import java.io.File;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class App extends Application {

    static  org.slf4j.Logger staticLogger = LoggerFactory.getLogger("static-logger");
    private org.slf4j.Logger logger;
    public void onCreate () {
        // Setup handler for uncaught exceptions.
        super.onCreate();
        configureLogbackDirectly();

        logger = LoggerFactory.getLogger("exceptions-logger");

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });
    }

    public void handleUncaughtException (Thread thread, Throwable e)
    {

        logger.error("", e);
        Process.killProcess(Process.myPid());
    }

    private void configureLogbackDirectly() {

        // reset the default context (which may already have been initialized)
        // since we want to reconfigure it
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.stop();

        // setup FileAppender
        PatternLayoutEncoder requestsEncoder = new PatternLayoutEncoder();
        requestsEncoder.setContext(lc);
        requestsEncoder.setPattern("%d{HH:mm:ss.SSS} - %msg%n");
        requestsEncoder.start();

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setContext(lc);
        fileAppender.setFile(getExternalFilesDir("logs").getAbsolutePath() + File.separatorChar +  "requests.txt");
        fileAppender.setEncoder(requestsEncoder);
        fileAppender.start();

        PatternLayoutEncoder exceptionsEncoder = new PatternLayoutEncoder();
        exceptionsEncoder.setContext(lc);
        exceptionsEncoder.setPattern("%d{HH:mm:ss.SSS} - %logger - %level - %thread - %xEx");
        exceptionsEncoder.start();

        ThresholdFilter exceptionsFilter = new ThresholdFilter();
        exceptionsFilter.setLevel("error");
        exceptionsFilter.start();

        FileAppender<ILoggingEvent> exceptionsAppender = new FileAppender<ILoggingEvent>();
        exceptionsAppender.setContext(lc);
        exceptionsAppender.setFile(getExternalFilesDir("logs").getAbsolutePath() + File.separatorChar +  "exceptions.txt");
        exceptionsAppender.setEncoder(exceptionsEncoder);
        exceptionsAppender.addFilter(exceptionsFilter);
        exceptionsAppender.start();



//         setup LogcatAppender
        PatternLayoutEncoder encoder2 = new PatternLayoutEncoder();
        encoder2.setContext(lc);
        encoder2.setPattern("[%thread] %msg%n");
        encoder2.start();

        LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setContext(lc);
        logcatAppender.setEncoder(encoder2);
        logcatAppender.start();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(fileAppender);
        root.addAppender(exceptionsAppender);
        root.addAppender(logcatAppender);
    }
}
