package com.qait.automation.report;

import static com.qait.automation.utils.ConfigPropertyReader.getProperty;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.qait.automation.utils.PropFileHandler;
import com.qait.automation.utils.YamlReader;

public class ResultsIT {

	String today = new Date().toString();
	String host = "smtp.gmail.com";
	String from = "automation.resultsqait@gmail.com";
	private static final String replyto = "vikashagrawal@qainfotech.com";
	String password = "QaitAutomation", pieChartPath = "/target/pieChart";
	int port = 25;
	String failureResults = "";
	String skippedResults = "";
	String passedResult = "";
	boolean sendResults = false;
	private static String projectName;
	public static int count = 0;
	private DefaultPieDataset dataset;

	@BeforeClass
	void setupMailConfig() {
		projectName = "Dev-Math";
	}

	@Test
	public void sendResultsMail() throws MessagingException, IOException {
		if (true) { // send email is true *************************
			dataset = new DefaultPieDataset();
			Message message = new MimeMessage(getSession());
			message.addFrom(new InternetAddress[] { (new InternetAddress(from)) });
			setMailRecipient(message);
			message.setContent(setAttachment());
			message.setSubject(setMailSubject());
			Session session = getSession();
			Transport transport = session.getTransport("smtps");
			transport.connect(host, from, password);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		}
		System.out.println("Result report has been emailed");

	}

	private Session getSession() {
		Authenticator authenticator = new Authenticator(from, password);
		Properties properties = new Properties();
		properties.setProperty("mail.transport.protocol", "smtps");
		properties.put("mail.smtps.auth", "true");
		properties.setProperty("mail.smtp.submitter", authenticator.getPasswordAuthentication().getUserName());
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.host", host);
		properties.setProperty("mail.smtp.port", String.valueOf(port));
		return Session.getInstance(properties, authenticator);
	}

	String mailtext = "";

	private String setBodyText() throws IOException {
		List<String> failedResultsList = printFailedTestInformation();
		String[] failedResultArray = new String[failedResultsList.size()];
		for (int i = 0; i < failedResultArray.length; i++) {
			failedResultArray[i] = failedResultsList.get(i);
		}
		testSetResult();
		generatePieChart();
		String agentName1 = getuserAgent();

		mailtext = "Hi All,<br>";
		mailtext = mailtext
				+ "<br><font color = Black>Please find below the Dev-Math <b>Features Regression </b> test automation specifictions & test execution results : </font><br>";
		mailtext = mailtext + "<br>";
		mailtext = mailtext + "<b><u><font color = Black>Test Specifications & Execution Results</u> : </b></font><br>";
		mailtext = mailtext
				+ "<br><table border=\"1\"><tr><td align=\"center\"><b><font color = Black>Environment</font></b></td><td><font color = Blue>"
				+ System.getProperty("tier", getProperty("./Config.properties", "tier")).toUpperCase()
				+ "</font></td></tr>"
				+ "<tr><td align=\"center\"><b><font color = Black>Browser Version</font></b></td><td><font color = Blue>"
				+ PropFileHandler.readProperty("browserVersion") + "</font></td></tr>"
				+ "<tr><td align=\"center\"><b><font color = Black>Operating System Info</font></b></td><td><font color = Blue>"
				+ agentName1.toUpperCase(Locale.ENGLISH) + "</font></td></tr>"
				+ "<tr><td align=\"center\"><b><font color = Black>Browser</font></b></td><td><font color = Blue>"
				+ System.getProperty("browser", getProperty("./Config.properties", "browser"))
						.toUpperCase(Locale.ENGLISH)
				+ "</font></td></tr>"
				+ "<tr><td align=\"center\"><b><font color = Black>Test Case Executed By</font></b></td><td><font color = Blue>"
				+ projectName + " Automation Team</font></td></tr>"
				+ "<tr><td align=\"center\"><b><font color = Black>Time Taken</font></b></td><td><font color = Blue>"
				+ totalTime + "</font></td></tr>"
				+ "<tr><td align=\"center\"><b><font color = Black>Total Test Case</font></b></td><td><font color = Blue>"
				+ (Integer.parseInt(passedResult) + Integer.parseInt(failureResults) + Integer.parseInt(skippedResults))
				+ "</font></td></tr>"
				+ "<tr><td align=\"center\"><b><font color = Black>Pass Percentage</font></b></td><td bgcolor=\"Green\" align=\"center\"><b><font color = Black>PASS; "
				+ passPercentage + " % </font></b></td></tr>";

		if (!(Integer.parseInt(failureResults) == 0 && Integer.parseInt(skippedResults) == 0)) {
			mailtext = mailtext
					+ "<tr><td align=\"center\"><b><font color = Black>Test Cases Fail</font></b></td><td bgcolor=\"Red\" align=\"center\"><b><font color = Black>"
					+ failureResults + " test cases</font></b></td></tr></table><br><br>";
		}

		mailtext = mailtext + "</table>";

		mailtext = mailtext
				+ "<b><i>NOTE : <br> 1. </i></b>Captured screenshots are saved in Project WorkSpace within location : <font color = Blue> \".target/screenshots\"</font><br>";
		mailtext = mailtext
				+ "<b><i>2. </i></b>There is a Pie Chart attached in email. This can locally be found on : <font color = Blue> "
				+ pieChartPath + today.replaceAll(":", "_") + ".png</font><br><br><br>";
		mailtext = mailtext
				+ "The detailed test results are given in the attached <b><i>emailable-report.html</i></b> </br></br>"
				+ "<br>";
		mailtext = mailtext + "-- <br>Best Regards";
		mailtext = mailtext + "<br><i>" + projectName + " Test Automation Team </i></br>";

		mailtext = mailtext + "<hr>" + "<i>Note: This is a system generated mail. Please do not reply." + " ";
		mailtext = mailtext + "If you have any queries mail to <a href=mailto:" + replyto
				+ "?subject=Reply-of-Automation-Status" + today.replaceAll(" ", "_") + ">" + projectName
				+ " Test Automation Team</a></i>";

		return mailtext;
	}

	private String setMailSubject() {

		return (projectName + " Automated Test Results: " + failureResults + " Failures | " + today);
	}

	double passPercentage;

	private void generatePieChart() {
		setPieChartPath();
		int totalCases = Integer.parseInt(totalTestCases);
		passPercentage = (Integer.parseInt(passedResult) * 100) / totalCases;
		double failPercentage = (Integer.parseInt(failureResults) * 100) / totalCases;
		double skipPercentage = (Integer.parseInt(skippedResults) * 100) / totalCases;
		dataset.setValue("Passed:- " + passedResult, passPercentage);
		dataset.setValue("Failed:- " + failureResults, failPercentage);
		dataset.setValue("Skipped:- " + skippedResults, skipPercentage);
		JFreeChart chart = ChartFactory.createPieChart(projectName + "Test Automation Results", dataset, true, true,
				false);
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		plot.setSectionOutlinePaint("Passed", Color.GREEN);
		plot.setSectionOutlinePaint("Failed", Color.RED);
		plot.setSectionOutlinePaint("Skipped", Color.YELLOW);

		int width = 640;
		int height = 480;

		File pieChart = new File(pieChartPath + "/" + today.replaceAll(":", "_") + ".png");
		pieChartPath += "/" + today.replaceAll(":", "_") + ".png";
		System.out.println("[INFO]: PieChart saved at ::::: " + pieChartPath);
		try {
			ChartUtilities.saveChartAsPNG(pieChart, chart, width, height);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getuserAgent() {
		String agentName = PropFileHandler.readProperty("userAgent");
		int index1 = agentName.indexOf("(");
		int index2 = agentName.indexOf(")");
		return agentName.substring(index1 + 1, index2);
	}

	private void setMailRecipient(Message message) throws AddressException, MessagingException, IOException {

		Map<String, Object> emailMap = YamlReader.getYamlValues("email.recepients");
		for (Object val : emailMap.values()) {
			System.out.println("Email Ids:- " + val.toString());
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(val.toString()));
		}
		message.addRecipient(Message.RecipientType.CC, new InternetAddress("vikashagrawal@qainfotech.com"));

	}

	private Multipart setAttachment() throws MessagingException, IOException {
		// Create the message part
		MimeBodyPart messageBodyPart = new MimeBodyPart();

		// Fill the message
		messageBodyPart.setContent(setBodyText(), "text/html");

		MimeMultipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		// Part two is attachment
		messageBodyPart = new MimeBodyPart();
		addAttachment(multipart, messageBodyPart, pieChartPath);
		addAttachment(multipart, messageBodyPart, "./target/surefire-reports/emailable-report.html");
		return multipart;
	}

	private static void addAttachment(Multipart multipart, MimeBodyPart messageBodyPart, String filename)
			throws MessagingException {
		messageBodyPart = new MimeBodyPart();
		File f = new File(filename);
		DataSource source = new FileDataSource(f);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(f.getName());
		multipart.addBodyPart(messageBodyPart);
	}

	@SuppressWarnings("unused")
	private String getTestName() {
		String test = System.getProperty("test", "null");
		String testsuite = System.getProperty("testsuite", "null");
		String testName;
		if (test != "null") {
			testName = test + " was executed";
			return testName;
		} else if (testsuite != "null") {
			testName = testsuite + "were executed";
			return testName;
		} else {
			testName = "Dev Math Automated Smoke Test Suite";
			return testName;
		}
	}

	private void testSetResult() throws IOException {

		String filepath = "./target/surefire-reports/testng-results.xml";
		parseTestNgXmlFile(filepath);
	}

	String totalTime, startTime, endTime;
	String msgOutput = " ";
	String totalTestCases;

	private void parseTestNgXmlFile(String filepath) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document dom = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			dom = dBuilder.parse(filepath);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		NodeList nodes = dom.getElementsByTagName("testng-results");
		Element ele = (Element) nodes.item(0);

		failureResults = ele.getAttribute("failed");
		skippedResults = ele.getAttribute("skipped");
		passedResult = ele.getAttribute("passed");
		NodeList nodes1 = dom.getElementsByTagName("suite");
		Element ele1 = (Element) nodes1.item(0);

		totalTime = ele1.getAttribute("duration-ms");
		startTime = ele1.getAttribute("started-at");
		endTime = ele1.getAttribute("finished-at");
		startTime = startTime.replace("T", "; ");
		startTime = startTime.replace("Z", "");
		if (Math.round(Double.parseDouble(totalTime) / 1000) > 60) {
			totalTime = String.valueOf(Math.round((Double.parseDouble(totalTime) / 1000) / 60)) + " minutes";
		} else {
			totalTime = String.valueOf(Math.round(Double.parseDouble(totalTime) / 1000)) + " seconds";
		}
		msgOutput = msgOutput + ele.getAttribute("total") + " ,Passed: " + passedResult + " ,Failures: "
				+ ele.getAttribute("failed") + " ,Skipped: " + ele.getAttribute("skipped") + " ,Total Execution Time: "
				+ totalTime;
		totalTestCases = ele.getAttribute("total");
	}

	private List<String> printFailedTestInformation() {
		String filepath = "./target/surefire-reports/testng-results.xml";
		File file = new File(filepath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document dom = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			dom = dBuilder.parse(file);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> list = identifyTagsAndTraverseThroguhElements(dom);
		System.out.println("Number of Failed Test Cases:- " + count);
		return list;
	}

	private List<String> identifyTagsAndTraverseThroguhElements(Document dom) {

		List<String> list = new ArrayList<String>();

		NodeList nodes = dom.getElementsByTagName("test-method");
		try {
			NodeList nodesMessage = dom.getElementsByTagName("full-stacktrace");
			for (int i = 0, j = 0; i < nodes.getLength() && j < nodesMessage.getLength(); i++) {

				Element ele1 = (Element) nodes.item(i);
				Element ele2 = (Element) nodesMessage.item(j);

				if (ele1.getAttribute("status").equalsIgnoreCase("FAIL")) {
					count++;
					String[] testMethodResonOfFailure = getNameTestReason(ele1, ele2);
					list.add(testMethodResonOfFailure[0]);
					list.add(testMethodResonOfFailure[1]);
					list.add(testMethodResonOfFailure[2]);

					j++;
				}
			}
		} catch (Exception e) {
			Reporter.log("[INFO]: No Failures!!", true);
		}
		return list;

	}

	private String[] getNameTestReason(Element el1, Element el2) {
		String[] returnNameTestReason = new String[3];
		NamedNodeMap name = el1.getParentNode().getParentNode().getAttributes();

		returnNameTestReason[0] = name.getNamedItem("name").toString().replaceAll("name=", "");
		returnNameTestReason[1] = el1.getAttribute("name");
		returnNameTestReason[2] = el2.getTextContent();
		return returnNameTestReason;
	}

	public void setPieChartPath() {
		File file = new File(System.getProperty("user.dir") + File.separator + pieChartPath + File.separator);
		boolean exists = file.exists();
		if (!exists) {
			new File(System.getProperty("user.dir") + File.separator + pieChartPath + File.separator).mkdir();
		}
		pieChartPath = System.getProperty("user.dir") + File.separator + pieChartPath + File.separator;
	}

}
