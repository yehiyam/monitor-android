package com.example.android.camera2basic;


import java.util.logging.Logger;

public class Log4jHelper {

    private final static logConfigurator  _logConfigurator = new LogConfigurator();


    public static void Configure(String fileName, String filePattern,
                                 int maxBackupSize, long maxFileSize) {

        // set the name of the log file
        _logConfigurator.setFileName(fileName);
        // set output format of the log line
        _logConfigurator.setFilePattern(filePattern);
        // Maximum number of backed up log files
        _logConfigurator.setMaxBackupSize(maxBackupSize);
        // Maximum size of log file until rolling
        _logConfigurator.setMaxFileSize(maxFileSize);

        // configure
        _logConfigurator.configure();

    }

    public static Logger getLogger(String name) {
        Logger logger = Logger.getLogger(name);
        return logger;
    }

}