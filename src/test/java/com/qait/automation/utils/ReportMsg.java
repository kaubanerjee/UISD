/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qait.automation.utils;

import org.testng.Reporter;

/**
 *
 * @author Prashant Shukla <prashantshukla@qainfotech.com>
 */
public class ReportMsg {

    protected static final String fail = "[ASSERT FAIL]: ";
    protected static final String info = "[INFO]: ";
    protected static final String pass = "[ASSERT PASS]: ";
    protected static final String scripterror = "[SCRIPTING ERROR]: ";

    public static String failForAssert(String message) {
        return reportMsgForAssert(fail, message, true);
    }
    
    public static String fail(String message) {
        return reportMsg(fail, message);
    }

    public static String pass(String message) {
        return reportMsg(pass, message);
    }

    public static String scripterror(String message) {
        return reportMsg(scripterror, message);
    }
    
    public static String info(String message) {
        return reportMsg(info, message);
    }
    
    public static String log(String message){
        return reportMsg(info, message);
    }

    private static String reportMsg(String prefix, String message) {
    	Reporter.log(prefix + message, true);
        return prefix + message;
    }
    
    private static String reportMsgForAssert(String prefix, String message, boolean flag) {
        return prefix + message;
    }
    
    public static String log(String msgType, String message){
        msgType=msgType.toUpperCase();
        return reportMsg("["+msgType+"]: ", message);
    }
    
    
}
