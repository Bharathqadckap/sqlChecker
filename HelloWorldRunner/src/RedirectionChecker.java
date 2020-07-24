import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class RedirectionChecker {

	public static Properties propy;

	public static void main(String[] args) throws IOException {

		HtmlUnitDriver driver = new HtmlUnitDriver();
		//added a new comment
		//added a new comment
		InputStream propertyStream = RedirectionChecker.class.getResourceAsStream("application.properties");
		propy.load(propertyStream);
		// assign property values to local variables
		FileInputStream fis = new FileInputStream(propy.getProperty("excelpath"));
		int startingRowNo = Integer.parseInt(propy.getProperty("startingrowno"));
		int endingRowNo = Integer.parseInt(propy.getProperty("endingrowno"));
		String is301Redirectiononly = propy.getProperty("Is301Redirectiononly");
		// Reading data from excel source
		XSSFWorkbook wbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = wbook.getSheet(propy.getProperty("sheetname"));
		FileOutputStream fos = new FileOutputStream(propy.getProperty("excelpath"));
		for (int i = startingRowNo - 1; i < endingRowNo; i++) {
			printMessage("Processing of row " + Integer.toString(i+1) + " started");
			XSSFRow row = sheet.getRow(i);
			XSSFCell cell = row.getCell(0);
			String sourceURL = cell.getStringCellValue();
			XSSFRow row1 = sheet.getRow(i);
			XSSFCell cell1 = row1.getCell(1);
			String destinationURL = cell1.getStringCellValue();
			driver.get(sourceURL);
			String currentURL = driver.getCurrentUrl();
			String outputValue = "Fail";
			if (currentURL.equalsIgnoreCase(destinationURL)) {
				//To verify is 301 Redirection Only
				if (is301Redirectiononly.equalsIgnoreCase("TRUE")) {
					URL url = new URL(sourceURL);
					HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
					httpCon.setInstanceFollowRedirects(false);
					httpCon.connect();
					int res = httpCon.getResponseCode();
					if (res == 301 || res == 302) {
						outputValue = "Pass";
					}
				} else {
					outputValue = "Pass";
				}
			}
			sheet.getRow(i).createCell(2).setCellValue(outputValue);
			wbook.write(fos);
			printMessage("Processing of row " + Integer.toString(i+1) + " completed");
		}
		fos.close();
		wbook.close();
		driver.close();
		propertyStream.close();
	}
	public static void printMessage(String message) {
		System.out.println(message);
	}
}
