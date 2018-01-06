/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qait.automation.utils;

import static com.qait.automation.utils.ConfigPropertyReader.getProperty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.file.Paths;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.testng.Reporter;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

/**
 *
 *
 */
public class BrowserConsolelog {

    WebDriver driver;
    String testname, testcaseName;
    String consoleLogPath = "/target/consoleLog";
    final String USER_DIR_PROPERTY_NAME = "user.dir";
    String _userDirPath;
    static final String NEW_LINE = System.getProperty("line.separator");

    private String getUserDirPath() {
        if (_userDirPath == null || _userDirPath.trim().isEmpty()) {
            _userDirPath = System.getProperty("user.dir");
        }

        return _userDirPath;
    }

    public BrowserConsolelog(String testName, WebDriver driver) {
        this.driver = driver;
        this.testname = testName;
    }

    private void captureLog(String path, String testcaseName) throws IOException {
        boolean flag = true;
        LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
        String text = "";
        BufferedWriter writer = null;

        try {
            for (LogEntry entry : logEntries) {

                text += String.format("%1$s %2$s Test: %3$s Message: %4$s%5$s", new Date(entry.getTimestamp()), entry.getLevel(), testcaseName, entry.getMessage(), NEW_LINE);

            }
            flag = text.isEmpty();
            if (!flag) {
                System.out.println(text);
                writer = new BufferedWriter(new FileWriter(path));
                writer.write(text);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!flag) {
                writer.close();
            }
            System.out.println("No log entries found");
        }
    }

    private void browserConsoleLog() {
        consoleLogPath = (getProperty("consoleLog-path") != null) ? getProperty("consoleLog-path") : consoleLogPath;
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_a");
        Date date = new Date();
        String date_time = dateFormat.format(date);
        String dirPath = Paths.get(getUserDirPath(), consoleLogPath, testname).toString();
        File file = new File(dirPath);
        boolean exists = file.exists();
        boolean createdDir = false;
        if (!exists) {
            createdDir = new File(dirPath).mkdirs();
        }
        Reporter.log(String.valueOf(createdDir));
        try {
            String saveLogFile = Paths.get(dirPath, testcaseName + ".txt").toString();
            Reporter.log("[INFO]: Save Console Log File Path : " + saveLogFile, true);
            captureLog(saveLogFile, this.testcaseName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void takeConsoleLogOnException(String result) {
        String browserConsolelog = getProperty("take-ConsoleLogs");
        if (result.contains("fail")) {
            if (true) {
                if (browserConsolelog.equalsIgnoreCase("true") || browserConsolelog.equalsIgnoreCase("yes")) {
                    try {
                        if (driver != null) {
                            browserConsoleLog();
                        }
                    } catch (Exception ex) {
                        Reporter.log("[TAKES CONSOLE EXCEPTION]Driver is null while taking Browser console logs", true);
                    }
                }
            }
        }
        Reporter.log("*************************************", true);
    }
}
