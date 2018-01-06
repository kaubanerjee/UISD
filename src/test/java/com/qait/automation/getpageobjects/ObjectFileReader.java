package com.qait.automation.getpageobjects;

import com.qait.automation.utils.ReportMsg;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static com.qait.automation.utils.ConfigPropertyReader.getProperty;

/**
 * This class reads the PageObjectRepository text files. Uses buffer reader.
 *
 * @author prashantshukla
 *
 */
public class ObjectFileReader {


    public static String tier; // Selects the respective environment folder of object locators in framework
    static String filepath = "src/test/resources/PageObjectRepository/";

    public static String[] getELementFromFile(String pageName,String elementName) {
        setTier();
        try {
            FileReader specFile = new FileReader(filepath + tier + pageName + ".spec");
            return getElement(specFile, elementName, pageName);
        } catch (FileNotFoundException e) {
            ReportMsg.fail(pageName + ".spec File not found at location :- " + filepath);
        }
        return null;
    }

    public static String getPageTitleFromFile(String pageName) {
        setTier();
        BufferedReader br = null;
        String returnElement = "";
        try {
            br = new BufferedReader(new FileReader(filepath + tier + pageName
                    + ".spec"));
            String line = br.readLine();

            while (line != null && !line.startsWith("========")) {
                String titleId = line.split(":", 3)[0];
                if (titleId.equalsIgnoreCase("pagetitle")
                        || titleId.equalsIgnoreCase("title")
                        || titleId.equalsIgnoreCase("page title")) {
                    returnElement = line;
                    break;
                }
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            ReportMsg.fail(pageName + ".spec File not found at location :- " + filepath);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnElement.split(":", 2)[1].trim();
    }

    private static String[] getElement(FileReader specFile, String elementName, String pageName) {

        ArrayList<String> elementLines = getSpecSection(specFile);
        for (String elementLine : elementLines) {
        	String elements[]=elementLine.split(" ");
            if (elements[0].equals(elementName)) {
                return elementLine.split(" ", 3);
            }
        }
        throw new NullPointerException("Element " + elementName + " Not Found in the " + pageName + ".spec file" + " in " + tier + " PageObjectRepository");
    }
    

    private static ArrayList<String> getSpecSection(FileReader specfile) {
        String readBuff = null;
        ArrayList<String> elementLines = new ArrayList<String>();

        try {
            BufferedReader buff = new BufferedReader(specfile);
            try {
                boolean flag = false;
                readBuff = buff.readLine();
                while ((readBuff = buff.readLine()) != null) {
                    if (readBuff.startsWith("========")) {
                        flag = !flag;
                    }
                    if (flag) {
                        elementLines.add(readBuff.trim().replaceAll("[ \t]+",
                                " "));
                    }
                    if (!elementLines.isEmpty() && !flag) {
                        break;
                    }
                }
            } finally {
                buff.close();
                if (elementLines.get(0).startsWith("========")) {
                    elementLines.remove(0);
                }
            }
        } catch (IOException e) {
            System.out.println("exceptional case");
        }
        return elementLines;
    }

    public static String getTier() {
        setTier();
        return tier;
    }

    //TODO move this to tiers enum
     public static void setTier() {
        tier=System.getProperty("tier");
        if(tier==null){
        tier=(getProperty("Config.properties", "tier")).toLowerCase();}
        switch (Tiers.valueOf(tier)) {
            case production:
            case prod:
                tier = "PROD/";
                break;
            case qa:
                tier = "QA/";
                break;
            case integration:
            case Int:
                tier = "INT/";
                break;
            case performancetuning:
            case perf:
                tier = "PERF/";
                break;
            case stage:
            case staging:
                tier = "STAGE/";
                break;
        }
        
    }
}
