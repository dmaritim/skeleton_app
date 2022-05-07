package com.elsofthost.syncapp.component;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.elsofthost.syncapp.entity.OrgUnit;
import com.elsofthost.syncapp.service.OrgUnitService;


@Component
public class ExcelFileReader {
	
	private static final Logger logger = LoggerFactory.getLogger(ExcelFileReader.class);
	
	@Autowired
	private OrgUnitService orgUnitService;
	
	public static List<OrgUnit> getOrgUnits() {
		//OrgUnits orgUnits = new OrgUnits();
		List<OrgUnit> allOrgUnits = new ArrayList<>();

        try
        {
            File file = new ClassPathResource("/setup/AMEP_Facilities_FY21_Version_2.xlsx").getFile();
            FileInputStream fileInputStream = new FileInputStream(file);

            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
 
            XSSFSheet sheet = workbook.getSheet("Facilities");
 	        
            int rowsCount = sheet.getLastRowNum();

            logger.info("Total Number of Rows: " + (rowsCount + 1));
            //Start reading from row two.
            for (int i = 1; i <= rowsCount; i++) {
                Row row = sheet.getRow(i);

             //String uuid, String county, String subCounty, String ward, String name, String datimName,
    		//	String datimUuid, Double hts, Double pmtct, Double ct, Double level,String mfl
                OrgUnit orgUnit = new OrgUnit(
                		row.getCell(0).getStringCellValue(), //String uuid
                		row.getCell(1).getStringCellValue(), //String county
                		row.getCell(2).getStringCellValue(), //String subCounty
                		row.getCell(3).getStringCellValue(), //String ward
                		row.getCell(4).getStringCellValue(), //String name
                		row.getCell(5).getStringCellValue(), //String datimName
                		row.getCell(6).getStringCellValue(), //String datimUuid
                		(int) row.getCell(7).getNumericCellValue(), //Double hts
                		(int) row.getCell(8).getNumericCellValue(), //Double pmtct
                		(int) row.getCell(9).getNumericCellValue(), //Double ct,
                		(int) row.getCell(11).getNumericCellValue(), //Integer level
                		String.valueOf((int) row.getCell(10).getNumericCellValue())//String mfl
                );
                logger.info("The current information " + i);
                allOrgUnits.add(orgUnit);
            }
            workbook.close();
            fileInputStream.close();
	        return allOrgUnits;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return null;
        }
	}
	
	public static void getCounties() {
		
	}

	public static void getSubCounties() {
		
	}
	
	public static void getWards() {
		
	}

}
