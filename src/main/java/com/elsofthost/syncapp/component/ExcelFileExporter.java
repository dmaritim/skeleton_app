package com.elsofthost.syncapp.component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ComparisonOperator;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.elsofthost.syncapp.entity.DataValue;
import com.elsofthost.syncapp.entity.OrgUnit;
import com.elsofthost.syncapp.response.DataValues;
import com.elsofthost.syncapp.service.OrgUnitService;



@Component
public class ExcelFileExporter {
	
	private static final Logger logger = LoggerFactory.getLogger(ExcelFileExporter.class);
	
	@Autowired
	RestDHIS2Util restDHIS2Util;
	
	@Autowired
	OrgUnitService orgUnitService;
	
	
	public ByteArrayInputStream KHISDownload(List<String> counties,List<String> periods) {
		//These are the headers of strSheetName 
		final String[] headerColumns = {
				"OrgUnit_dataelement_CategoryOptionCombo_Period", 
				"DataElement_CategoryOptionCombo",
				"OrgUnit",
				"DataElement",
				"CategoryOptionCombo",
				"Period",
				"attributeoptioncombo",
				"Value",
				"storedby",
				"followup",
				"comment",
				"last_updated"
				};
		String strSheetName ="KHIS_Download";
		String strOrgUnits = "";
		String strPeriods = "";
		
		List<List<OrgUnit>> countyFacilitiesList = new ArrayList<List<OrgUnit>>();
		
		//
		List<OrgUnit> countyOrgUnits = new ArrayList<OrgUnit>();
		
		//Prepare the OrgUnits to be fetched.
		for (int i = 0; i < counties.size(); i++) {
			countyOrgUnits = orgUnitService.findByCountyAndHts(counties.get(i),1);
			countyFacilitiesList.add(countyOrgUnits);
		}
		//Prepare the Period to be fetched. Can be a Period or more.
        for (int i = 0; i < periods.size(); i++) {
            //System.out.println(periods.get(i));
            if (i==0) {
            	strPeriods = "period=" + periods.get(i);
            }else {
            	strPeriods = strPeriods + "&period="   +  periods.get(i);       	
            }
        }
        strPeriods = strPeriods + ";";
        
        logger.info("Generated string of periods :" +strPeriods);
        logger.info("Begining the process of coping the template file.");
        
        try
        {
            File file = new ClassPathResource("/reports/KHIS_to_AMEP_Template.xlsx").getFile();
            FileInputStream fileInputStream = new FileInputStream(file);

            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
 
            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.createSheet(strSheetName);
                      
            //Row row = sheet.createRow(0);
            //sheet.autoSizeColumn(0);
	        CellStyle headerCellStyle = workbook.createCellStyle();
	       // headerCellStyle.setRotation((short) 90);
	        headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
	        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	        headerCellStyle.setWrapText(true);
	        
	        //Cell cell = row.createCell(0);
	        Row headerRow = sheet.createRow(0);
	        

	        logger.info("Processing headers.");
	        for(int i = 0; i < headerColumns.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(headerColumns[i]);
	            cell.setCellStyle(headerCellStyle);
	        }
	        
	        
	        //Beginning Processing the selected Counties
	        //It pulls the individual Counties One by One and extracts its Facilities in loop
	        int iRowNumber = 1;
	        for (int i = 0; i < countyFacilitiesList.size(); i++) {
	        	//Get the List of Facilities from a County
	        	List<OrgUnit> orgUnits1 = countyFacilitiesList.get(i);
	        	strOrgUnits = "";
					//Loop the list of list of OrgUnits.
			        for (int j = 0; j < orgUnits1.size(); j++) {
			        	logger.info(orgUnits1.get(j).getUuid());

			            if (j==0) {
			            	strOrgUnits = "orgUnit="   + orgUnits1.get(j).getUuid();
			            }else {
			                strOrgUnits = strOrgUnits + "&orgUnit="   +  orgUnits1.get(j).getUuid();       	
			            }
			        }
			        strOrgUnits = strOrgUnits + ";";
			        logger.info("Generated string of OrgUnits :" +strOrgUnits);

			        logger.info("KHIS Download processing of datavalues.");
			        List<DataValue> dataValue = getKHISDataValueSets(strOrgUnits,strPeriods);
			        
			        	// Storing the Values in Sheet
				        for(int x = 0; x < dataValue.size(); x++) {
				        	Row dataRow = sheet.createRow(iRowNumber);
				        	logger.info("Datavalue :" + dataValue.toString());
				        	dataRow.createCell(0).setCellValue(dataValue.get(x).getId());
				        	dataRow.createCell(1).setCellValue(dataValue.get(x).getDataElementCategoryOptionCombo());
				        	dataRow.createCell(2).setCellValue(dataValue.get(x).getOrgUnit());
				        	dataRow.createCell(3).setCellValue(dataValue.get(x).getDataElement());
				        	dataRow.createCell(4).setCellValue(dataValue.get(x).getCategoryOptionCombo());
				        	dataRow.createCell(5).setCellValue(dataValue.get(x).getPeriod());
				        	dataRow.createCell(6).setCellValue(dataValue.get(x).getAttributeOptionCombo());
				        	dataRow.createCell(7).setCellValue(dataValue.get(x).getValue());
				        	dataRow.createCell(8).setCellValue(dataValue.get(x).getStoredBy());
				        	dataRow.createCell(9).setCellValue(dataValue.get(x).getFollowup());
				        	dataRow.createCell(10).setCellValue(dataValue.get(x).getComment());
				        	dataRow.createCell(11).setCellValue(dataValue.get(x).getLastUpdated());
				        	iRowNumber++;
				        }

	        }     //End Loop through the Counties
				        
	        // Making size of column auto resize to fit with data
	        sheet.autoSizeColumn(0);
	        sheet.autoSizeColumn(1);
	        sheet.autoSizeColumn(2);
	        sheet.autoSizeColumn(3);
	        sheet.autoSizeColumn(4);
	        
	        
	        logger.info("Giving a name to region with data in raw data sheet.");
	        //TODO: To check for existence of the name before creating
	        CellRangeAddress userRange = new CellRangeAddress(0,sheet.getLastRowNum(),0,4);
	        Name namedRange = workbook.createName();
	        namedRange.setNameName(strSheetName);
	        String reference = userRange.formatAsString(strSheetName, true);
	        namedRange.setRefersToFormula(reference);
	        
	        logger.info(userRange.formatAsString(strSheetName, true));
	        sheet.setAutoFilter(userRange);
	        
	        //Hide the sheet
	        logger.info("Done with downloading the Datavalues and now Hidding the Raw data Sheet.");
	        workbook.setSheetVisibility(workbook.getSheetIndex(sheet.getSheetName()), SheetVisibility.HIDDEN);
	        
	        	        
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        workbook.write(outputStream);
	        //outputStream.close();
	        
	        workbook.close();
	        return new ByteArrayInputStream(outputStream.toByteArray());
        } 
        catch (Exception e) 
        {
        	logger.info("There was an error: " + e.toString());
            e.printStackTrace();
            return null;
        }
	}
	
	public ByteArrayInputStream htsDataGapsDownload(List<String> counties,List<String> periods) {
		//These are the headers of strSheetName 
		final String[] headerColumns = {"id", "de_uuid", "Period", "OrgUnit_uuid","Value"};
		String strSheetName ="HTS_Variance_raw";
		String strOrgUnits = "";
		String strPeriods = "";
		
		List<List<OrgUnit>> countyFacilitiesList = new ArrayList<List<OrgUnit>>();
		
		//
		List<OrgUnit> countyOrgUnits = new ArrayList<OrgUnit>();
		
		//Prepare the OrgUnits to be fetched.
		for (int i = 0; i < counties.size(); i++) {
			countyOrgUnits = orgUnitService.findByCountyAndHts(counties.get(i),1);
			countyFacilitiesList.add(countyOrgUnits);
		}
		//Prepare the Period to be fetched. Can be a Period or more.
        for (int i = 0; i < periods.size(); i++) {
            //System.out.println(periods.get(i));
            if (i==0) {
            	strPeriods = periods.get(i);
            }else {
            	strPeriods = strPeriods + ";"   +  periods.get(i);       	
            }
        }
        strPeriods = strPeriods + ";";
        
        logger.info("Generated string of periods :" +strPeriods);
        logger.info("Begining the process of coping the template file.");
        
        try
        {
            File file = new ClassPathResource("/reports/Gap_analysis_Template.xlsx").getFile();
            FileInputStream fileInputStream = new FileInputStream(file);

            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
 
            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.createSheet(strSheetName);
                      
            //Row row = sheet.createRow(0);
            //sheet.autoSizeColumn(0);
	        CellStyle headerCellStyle = workbook.createCellStyle();
	       // headerCellStyle.setRotation((short) 90);
	        headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
	        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	        headerCellStyle.setWrapText(true);
	        
	        //Cell cell = row.createCell(0);
	        Row headerRow = sheet.createRow(0);
	        

	        logger.info("Processing headers.");
	        for(int i = 0; i < headerColumns.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(headerColumns[i]);
	            cell.setCellStyle(headerCellStyle);
	        }
	        
	        
	        //Beginning Processing the selected Counties
	        //It pulls the individual Counties One by One and extracts its Facilities in loop
	        int iRowNumber = 1;
	        for (int i = 0; i < countyFacilitiesList.size(); i++) {
	        	//Get the List of Facilities from a County
	        	List<OrgUnit> orgUnits1 = countyFacilitiesList.get(i);
	        	strOrgUnits = "";
					//Loop the list of list of OrgUnits.
			        for (int j = 0; j < orgUnits1.size(); j++) {
			        	logger.info(orgUnits1.get(j).getUuid());

			            if (j==0) {
			            	strOrgUnits = orgUnits1.get(j).getUuid();
			            }else {
			                strOrgUnits = strOrgUnits + ";"   +  orgUnits1.get(j).getUuid();       	
			            }
			        }
			        strOrgUnits = strOrgUnits + ";";
			        logger.info("Generated string of OrgUnits :" +strOrgUnits);

			        logger.info("HTS processing of generation of datavalues.");
			        List<DataValue> dataValue = getHtsDataGapsDataValueSets(strOrgUnits,strPeriods);
			        
			        	// Storing the Values in Sheet
				        for(int x = 0; x < dataValue.size(); x++) {
				        	Row dataRow = sheet.createRow(iRowNumber);
				        	dataRow.createCell(0).setCellValue(dataValue.get(x).getId());
				        	dataRow.createCell(1).setCellValue(dataValue.get(x).getDataElement());
				        	dataRow.createCell(2).setCellValue(dataValue.get(x).getPeriod());
				        	dataRow.createCell(3).setCellValue(dataValue.get(x).getOrgUnit());
				        	dataRow.createCell(4).setCellValue(dataValue.get(x).getValue());
				        	iRowNumber++;
				        }
				        
				        
				        logger.info("HTS_DEX procesing of generation of datavalues.");
				         dataValue = getHtsIndexDataGapsDataValueSets(strOrgUnits,strPeriods);
				        
				        	// Storing the Values in Sheet
					        for(int x = 0; x < dataValue.size(); x++) {
					        	Row dataRow = sheet.createRow(iRowNumber);
					        	dataRow.createCell(0).setCellValue(dataValue.get(x).getId());
					        	dataRow.createCell(1).setCellValue(dataValue.get(x).getDataElement());
					        	dataRow.createCell(2).setCellValue(dataValue.get(x).getPeriod());
					        	dataRow.createCell(3).setCellValue(dataValue.get(x).getOrgUnit());
					        	dataRow.createCell(4).setCellValue(dataValue.get(x).getValue());
					        	iRowNumber++;
					        }
					        
				        logger.info("TB procesing of generation of datavalues.");
				         dataValue = getTBDataGapsDataValueSets(strOrgUnits,strPeriods);
				        
				        	// Storing the Values in Sheet
					        for(int x = 0; x < dataValue.size(); x++) {
					        	Row dataRow = sheet.createRow(iRowNumber);
					        	dataRow.createCell(0).setCellValue(dataValue.get(x).getId());
					        	dataRow.createCell(1).setCellValue(dataValue.get(x).getDataElement());
					        	dataRow.createCell(2).setCellValue(dataValue.get(x).getPeriod());
					        	dataRow.createCell(3).setCellValue(dataValue.get(x).getOrgUnit());
					        	dataRow.createCell(4).setCellValue(dataValue.get(x).getValue());
					        	iRowNumber++;
					        }	

					        logger.info("PMTCT procesing of generation of datavalues.");
					         dataValue = getPMTCTDataGapsDataValueSets(strOrgUnits,strPeriods);
					        
					        	// Storing the Values in Sheet
						        for(int x = 0; x < dataValue.size(); x++) {
						        	Row dataRow = sheet.createRow(iRowNumber);
						        	dataRow.createCell(0).setCellValue(dataValue.get(x).getId());
						        	dataRow.createCell(1).setCellValue(dataValue.get(x).getDataElement());
						        	dataRow.createCell(2).setCellValue(dataValue.get(x).getPeriod());
						        	dataRow.createCell(3).setCellValue(dataValue.get(x).getOrgUnit());
						        	dataRow.createCell(4).setCellValue(dataValue.get(x).getValue());
						        	iRowNumber++;
						        }
						        
					        logger.info("C&T procesing of generation of datavalues.");
					         dataValue = getCTDataGapsDataValueSets(strOrgUnits,strPeriods);
					        
					        	// Storing the Values in Sheet
						        for(int x = 0; x < dataValue.size(); x++) {
						        	Row dataRow = sheet.createRow(iRowNumber);
						        	dataRow.createCell(0).setCellValue(dataValue.get(x).getId());
						        	dataRow.createCell(1).setCellValue(dataValue.get(x).getDataElement());
						        	dataRow.createCell(2).setCellValue(dataValue.get(x).getPeriod());
						        	dataRow.createCell(3).setCellValue(dataValue.get(x).getOrgUnit());
						        	dataRow.createCell(4).setCellValue(dataValue.get(x).getValue());
						        	iRowNumber++;
						        }						        
						        
	        }     //End Loop through the Counties
				        
	        // Making size of column auto resize to fit with data
	        sheet.autoSizeColumn(0);
	        sheet.autoSizeColumn(1);
	        sheet.autoSizeColumn(2);
	        sheet.autoSizeColumn(3);
	        sheet.autoSizeColumn(4);
	        
	        
	        logger.info("Giving a name to region with data in raw data sheet.");
	        //TODO: To check for existence of the name before creating
	        CellRangeAddress userRange = new CellRangeAddress(0,sheet.getLastRowNum(),0,4);
	        Name namedRange = workbook.createName();
	        namedRange.setNameName(strSheetName);
	        String reference = userRange.formatAsString(strSheetName, true);
	        namedRange.setRefersToFormula(reference);
	        
	        logger.info(userRange.formatAsString(strSheetName, true));
	        sheet.setAutoFilter(userRange);
	        
	        //Hide the sheet
	        logger.info("Done with downloading the Datavalues and now Hiddin the Raw data Sheet.");
	        workbook.setSheetVisibility(workbook.getSheetIndex(sheet.getSheetName()), SheetVisibility.HIDDEN);
	        
	        logger.info("Preparing HTS Sheet.");
	        PrepareHTSVarinceSheet(workbook,countyFacilitiesList,periods);
 
	        logger.info("Preparing Positivity & Linkage Sheet.");
	        PreparePositivityLinkageVarinceSheet(workbook,countyFacilitiesList,periods);
	        
	        logger.info("Preparing HTS_INDEX Sheet.");
	        PrepareHtsIndexVarinceSheet(workbook,countyFacilitiesList,periods);
	        
	        logger.info("Preparing Care and Treatment Sheet.");
	        PrepareCandTVarinceSheet(workbook,countyFacilitiesList,periods);
	        
	        logger.info("Preparing PMTCT Sheet.");
	        PreparePMTCTVarinceSheet(workbook,countyFacilitiesList,periods);
	        
	        logger.info("Preparing TB Sheet.");
	        PrepareTBVarinceSheet(workbook,countyFacilitiesList,periods);
	        	        
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        workbook.write(outputStream);
	        //outputStream.close();
	        
	        workbook.close();
	        return new ByteArrayInputStream(outputStream.toByteArray());
        } 
        catch (Exception e) 
        {
        	logger.info("There was an error: " + e.toString());
            e.printStackTrace();
            return null;
        }
	}
	
	public static ByteArrayInputStream datavalueListToExcelFile(List<DataValue> dataValue) {
		try(Workbook workbook = new XSSFWorkbook()){
			Sheet sheet = workbook.createSheet("DataValue");
			
			Row row = sheet.createRow(0);
	        CellStyle headerCellStyle = workbook.createCellStyle();
	        headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
	        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	        // Creating header
	        Cell cell = row.createCell(0);
	        cell.setCellValue("First Name");
	        cell.setCellStyle(headerCellStyle);
	        
	        cell = row.createCell(1);
	        cell.setCellValue("Last Name");
	        cell.setCellStyle(headerCellStyle);
	
	        cell = row.createCell(2);
	        cell.setCellValue("Mobile");
	        cell.setCellStyle(headerCellStyle);
	
	        cell = row.createCell(3);
	        cell.setCellValue("Email");
	        cell.setCellStyle(headerCellStyle);
	        
	        // Creating data rows for each customer
	        for(int i = 0; i < dataValue.size(); i++) {
	        	Row dataRow = sheet.createRow(i + 1);
	        	dataRow.createCell(0).setCellValue(dataValue.get(i).getId());
	        	dataRow.createCell(1).setCellValue(dataValue.get(i).getCategoryOptionCombo());
	        	dataRow.createCell(2).setCellValue(dataValue.get(i).getDataElement());
	        	dataRow.createCell(3).setCellValue(dataValue.get(i).getValue());
	        	
	        }
	
	        // Making size of column auto resize to fit with data
	        sheet.autoSizeColumn(0);
	        sheet.autoSizeColumn(1);
	        sheet.autoSizeColumn(2);
	        sheet.autoSizeColumn(3);
	        
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        workbook.write(outputStream);
	        workbook.close();
	        return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException ex) {
			logger.info("This is the issue:" + ex.getMessage());
			return null;
		}
	}
	
	
	private List<DataValue> getHtsDataGapsDataValueSets(String strOrgUnit,String strPeriod){
		String strDataelements ="RbDPjbEK6ME;I9J4Phbuthl;a65nZ4dFy6k;b7xt1wI2zKv;bPeXuh5pZVv;DTZP3FojCQy;sDfyuO9XkjU;ME6QQEiJZEK;";
    	strDataelements = strDataelements + "yazTVdDJqN3;UK3XYffpbRX;aNxyZabe8jt;Osl4LZSFqRV;GGw8R3rGcQc;rQSEFVOPaeP;Jgt5HS8kptu;IZVs7DBmEL7;";
    	strDataelements = strDataelements + "XU4LRU4tYLm;IIbcp8pzrVY;AX80EipWH2H;GRpZZfms6dx;vRXEMmg25ki;ESCRFuuK2vJ;qkmZZeYziM9;yUewTkVfIUO;";
    	strDataelements = strDataelements + "mDLO8cdb81R;s2ovnYHX3U3;dS4MkqKkjw4;R6AJLKl8T73;Ay3PoOyEk2o;I4AXUezsCD4;WcRbiWXvRTl;QcTr2U69gHm;";
    	strDataelements = strDataelements + "hE1fWGHIQbB;mtf99Natwzc;LkWWc7Ze2FC;";

		ResponseEntity<DataValues> responseEntity =  restDHIS2Util.getPriAnalysticsDataValueSets(strDataelements,strOrgUnit,strPeriod);
		DataValues dataValues = responseEntity.getBody();
		return dataValues.getDataValues();
	}
	private List<DataValue> getHtsIndexDataGapsDataValueSets(String strOrgUnit,String strPeriod){
		//gp0RYhjsc1f;sTBggmuuiGR;n7EtXqtIYbY;jwdD1sNc2qV;gtuoKQZCCdM;r4iOz7SplEm;pJ9S0wgNfMT;jYfjmQmkZo6;K9S00D1xCLU;AcJFDddl6JC;GZUcq8sKVlI;QuGzFuOw4pe;Jud4BVUbA3f
		String strDataelements ="gp0RYhjsc1f;sTBggmuuiGR;n7EtXqtIYbY;jwdD1sNc2qV;gtuoKQZCCdM;r4iOz7SplEm;pJ9S0wgNfMT;jYfjmQmkZo6;K9S00D1xCLU;AcJFDddl6JC;GZUcq8sKVlI;QuGzFuOw4pe;Jud4BVUbA3f;";
		//getPriDataValueSets
		//ResponseEntity<DataValues> responseEntity =  restDHIS2Util.getVarinceDataValueSets(strDataelements,strOrgUnit,strPeriod);
		ResponseEntity<DataValues> responseEntity =  restDHIS2Util.getPriAnalysticsDataValueSets(strDataelements,strOrgUnit,strPeriod);
		DataValues dataValues = responseEntity.getBody();
		return dataValues.getDataValues();
	}
	
	private List<DataValue> getTBDataGapsDataValueSets(String strOrgUnit,String strPeriod){
		//wwiOok2aUaD;FvjbCEixzhS;DWyMzkaAfue;O4uQeYj5u14;KvkqQwgmhjJ;uYOg95RNj9B;sZJUeOZB4au;rcb4bM7fK5P;iOqDP6sFlIE;Skan9r4q3Ug;oO5NmuN2dp4;UAWtNnp1vXL;aM0HO40iBJv;c0C0RK9Fszy;b7rReLAkhk9;xhOV8WXKHaz;GB3Dc7mvfT6;soTnKNs4uqi
		String strDataelements ="wwiOok2aUaD;FvjbCEixzhS;DWyMzkaAfue;O4uQeYj5u14;KvkqQwgmhjJ;uYOg95RNj9B;sZJUeOZB4au;rcb4bM7fK5P;iOqDP6sFlIE;Skan9r4q3Ug;oO5NmuN2dp4;UAWtNnp1vXL;aM0HO40iBJv;c0C0RK9Fszy;b7rReLAkhk9;xhOV8WXKHaz;GB3Dc7mvfT6;soTnKNs4uqi;";
		ResponseEntity<DataValues> responseEntity =  restDHIS2Util.getPriAnalysticsDataValueSets(strDataelements,strOrgUnit,strPeriod);
		DataValues dataValues = responseEntity.getBody();
		return dataValues.getDataValues();
	}
	private List<DataValue> getPMTCTDataGapsDataValueSets(String strOrgUnit,String strPeriod){
		//YhISezyMllU;cU1vxx1QPOD;trWRYQ3zyA6;vZAyU1CcNhS;HZQteHxzpUf;n8x6pvKGdN9;qlmFUrWVm5T;ME6QQEiJZEK;nZC7CulOVgX;rQSEFVOPaeP;AX80EipWH2H;yUewTkVfIUO;b5DEBiA1FMk;Ay3PoOyEk2o;mDLO8cdb81R;s2ovnYHX3U3;phlgtOHe241;TAxlf87Lu4Z;arHi1yFkXfs;wrmfSjcB29U;TuMNs4F7gXj;EIqRiuzQCUQ;T75e6P4RkP7
		String strDataelements ="YhISezyMllU;cU1vxx1QPOD;trWRYQ3zyA6;vZAyU1CcNhS;HZQteHxzpUf;n8x6pvKGdN9;qlmFUrWVm5T;ME6QQEiJZEK;nZC7CulOVgX;rQSEFVOPaeP;AX80EipWH2H;yUewTkVfIUO;b5DEBiA1FMk;Ay3PoOyEk2o;mDLO8cdb81R;s2ovnYHX3U3;phlgtOHe241;TAxlf87Lu4Z;arHi1yFkXfs;wrmfSjcB29U;TuMNs4F7gXj;EIqRiuzQCUQ;T75e6P4RkP7;";
		ResponseEntity<DataValues> responseEntity =  restDHIS2Util.getPriAnalysticsDataValueSets(strDataelements,strOrgUnit,strPeriod);
		DataValues dataValues = responseEntity.getBody();
		return dataValues.getDataValues();
	}
	private List<DataValue> getCTDataGapsDataValueSets(String strOrgUnit,String strPeriod){
		//DVGkqxYfoaN;ULVYtRIxS5m;nUdivCZGEVq;DBxz4p6h1Tk;xuJoK3XVfW1;KaHPsyo8ZIq
		String strDataelements ="DVGkqxYfoaN;ULVYtRIxS5m;nUdivCZGEVq;DBxz4p6h1Tk;xuJoK3XVfW1;KaHPsyo8ZIq;";
		ResponseEntity<DataValues> responseEntity =  restDHIS2Util.getPriAnalysticsDataValueSets(strDataelements,strOrgUnit,strPeriod);
		DataValues dataValues = responseEntity.getBody();
		return dataValues.getDataValues();
	}
	
	
	private List<DataValue> getKHISDataValueSets(String strOrgUnit,String strPeriod){
		//DVGkqxYfoaN;ULVYtRIxS5m;nUdivCZGEVq;DBxz4p6h1Tk;xuJoK3XVfW1;KaHPsyo8ZIq
		String strDatasets ="dataSet=Vo4KDrUFwnA&dataSet=ptIUGFkE6jn&dataSet=kAofV66isvC";
		strDatasets = strDatasets +"&dataSet=yrYwif6R6sH&dataSet=xUesg8lcmDs&dataSet=GGgrU5QkjVs&dataSet=bbMNLyKCnkm&dataSet=cvyzaRp8OlE&dataSet=UpS2bTVcClZ";
		ResponseEntity<DataValues> responseEntity =  restDHIS2Util.getSecDataValueSets(strDatasets,strOrgUnit,strPeriod,true,true);
		DataValues dataValues = responseEntity.getBody();
		return dataValues.getDataValues();
	}
	
	
	private void PrepareHTSVarinceSheet(XSSFWorkbook workbook,List<List<OrgUnit>> countyFacilitiesList,List<String> periods) {
        //RegionUtil.setTopBorderColor(IndexedColors.RED.index, userRange1, sheet1);
        //RegionUtil.setBottomBorderColor(IndexedColors.GREEN.index, userRange1, sheet1);
       // RegionUtil.setLeftBorderColor(IndexedColors.BLUE.index, userRange1, sheet1);
       // RegionUtil.setRightBorderColor(IndexedColors.VIOLET.index, userRange1, sheet1);
		String strSheetName ="HTS_Variance";
		
		//XSSFFormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
		
		
        DataFormat format = workbook.createDataFormat();
        
        CellStyle nomStyle = workbook.createCellStyle();  
        nomStyle.setBorderBottom(BorderStyle.MEDIUM);
        nomStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        nomStyle.setBorderRight(BorderStyle.THIN);  
        nomStyle.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        nomStyle.setBorderTop(BorderStyle.MEDIUM);  
        nomStyle.setBorderBottom(BorderStyle.MEDIUM);
        nomStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        
        CellStyle percStyle = workbook.createCellStyle();  
        percStyle.setBorderBottom(BorderStyle.MEDIUM);
        percStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        percStyle.setBorderRight(BorderStyle.THIN);  
        percStyle.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        percStyle.setBorderTop(BorderStyle.MEDIUM);  
        percStyle.setBorderBottom(BorderStyle.MEDIUM);
        percStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        percStyle.setDataFormat(format.getFormat("0.0%"));
        percStyle.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle numStyle = workbook.createCellStyle();  
        numStyle.setBorderBottom(BorderStyle.MEDIUM);
        numStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        numStyle.setBorderRight(BorderStyle.THIN);  
        numStyle.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        numStyle.setBorderTop(BorderStyle.MEDIUM);  
        numStyle.setBorderBottom(BorderStyle.MEDIUM);
        numStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        numStyle.setDataFormat(format.getFormat("######"));
        numStyle.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle dateStyle = workbook.createCellStyle();  
        dateStyle.setBorderBottom(BorderStyle.MEDIUM);
        dateStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        dateStyle.setBorderRight(BorderStyle.THIN);  
        dateStyle.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        dateStyle.setBorderTop(BorderStyle.MEDIUM);  
        dateStyle.setBorderBottom(BorderStyle.MEDIUM);
        dateStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        dateStyle.setDataFormat(format.getFormat("dd-mmm"));
        dateStyle.setAlignment(HorizontalAlignment.RIGHT); 
        
        //Prepare the HTS_Variance Sheet
        XSSFSheet sheet1 = workbook.getSheet(strSheetName);
        logger.info("Getting Sheet " + strSheetName);
        int intRow = 2;
        
        for (int x = 0; x < countyFacilitiesList.size(); x++) {
        	//Get the List of Facilities from a County
        	List<OrgUnit> orgUnits = countyFacilitiesList.get(x);
        	for (int i = 0; i < orgUnits.size(); i++) {
        	for (int j = 0; j < periods.size(); j++) {
	        
	        	Row dataRow = sheet1.createRow(intRow);
	        	dataRow.createCell(0).setCellValue(orgUnits.get(i).getUuid());
	        	dataRow.createCell(1).setCellValue(orgUnits.get(i).getCounty());
	        	dataRow.createCell(2).setCellValue(orgUnits.get(i).getSubCounty());
	        	dataRow.createCell(3).setCellValue(orgUnits.get(i).getWard());
	        	
	        	dataRow.createCell(4).setCellValue(Integer.parseInt(periods.get(j)));
	        	
	        	dataRow.getCell(4).setCellStyle(numStyle);
	        	
	        	dataRow.createCell(5).setCellValue(orgUnits.get(i).getName());
	        	dataRow.createCell(6).setCellValue(orgUnits.get(i).getMfl());
	        	dataRow.createCell(7).setCellFormula("VLOOKUP(" + dataRow.getCell(4).getAddress() + ",Period_Months,2,FALSE)");
	        	//workbook.getCreationHelper().createFormulaEvaluator().evaluateInCell(dataRow.getCell(7));
	        	//myStyle
	        	dataRow.getCell(0).setCellStyle(nomStyle);
	        	dataRow.getCell(1).setCellStyle(nomStyle);
	        	dataRow.getCell(2).setCellStyle(nomStyle);
	        	dataRow.getCell(3).setCellStyle(nomStyle);
	        	dataRow.getCell(4).setCellStyle(numStyle);
	        	dataRow.getCell(5).setCellStyle(nomStyle);
	        	dataRow.getCell(6).setCellStyle(nomStyle);
	        	dataRow.getCell(7).setCellStyle(dateStyle);
	        	//Prepare the Formulas to pupulate data from
	        	for (int intCol = 8; intCol < 40; intCol++) {
	        		CellRangeAddress strRange = new CellRangeAddress(0,0,intCol,intCol);
	        		if((intCol >= 20 && intCol <= 24) || (intCol >= 35 && intCol <= 39)) {
	        			//Ensure that there is a Zero in the Columns if the result is an error from the VLOOKUP function.
	        			dataRow.createCell(intCol).setCellFormula("IFERROR(VLOOKUP(CONCATENATE(" + dataRow.getCell(0).getAddress() + ",\"_\"," + strRange.formatAsString("HTS_Variance", true) + ",\"_\"," + dataRow.getCell(4).getAddress() +"),HTS_Variance_raw,5,FALSE),0)");	        			 
	        		}else {
	        			dataRow.createCell(intCol).setCellFormula("IFERROR(VLOOKUP(CONCATENATE(" + dataRow.getCell(0).getAddress() + ",\"_\"," + strRange.formatAsString("HTS_Variance", true) + ",\"_\"," + dataRow.getCell(4).getAddress() +"),HTS_Variance_raw,5,FALSE),\"\")");
//	        			 CellValue cellValue = formulaEvaluator.evaluate(dataRow.getCell(intCol));
//	        			 dataRow.getCell(intCol).setCellValue(cellValue.getNumberValue());
	        		}
	        		dataRow.getCell(intCol).setCellStyle(numStyle);
	        		//workbook.getCreationHelper().createFormulaEvaluator().evaluateInCell(dataRow.getCell(intCol));
	        	}
	        	
	
	        	//=COUNTIF(U3:Y3,"<>0")+ COUNTIF(AJ3:AN3,"<>0")
        		dataRow.createCell(40).setCellFormula("COUNTIF(" + dataRow.getCell(20).getAddress() + ":"+ dataRow.getCell(24).getAddress() + ",\"<>0\")+ COUNTIF(" + dataRow.getCell(35).getAddress() + ":"+ dataRow.getCell(39).getAddress() + ",\"<>0\")");
        		dataRow.getCell(40).setCellStyle(numStyle);
        		//workbook.getCreationHelper().createFormulaEvaluator().evaluateInCell(dataRow.getCell(40));
        		//=ABS(U3)+ABS(V3)+ABS(W3)+ABS(X3) dataRow.getCell(0).getAddress()
        		dataRow.createCell(41).setCellFormula("ABS(" + dataRow.getCell(20).getAddress() + ")+ABS(" + dataRow.getCell(21).getAddress() + ")+ABS(" + dataRow.getCell(22).getAddress() + ")+ABS(" + dataRow.getCell(23).getAddress() + ")");
        		dataRow.getCell(41).setCellStyle(numStyle);
        		//workbook.getCreationHelper().createFormulaEvaluator().evaluateInCell(dataRow.getCell(41));
        		//=ABS(AJ3)+ABS(AK3)+ABS(AL3)+ABS(AM3)+ABS(AN3)
        		dataRow.createCell(42).setCellFormula("ABS(" + dataRow.getCell(35).getAddress() + ")+ABS(" + dataRow.getCell(36).getAddress() + ")+ABS(" + dataRow.getCell(37).getAddress() + ")+ABS(" + dataRow.getCell(38).getAddress() + ")+ABS(" + dataRow.getCell(39).getAddress() + ")");
        		dataRow.getCell(42).setCellStyle(numStyle);
        		//workbook.getCreationHelper().createFormulaEvaluator().evaluateInCell(dataRow.getCell(42));
        		//=IF(AO3<>0,"Has variance between KHIS and AMEP"," ")
        		dataRow.createCell(43).setCellFormula("IF(" + dataRow.getCell(40).getAddress() + "<>0,\"Has variance between KHIS and AMEP\",\" \")");
        		dataRow.getCell(43).setCellStyle(nomStyle);
        		dataRow.createCell(44).setCellValue(orgUnits.get(i).getHts());
        		dataRow.createCell(45).setCellValue(orgUnits.get(i).getPmtct());
        		//=IF(AS4=1,IF(O4="","No","Yes"),"N/A")
    			dataRow.createCell(46).setCellFormula("IF("+ dataRow.getCell(44).getAddress() + "=1,IF(" + dataRow.getCell(14).getAddress() + "=\"\",\"Missing\",\"Reported\"),\"N/A\")");
    			//=IF(AT4=1,IF(S4="","No","Yes"),"N/A")
    			dataRow.createCell(47).setCellFormula("IF("+ dataRow.getCell(45).getAddress() + "=1,IF(" + dataRow.getCell(18).getAddress() + "=\"\",\"Missing\",\"Reported\"),\"N/A\")");
    			
    			dataRow.getCell(44).setCellStyle(nomStyle);
    			dataRow.getCell(45).setCellStyle(nomStyle);
    			dataRow.getCell(46).setCellStyle(nomStyle);
    			dataRow.getCell(47).setCellStyle(nomStyle);
    			
    			intRow = intRow + 1;
	        }
        	}
        }
        
        Row dataRow = sheet1.createRow(intRow +2);
    	for (int intCol = 8; intCol < 40; intCol++) {
    		CellRangeAddress strRange = new CellRangeAddress(2,intRow-1,intCol,intCol);
    		dataRow.createCell(intCol).setCellFormula("SUBTOTAL(109," + strRange.formatAsString("HTS_Variance", true) + ")");
    		dataRow.getCell(intCol).setCellStyle(numStyle);
    	}
    	
        SheetConditionalFormatting sheetConditionalFormatting = sheet1.getSheetConditionalFormatting();

        ConditionalFormattingRule rule = sheetConditionalFormatting.createConditionalFormattingRule(ComparisonOperator.LT,"0");
        PatternFormatting fill1 = rule.createPatternFormatting();
        fill1.setFillBackgroundColor(IndexedColors.YELLOW.index);
        fill1.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
        
        
        SheetConditionalFormatting sheetConditionalFormatting1 = sheet1.getSheetConditionalFormatting();

        ConditionalFormattingRule rule1 = sheetConditionalFormatting1.createConditionalFormattingRule(ComparisonOperator.GT,"0");
        PatternFormatting fill11 = rule1.createPatternFormatting();
        fill11.setFillBackgroundColor(IndexedColors.RED.index);
        fill11.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
        
        ConditionalFormattingRule [] cfRules =
        	{
        	    rule, rule1
        	};

        CellRangeAddress[] regions = {
                CellRangeAddress.valueOf(new CellRangeAddress(1,intRow-1,20,24).formatAsString()),
                CellRangeAddress.valueOf(new CellRangeAddress(1,intRow-1,35,39).formatAsString())
        };

        sheetConditionalFormatting.addConditionalFormatting(regions, cfRules);
        
        //Create the name range for use in the Pivottable.
        /////////////////////////////////////////////
        CellRangeAddress userRange1 = new CellRangeAddress(1,intRow-1,0,47);
        Name namedRange1 = workbook.createName();
        namedRange1.setNameName(strSheetName);
        String reference1 = userRange1.formatAsString(strSheetName, true);
        namedRange1.setRefersToFormula(reference1);

        logger.info("Creating HTS_Variance for Pivottables and the Filter Feature!");
        
        sheet1.setAutoFilter(userRange1);
        /////////////////////
    
        
        sheet1.setColumnHidden(4, true);
        sheet1.getRow(0).setZeroHeight(true);
	}
	
	private void PreparePositivityLinkageVarinceSheet(XSSFWorkbook workbook,List<List<OrgUnit>> countyFacilitiesList,List<String> periods) {
        //RegionUtil.setTopBorderColor(IndexedColors.RED.index, userRange1, sheet1);
        //RegionUtil.setBottomBorderColor(IndexedColors.GREEN.index, userRange1, sheet1);
       // RegionUtil.setLeftBorderColor(IndexedColors.BLUE.index, userRange1, sheet1);
       // RegionUtil.setRightBorderColor(IndexedColors.VIOLET.index, userRange1, sheet1);
        DataFormat format = workbook.createDataFormat();
        
        CellStyle nomStyle = workbook.createCellStyle();  
        nomStyle.setBorderBottom(BorderStyle.MEDIUM);
        nomStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        nomStyle.setBorderRight(BorderStyle.THIN);  
        nomStyle.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        nomStyle.setBorderTop(BorderStyle.MEDIUM);  
        nomStyle.setBorderBottom(BorderStyle.MEDIUM);
        nomStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        
        CellStyle percStyle = workbook.createCellStyle();  
        percStyle.setBorderBottom(BorderStyle.MEDIUM);
        percStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        percStyle.setBorderRight(BorderStyle.THIN);  
        percStyle.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        percStyle.setBorderTop(BorderStyle.MEDIUM);  
        percStyle.setBorderBottom(BorderStyle.MEDIUM);
        percStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        percStyle.setDataFormat(format.getFormat("0.0%"));
        percStyle.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle numStyle = workbook.createCellStyle();  
        numStyle.setBorderBottom(BorderStyle.MEDIUM);
        numStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        numStyle.setBorderRight(BorderStyle.THIN);  
        numStyle.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        numStyle.setBorderTop(BorderStyle.MEDIUM);  
        numStyle.setBorderBottom(BorderStyle.MEDIUM);
        numStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        numStyle.setDataFormat(format.getFormat("######"));
        numStyle.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle dateStyle = workbook.createCellStyle();  
        dateStyle.setBorderBottom(BorderStyle.MEDIUM);
        dateStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        dateStyle.setBorderRight(BorderStyle.THIN);  
        dateStyle.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        dateStyle.setBorderTop(BorderStyle.MEDIUM);  
        dateStyle.setBorderBottom(BorderStyle.MEDIUM);
        dateStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        dateStyle.setDataFormat(format.getFormat("dd-mmm"));
        dateStyle.setAlignment(HorizontalAlignment.RIGHT);

        
        String strSheetName = "Positivity_and_Linkage_Cases";
        //Prepare the HTS_Variance Sheet
        XSSFSheet sheet1 = workbook.getSheet(strSheetName);
        logger.info("Getting Sheet " + strSheetName);
        int intRow = 3;
        
        for (int x = 0; x < countyFacilitiesList.size(); x++) {
        	//Get the List of Facilities from a County
        	List<OrgUnit> orgUnits = countyFacilitiesList.get(x);
        	for (int i = 0; i < orgUnits.size(); i++) {
        	for (int j = 0; j < periods.size(); j++) {
	        
	        	Row dataRow = sheet1.createRow(intRow);
	        	dataRow.createCell(0).setCellValue(orgUnits.get(i).getUuid());
	        	dataRow.createCell(1).setCellValue(orgUnits.get(i).getCounty());
	        	dataRow.createCell(2).setCellValue(orgUnits.get(i).getSubCounty());
	        	dataRow.createCell(3).setCellValue(orgUnits.get(i).getWard());    	
	        	dataRow.createCell(4).setCellValue(Integer.parseInt(periods.get(j)));
	        	dataRow.createCell(5).setCellValue(orgUnits.get(i).getName());
	        	dataRow.createCell(6).setCellValue(orgUnits.get(i).getMfl());
	        	dataRow.createCell(7).setCellFormula("VLOOKUP(" + dataRow.getCell(4).getAddress() + ",Period_Months,2,FALSE)");

	        	dataRow.getCell(0).setCellStyle(nomStyle);
	        	dataRow.getCell(1).setCellStyle(nomStyle);
	        	dataRow.getCell(2).setCellStyle(nomStyle);
	        	dataRow.getCell(3).setCellStyle(nomStyle);
	        	dataRow.getCell(4).setCellStyle(numStyle);
	        	dataRow.getCell(5).setCellStyle(nomStyle);
	        	dataRow.getCell(6).setCellStyle(nomStyle);
	        	dataRow.getCell(7).setCellStyle(dateStyle);
	        	//Prepare the Formulas to pupulate data from
	        	for (int intCol = 8; intCol < 18; intCol++) {
	        		CellRangeAddress strRange = new CellRangeAddress(0,0,intCol,intCol);
	        		switch(intCol) {
	        		  case 10:
	        			  //=IFERROR(J6010/I6010,0)
	        			  dataRow.createCell(intCol).setCellFormula("IFERROR(" + dataRow.getCell(9).getAddress() + "/" + dataRow.getCell(8).getAddress() + ",0)");
	  	        		  //dataRow.getCell(intCol).setCellStyle(style2);
	  	        		  dataRow.getCell(intCol).setCellStyle(percStyle); 
	  	        		 
	  	        		  //percStyle
	  	        		  //dataRow.getCell(intCol).setCellStyle(percStyle);
	        			  break;
	        		  case 12:
	        			  //=IFERROR(L6010/J6010,0)
	        			  dataRow.createCell(intCol).setCellFormula("IFERROR(" + dataRow.getCell(11).getAddress() + "/" + dataRow.getCell(9).getAddress() + ",0)");
	  	        		  //dataRow.getCell(intCol).setCellStyle(style2);
	  	        		  //dataRow.getCell(intCol).setCellStyle(myStyle);
	  	        		  dataRow.getCell(intCol).setCellStyle(percStyle);
	        			  break;
	        		  case 16:
	        			  //=IFERROR(P6010/J6010,0)
	        			  dataRow.createCell(intCol).setCellFormula("IFERROR(" + dataRow.getCell(15).getAddress() + "/" + dataRow.getCell(9).getAddress() + ",0)");
	  	        		  dataRow.getCell(intCol).setCellStyle(percStyle);
	  	        		  //dataRow.getCell(intCol).setCellStyle(myStyle);
	  	        		  //dataRow.getCell(intCol).setCellStyle(percStyle);
	        			  break;
	        		  case 17:
	        			  //Comments
	        			  //=IF(AND(J6010<>"",Q6010<100%),"Low Actual Linkage",IF(AND(J6010<>0,Q6010>105%),"High Actutal  Linkage",IF(AND(J6010<>0,K6010>10%),"Positivity above 10%",IF(AND(J6010<>0,M6010>105%),"High Proxy Linkage",IF(AND(M6010<>0,M6010<90%),"Low Proxy Linkage"," ")))))
	        			  dataRow.createCell(intCol).setCellFormula("IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(16).getAddress()  + "<100%),\"Low Actual Linkage\",IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(16).getAddress()  + ">100%),\"High Actutal  Linkage\",IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(10).getAddress()  + ">10%),\"Positivity above 10%\",IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(16).getAddress()  + ">105%),\"High Proxy Linkage\",IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(16).getAddress()  + "<90%),\"Low Proxy Linkage\",\" \")))))");
	        			  dataRow.getCell(intCol).setCellStyle(nomStyle); 
	        			  break;		        		  
	        		  default:
	        			  dataRow.createCell(intCol).setCellFormula("IFERROR(VLOOKUP(CONCATENATE(" + dataRow.getCell(0).getAddress() + ",\"_\"," + strRange.formatAsString(strSheetName, true) + ",\"_\"," + dataRow.getCell(4).getAddress() +"),HTS_Variance_raw,5,FALSE),\"\")");
	  	        		  dataRow.getCell(intCol).setCellStyle(nomStyle);  
	        		}		
	        	}
    			
    			intRow = intRow + 1;
	        }
        	}
        }
        
        SheetConditionalFormatting sheetConditionalFormatting = sheet1.getSheetConditionalFormatting();

        ConditionalFormattingRule rule = sheetConditionalFormatting.createConditionalFormattingRule(ComparisonOperator.GT,"0.1");
        PatternFormatting fill11 = rule.createPatternFormatting();
        fill11.setFillBackgroundColor(IndexedColors.RED.index);
        fill11.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        CellRangeAddress[] regions = {
                CellRangeAddress.valueOf(new CellRangeAddress(1,intRow-1,10,10).formatAsString())
        };
        sheetConditionalFormatting.addConditionalFormatting(regions, rule);
        
        //Create the name range for use in the Pivottable.
        /////////////////////////////////////////////
        CellRangeAddress userRange1 = new CellRangeAddress(2,intRow-1,0,17);
        Name namedRange1 = workbook.createName();
        namedRange1.setNameName(strSheetName);
        String reference1 = userRange1.formatAsString(strSheetName, true);
        namedRange1.setRefersToFormula(reference1);

        logger.info("Creating " + strSheetName + " for Pivottables and the Filter Feature!");
        
        sheet1.setAutoFilter(userRange1);
        /////////////////////
        
        sheet1.setColumnHidden(4, true);
        sheet1.getRow(0).setZeroHeight(true);
	}
	
	private void PrepareHtsIndexVarinceSheet(XSSFWorkbook workbook,List<List<OrgUnit>> countyFacilitiesList,List<String> periods) {
		final int INTSTARTROW = 3;
		final int INTENDCOL = 28;
        DataFormat format = workbook.createDataFormat();
       
        
        CellStyle myStyle = workbook.createCellStyle();  
        myStyle.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        myStyle.setBorderRight(BorderStyle.THIN);  
        myStyle.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        myStyle.setBorderTop(BorderStyle.MEDIUM_DASHED);  
        myStyle.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        
        CellStyle myStyle1 = workbook.createCellStyle();  
        myStyle1.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle1.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        myStyle1.setBorderRight(BorderStyle.THIN);  
        myStyle1.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        myStyle1.setBorderTop(BorderStyle.MEDIUM_DASHED);  
        myStyle1.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle1.setTopBorderColor(IndexedColors.BLACK.getIndex());
        myStyle1.setDataFormat(format.getFormat("0.0%"));
        myStyle1.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle myStyle2 = workbook.createCellStyle();  
        myStyle2.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle2.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        myStyle2.setBorderRight(BorderStyle.THIN);  
        myStyle2.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        myStyle2.setBorderTop(BorderStyle.MEDIUM_DASHED);  
        myStyle2.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle2.setTopBorderColor(IndexedColors.BLACK.getIndex());
        myStyle2.setDataFormat(format.getFormat("######"));
        myStyle2.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle myStyle3 = workbook.createCellStyle();  
        myStyle3.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle3.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        myStyle3.setBorderRight(BorderStyle.THIN);  
        myStyle3.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        myStyle3.setBorderTop(BorderStyle.MEDIUM_DASHED);  
        myStyle3.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle3.setTopBorderColor(IndexedColors.BLACK.getIndex());
        myStyle3.setDataFormat(format.getFormat("dd-mmm"));
        myStyle3.setAlignment(HorizontalAlignment.LEFT);

        
        String strSheetName = "HTS_INDEX_CASCADE";
        //Prepare the HTS_Variance Sheet
        XSSFSheet sheet1 = workbook.getSheet(strSheetName);
        logger.info("Getting Sheet " + strSheetName);
        int intRow = INTSTARTROW;
        
        for (int x = 0; x < countyFacilitiesList.size(); x++) {
        	//Get the List of Facilities from a County
        	List<OrgUnit> orgUnits = countyFacilitiesList.get(x);
        	for (int i = 0; i < orgUnits.size(); i++) {
        	for (int j = 0; j < periods.size(); j++) {
	        
	        	Row dataRow = sheet1.createRow(intRow);
	        	dataRow.createCell(0).setCellValue(orgUnits.get(i).getUuid());
	        	dataRow.createCell(1).setCellValue(orgUnits.get(i).getCounty());
	        	dataRow.createCell(2).setCellValue(orgUnits.get(i).getSubCounty());
	        	dataRow.createCell(3).setCellValue(orgUnits.get(i).getWard());    	
	        	dataRow.createCell(4).setCellValue(Integer.parseInt(periods.get(j)));
	        	dataRow.createCell(5).setCellValue(orgUnits.get(i).getName());
	        	dataRow.createCell(6).setCellValue(orgUnits.get(i).getMfl());
	        	dataRow.createCell(7).setCellFormula("VLOOKUP(" + dataRow.getCell(4).getAddress() + ",Period_Months,2,FALSE)");

	        	dataRow.getCell(0).setCellStyle(myStyle);
	        	dataRow.getCell(1).setCellStyle(myStyle);
	        	dataRow.getCell(2).setCellStyle(myStyle);
	        	dataRow.getCell(3).setCellStyle(myStyle);
	        	dataRow.getCell(4).setCellStyle(myStyle2);
	        	dataRow.getCell(5).setCellStyle(myStyle);
	        	dataRow.getCell(6).setCellStyle(myStyle);
	        	dataRow.getCell(7).setCellStyle(myStyle3);
	        	//Prepare the Formulas to pupulate data from
	        	for (int intCol = 8; intCol < 29; intCol++) {
	        		CellRangeAddress strRange = new CellRangeAddress(0,0,intCol,intCol);
	        		switch(intCol) {
	        		 case 10:
	        			  //=IF(I4302>=J4302,"No","Yes")
	        			dataRow.createCell(intCol).setCellFormula("IF(" + dataRow.getCell(8).getAddress() + ">=" + dataRow.getCell(9).getAddress() + ",\"No\",\"Yes\")");
	  	        		  //dataRow.getCell(intCol).setCellStyle(style2);
	  	        		//  dataRow.getCell(intCol).setCellStyle(myStyle1); 
	  	        		  //percStyle
	  	        		  //dataRow.getCell(intCol).setCellStyle(percStyle);
	        			break;
	        		 // case 14:
	        			  //=IFERROR(L6010/J6010,0)
	        			  //dataRow.createCell(intCol).setCellFormula("IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(16).getAddress()  + "<100%),\"Low Actual Linkage\",IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(16).getAddress()  + ">100%),\"High Actutal  Linkage\",IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(10).getAddress()  + ">10%),\"Positivity above 10%\",IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(16).getAddress()  + ">105%),\"High Proxy Linkage\",IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(16).getAddress()  + "<90%),\"Low Proxy Linkage\",\" \")))))");
	  	        		  //dataRow.getCell(intCol).setCellStyle(style2);
	  	        		  //dataRow.getCell(intCol).setCellStyle(myStyle);
	  	        		  //dataRow.getCell(intCol).setCellStyle(myStyle1);
	        			 // break;
	        		  //case 15:
	        			  //=IFERROR(P6010/J6010,0)
	        			  //dataRow.createCell(intCol).setCellFormula("IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(16).getAddress()  + "<100%),\"Low Actual Linkage\",IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(16).getAddress()  + ">100%),\"High Actutal  Linkage\",IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(10).getAddress()  + ">10%),\"Positivity above 10%\",IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(16).getAddress()  + ">105%),\"High Proxy Linkage\",IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(16).getAddress()  + "<90%),\"Low Proxy Linkage\",\" \")))))");
	  	        		  //dataRow.getCell(intCol).setCellStyle(myStyle1);
	  	        		  //dataRow.getCell(intCol).setCellStyle(myStyle);
	  	        		  //dataRow.getCell(intCol).setCellStyle(percStyle);
	        			  //break;
	        		  //case 17:
	        			  //Comments
	        			  //=IF(AND(J6010<>"",Q6010<100%),"Low Actual Linkage",IF(AND(J6010<>0,Q6010>105%),"High Actutal  Linkage",IF(AND(J6010<>0,K6010>10%),"Positivity above 10%",IF(AND(J6010<>0,M6010>105%),"High Proxy Linkage",IF(AND(M6010<>0,M6010<90%),"Low Proxy Linkage"," ")))))
	        			  //dataRow.createCell(intCol).setCellFormula("IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(16).getAddress()  + "<100%),\"Low Actual Linkage\",IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(16).getAddress()  + ">100%),\"High Actutal  Linkage\",IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(10).getAddress()  + ">10%),\"Positivity above 10%\",IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(16).getAddress()  + ">105%),\"High Proxy Linkage\",IF(AND(" + dataRow.getCell(9).getAddress()  + "<>\"\"," + dataRow.getCell(16).getAddress()  + "<90%),\"Low Proxy Linkage\",\" \")))))");
	        			  //dataRow.getCell(intCol).setCellStyle(myStyle); 
	        			  //break;		        		  
	        		  default:
	        			  dataRow.createCell(intCol).setCellFormula("IFERROR(VLOOKUP(CONCATENATE(" + dataRow.getCell(0).getAddress() + ",\"_\"," + strRange.formatAsString(strSheetName, true) + ",\"_\"," + dataRow.getCell(4).getAddress() +"),HTS_Variance_raw,5,FALSE),\"\")");
	  	        		  dataRow.getCell(intCol).setCellStyle(myStyle2);  
	        		}		
	        	}
    			
    			intRow = intRow + 1;
	        }
        	}
        }
        
        SheetConditionalFormatting sheetConditionalFormatting = sheet1.getSheetConditionalFormatting();

        ConditionalFormattingRule rule = sheetConditionalFormatting.createConditionalFormattingRule(ComparisonOperator.GT,"0.1");
        PatternFormatting fill11 = rule.createPatternFormatting();
        fill11.setFillBackgroundColor(IndexedColors.RED.index);
        fill11.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        CellRangeAddress[] regions = {
                CellRangeAddress.valueOf(new CellRangeAddress(INTENDCOL,intRow-1,10,10).formatAsString())
        };
        sheetConditionalFormatting.addConditionalFormatting(regions, rule);
        
        //Create the name range for use in the Pivottable.
        /////////////////////////////////////////////
        CellRangeAddress userRange1 = new CellRangeAddress(INTSTARTROW-1,intRow-1,0,INTENDCOL);
        Name namedRange1 = workbook.createName();
        namedRange1.setNameName(strSheetName);
        String reference1 = userRange1.formatAsString(strSheetName, true);
        namedRange1.setRefersToFormula(reference1);

        logger.info("Creating " + strSheetName + " for Pivottables and the Filter Feature!");
        
        
        sheet1.setAutoFilter(userRange1);
        /////////////////////
        
        sheet1.setColumnHidden(4, true);
        sheet1.getRow(0).setZeroHeight(true);
	}
	
	private void PrepareCandTVarinceSheet(XSSFWorkbook workbook,List<List<OrgUnit>> countyFacilitiesList,List<String> periods) {
		final int INTSTARTROW = 2;
		final int INTENDCOL = 15;
		
        DataFormat format = workbook.createDataFormat();
       
        
        CellStyle myStyle = workbook.createCellStyle();  
        myStyle.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        myStyle.setBorderRight(BorderStyle.THIN);  
        myStyle.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        myStyle.setBorderTop(BorderStyle.MEDIUM_DASHED);  
        myStyle.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        
        CellStyle myStyle1 = workbook.createCellStyle();  
        myStyle1.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle1.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        myStyle1.setBorderRight(BorderStyle.THIN);  
        myStyle1.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        myStyle1.setBorderTop(BorderStyle.MEDIUM_DASHED);  
        myStyle1.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle1.setTopBorderColor(IndexedColors.BLACK.getIndex());
        myStyle1.setDataFormat(format.getFormat("0.0%"));
        myStyle1.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle myStyle2 = workbook.createCellStyle();  
        myStyle2.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle2.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        myStyle2.setBorderRight(BorderStyle.THIN);  
        myStyle2.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        myStyle2.setBorderTop(BorderStyle.MEDIUM_DASHED);  
        myStyle2.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle2.setTopBorderColor(IndexedColors.BLACK.getIndex());
        myStyle2.setDataFormat(format.getFormat("######"));
        myStyle2.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle myStyle3 = workbook.createCellStyle();  
        myStyle3.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle3.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        myStyle3.setBorderRight(BorderStyle.THIN);  
        myStyle3.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        myStyle3.setBorderTop(BorderStyle.MEDIUM_DASHED);  
        myStyle3.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle3.setTopBorderColor(IndexedColors.BLACK.getIndex());
        myStyle3.setDataFormat(format.getFormat("dd-mmm"));
        myStyle3.setAlignment(HorizontalAlignment.LEFT);

        
        String strSheetName = "C_and_T_Variance";
        //Prepare the HTS_Variance Sheet
        XSSFSheet sheet1 = workbook.getSheet(strSheetName);
        logger.info("Getting Sheet " + strSheetName);
        int intRow = INTSTARTROW;
        
        for (int x = 0; x < countyFacilitiesList.size(); x++) {
        	//Get the List of Facilities from a County
        	List<OrgUnit> orgUnits = countyFacilitiesList.get(x);
        	for (int i = 0; i < orgUnits.size(); i++) {
        	for (int j = 0; j < periods.size(); j++) {
	        
	        	Row dataRow = sheet1.createRow(intRow);
	        	dataRow.createCell(0).setCellValue(orgUnits.get(i).getUuid());
	        	dataRow.createCell(1).setCellValue(orgUnits.get(i).getCounty());
	        	dataRow.createCell(2).setCellValue(orgUnits.get(i).getSubCounty());
	        	dataRow.createCell(3).setCellValue(orgUnits.get(i).getWard());    	
	        	dataRow.createCell(4).setCellValue(Integer.parseInt(periods.get(j)));
	        	dataRow.createCell(5).setCellValue(orgUnits.get(i).getName());
	        	dataRow.createCell(6).setCellValue(orgUnits.get(i).getMfl());
	        	dataRow.createCell(7).setCellFormula("VLOOKUP(" + dataRow.getCell(4).getAddress() + ",Period_Months,2,FALSE)");

	        	dataRow.getCell(0).setCellStyle(myStyle);
	        	dataRow.getCell(1).setCellStyle(myStyle);
	        	dataRow.getCell(2).setCellStyle(myStyle);
	        	dataRow.getCell(3).setCellStyle(myStyle);
	        	dataRow.getCell(4).setCellStyle(myStyle2);
	        	dataRow.getCell(5).setCellStyle(myStyle);
	        	dataRow.getCell(6).setCellStyle(myStyle);
	        	dataRow.getCell(7).setCellStyle(myStyle3);
	        	//Prepare the Formulas to pupulate data from
	        	for (int intCol = 8; intCol < 16; intCol++) {
	        		CellRangeAddress strRange = new CellRangeAddress(0,0,intCol,intCol);
      			  	dataRow.createCell(intCol).setCellFormula("IFERROR(VLOOKUP(CONCATENATE(" + dataRow.getCell(0).getAddress() + ",\"_\"," + strRange.formatAsString(strSheetName, true) + ",\"_\"," + dataRow.getCell(4).getAddress() +"),HTS_Variance_raw,5,FALSE),\"\")");
	        		dataRow.getCell(intCol).setCellStyle(myStyle2);	
	        	}
    			
    			intRow = intRow + 1;
	        }
        	}
        }
        
        SheetConditionalFormatting sheetConditionalFormatting = sheet1.getSheetConditionalFormatting();

        ConditionalFormattingRule rule = sheetConditionalFormatting.createConditionalFormattingRule(ComparisonOperator.GT,"0.1");
        PatternFormatting fill11 = rule.createPatternFormatting();
        fill11.setFillBackgroundColor(IndexedColors.RED.index);
        fill11.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        CellRangeAddress[] regions = {
                CellRangeAddress.valueOf(new CellRangeAddress(INTSTARTROW,intRow-1,10,10).formatAsString())
        };
        sheetConditionalFormatting.addConditionalFormatting(regions, rule);
        
        //Create the name range for use in the Pivottable.
        /////////////////////////////////////////////
        CellRangeAddress userRange1 = new CellRangeAddress(INTSTARTROW-1,intRow-1,0,INTENDCOL);
        Name namedRange1 = workbook.createName();
        namedRange1.setNameName(strSheetName);
        String reference1 = userRange1.formatAsString(strSheetName, true);
        namedRange1.setRefersToFormula(reference1);

        logger.info("Creating " + strSheetName + " for Pivottables and the Filter Feature!");
        
        sheet1.setAutoFilter(userRange1);
        /////////////////////
        
        sheet1.setColumnHidden(4, true);
        sheet1.getRow(0).setZeroHeight(true);
	}	
	
	private void PreparePMTCTVarinceSheet(XSSFWorkbook workbook,List<List<OrgUnit>> countyFacilitiesList,List<String> periods) {
		final int INTSTARTROW = 2;
		final int INTENDCOL = 32;
		
        DataFormat format = workbook.createDataFormat();
       
        
        CellStyle myStyle = workbook.createCellStyle();  
        myStyle.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        myStyle.setBorderRight(BorderStyle.THIN);  
        myStyle.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        myStyle.setBorderTop(BorderStyle.MEDIUM_DASHED);  
        myStyle.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        
        CellStyle myStyle1 = workbook.createCellStyle();  
        myStyle1.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle1.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        myStyle1.setBorderRight(BorderStyle.THIN);  
        myStyle1.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        myStyle1.setBorderTop(BorderStyle.MEDIUM_DASHED);  
        myStyle1.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle1.setTopBorderColor(IndexedColors.BLACK.getIndex());
        myStyle1.setDataFormat(format.getFormat("0.0%"));
        myStyle1.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle myStyle2 = workbook.createCellStyle();  
        myStyle2.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle2.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        myStyle2.setBorderRight(BorderStyle.THIN);  
        myStyle2.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        myStyle2.setBorderTop(BorderStyle.MEDIUM_DASHED);  
        myStyle2.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle2.setTopBorderColor(IndexedColors.BLACK.getIndex());
        myStyle2.setDataFormat(format.getFormat("######"));
        myStyle2.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle myStyle3 = workbook.createCellStyle();  
        myStyle3.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle3.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        myStyle3.setBorderRight(BorderStyle.THIN);  
        myStyle3.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        myStyle3.setBorderTop(BorderStyle.MEDIUM_DASHED);  
        myStyle3.setBorderBottom(BorderStyle.MEDIUM_DASHED);
        myStyle3.setTopBorderColor(IndexedColors.BLACK.getIndex());
        myStyle3.setDataFormat(format.getFormat("dd-mmm"));
        myStyle3.setAlignment(HorizontalAlignment.LEFT);

        
        String strSheetName = "PMTCT_Variance";
        //Prepare the HTS_Variance Sheet
        XSSFSheet sheet1 = workbook.getSheet(strSheetName);
        logger.info("Getting Sheet " + strSheetName);
        int intRow = INTSTARTROW;
        
        for (int x = 0; x < countyFacilitiesList.size(); x++) {
        	//Get the List of Facilities from a County
        	List<OrgUnit> orgUnits = countyFacilitiesList.get(x);
        	for (int i = 0; i < orgUnits.size(); i++) {
        	for (int j = 0; j < periods.size(); j++) {
	        
	        	Row dataRow = sheet1.createRow(intRow);
	        	dataRow.createCell(0).setCellValue(orgUnits.get(i).getUuid());
	        	dataRow.createCell(1).setCellValue(orgUnits.get(i).getCounty());
	        	dataRow.createCell(2).setCellValue(orgUnits.get(i).getSubCounty());
	        	dataRow.createCell(3).setCellValue(orgUnits.get(i).getWard());    	
	        	dataRow.createCell(4).setCellValue(Integer.parseInt(periods.get(j)));
	        	dataRow.createCell(5).setCellValue(orgUnits.get(i).getName());
	        	dataRow.createCell(6).setCellValue(orgUnits.get(i).getMfl());
	        	dataRow.createCell(7).setCellFormula("VLOOKUP(" + dataRow.getCell(4).getAddress() + ",Period_Months,2,FALSE)");

	        	dataRow.getCell(0).setCellStyle(myStyle);
	        	dataRow.getCell(1).setCellStyle(myStyle);
	        	dataRow.getCell(2).setCellStyle(myStyle);
	        	dataRow.getCell(3).setCellStyle(myStyle);
	        	dataRow.getCell(4).setCellStyle(myStyle2);
	        	dataRow.getCell(5).setCellStyle(myStyle);
	        	dataRow.getCell(6).setCellStyle(myStyle);
	        	dataRow.getCell(7).setCellStyle(myStyle3);
	        	//Prepare the Formulas to pupulate data from
	        	for (int intCol = 8; intCol < 33; intCol++) {
	        		CellRangeAddress strRange = new CellRangeAddress(0,0,intCol,intCol);
      			  	dataRow.createCell(intCol).setCellFormula("IFERROR(VLOOKUP(CONCATENATE(" + dataRow.getCell(0).getAddress() + ",\"_\"," + strRange.formatAsString(strSheetName, true) + ",\"_\"," + dataRow.getCell(4).getAddress() +"),HTS_Variance_raw,5,FALSE),\"\")");
	        		dataRow.getCell(intCol).setCellStyle(myStyle2);	
	        	}
    			
    			intRow = intRow + 1;
	        }
        	}
        }
        
        SheetConditionalFormatting sheetConditionalFormatting = sheet1.getSheetConditionalFormatting();

        ConditionalFormattingRule rule = sheetConditionalFormatting.createConditionalFormattingRule(ComparisonOperator.GT,"0.1");
        PatternFormatting fill11 = rule.createPatternFormatting();
        fill11.setFillBackgroundColor(IndexedColors.RED.index);
        fill11.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        CellRangeAddress[] regions = {
                CellRangeAddress.valueOf(new CellRangeAddress(INTSTARTROW,intRow-1,10,10).formatAsString())
        };
        sheetConditionalFormatting.addConditionalFormatting(regions, rule);
        
        //Create the name range for use in the Pivottable.
        /////////////////////////////////////////////
        CellRangeAddress userRange1 = new CellRangeAddress(INTSTARTROW-1,intRow-1,0,INTENDCOL);
        Name namedRange1 = workbook.createName();
        namedRange1.setNameName(strSheetName);
        String reference1 = userRange1.formatAsString(strSheetName, true);
        namedRange1.setRefersToFormula(reference1);

        logger.info("Creating " + strSheetName + " for Pivottables and the Filter Feature!");
        
        sheet1.setAutoFilter(userRange1);
        /////////////////////
        
        sheet1.setColumnHidden(4, true);
        sheet1.getRow(0).setZeroHeight(true);
	}

	private void PrepareTBVarinceSheet(XSSFWorkbook workbook,List<List<OrgUnit>> countyFacilitiesList,List<String> periods) {

		final int INTSTARTROW = 2;
		final int INTENDCOL = 28;
        DataFormat format = workbook.createDataFormat();
       
        CellStyle nomStyle = workbook.createCellStyle();  
        nomStyle.setBorderBottom(BorderStyle.MEDIUM);
        nomStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        nomStyle.setBorderRight(BorderStyle.THIN);  
        nomStyle.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        nomStyle.setBorderTop(BorderStyle.MEDIUM);  
        nomStyle.setBorderBottom(BorderStyle.MEDIUM);
        nomStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        
        CellStyle percStyle = workbook.createCellStyle();  
        percStyle.setBorderBottom(BorderStyle.MEDIUM);
        percStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        percStyle.setBorderRight(BorderStyle.THIN);  
        percStyle.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        percStyle.setBorderTop(BorderStyle.MEDIUM);  
        percStyle.setBorderBottom(BorderStyle.MEDIUM);
        percStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        percStyle.setDataFormat(format.getFormat("0.0%"));
        percStyle.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle numStyle = workbook.createCellStyle();  
        numStyle.setBorderBottom(BorderStyle.MEDIUM);
        numStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        numStyle.setBorderRight(BorderStyle.THIN);  
        numStyle.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        numStyle.setBorderTop(BorderStyle.MEDIUM);  
        numStyle.setBorderBottom(BorderStyle.MEDIUM);
        numStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        numStyle.setDataFormat(format.getFormat("######"));
        numStyle.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle dateStyle = workbook.createCellStyle();  
        dateStyle.setBorderBottom(BorderStyle.MEDIUM);
        dateStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        dateStyle.setBorderRight(BorderStyle.THIN);  
        dateStyle.setRightBorderColor(IndexedColors.BLUE.getIndex());  
        dateStyle.setBorderTop(BorderStyle.MEDIUM);  
        dateStyle.setBorderBottom(BorderStyle.MEDIUM);
        dateStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        dateStyle.setDataFormat(format.getFormat("dd-mmm"));
        dateStyle.setAlignment(HorizontalAlignment.RIGHT);

        
        String strSheetName = "TB_CASCADE_Variance";
        //Prepare the HTS_Variance Sheet
        XSSFSheet sheet1 = workbook.getSheet(strSheetName);
        logger.info("Getting Sheet " + strSheetName);
        int intRow = INTSTARTROW;
        
        for (int x = 0; x < countyFacilitiesList.size(); x++) {
        	//Get the List of Facilities from a County
        	List<OrgUnit> orgUnits = countyFacilitiesList.get(x);
        	for (int i = 0; i < orgUnits.size(); i++) {
        	for (int j = 0; j < periods.size(); j++) {
	        
	        	Row dataRow = sheet1.createRow(intRow);
	        	dataRow.createCell(0).setCellValue(orgUnits.get(i).getUuid());
	        	dataRow.createCell(1).setCellValue(orgUnits.get(i).getCounty());
	        	dataRow.createCell(2).setCellValue(orgUnits.get(i).getSubCounty());
	        	dataRow.createCell(3).setCellValue(orgUnits.get(i).getWard());    	
	        	dataRow.createCell(4).setCellValue(Integer.parseInt(periods.get(j)));
	        	dataRow.createCell(5).setCellValue(orgUnits.get(i).getName());
	        	dataRow.createCell(6).setCellValue(orgUnits.get(i).getMfl());
	        	dataRow.createCell(7).setCellFormula("VLOOKUP(" + dataRow.getCell(4).getAddress() + ",Period_Months,2,FALSE)");

	        	dataRow.getCell(0).setCellStyle(nomStyle);
	        	dataRow.getCell(1).setCellStyle(nomStyle);
	        	dataRow.getCell(2).setCellStyle(nomStyle);
	        	dataRow.getCell(3).setCellStyle(nomStyle);
	        	dataRow.getCell(4).setCellStyle(numStyle);
	        	dataRow.getCell(5).setCellStyle(nomStyle);
	        	dataRow.getCell(6).setCellStyle(nomStyle);
	        	dataRow.getCell(7).setCellStyle(dateStyle);
	        	//Prepare the Formulas to pupulate data from
	        	for (int intCol = 8; intCol < 29; intCol++) {
	        		CellRangeAddress strRange = new CellRangeAddress(0,0,intCol,intCol);
      			  	dataRow.createCell(intCol).setCellFormula("IFERROR(VLOOKUP(CONCATENATE(" + dataRow.getCell(0).getAddress() + ",\"_\"," + strRange.formatAsString(strSheetName, true) + ",\"_\"," + dataRow.getCell(4).getAddress() +"),HTS_Variance_raw,5,FALSE),\"\")");
	        		dataRow.getCell(intCol).setCellStyle(numStyle);	
	        	}
    			
    			intRow = intRow + 1;
	        }
        	}
        }
        
        SheetConditionalFormatting sheetConditionalFormatting = sheet1.getSheetConditionalFormatting();

        ConditionalFormattingRule rule = sheetConditionalFormatting.createConditionalFormattingRule(ComparisonOperator.GT,"0.1");
        PatternFormatting fill11 = rule.createPatternFormatting();
        fill11.setFillBackgroundColor(IndexedColors.RED.index);
        fill11.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        CellRangeAddress[] regions = {
                CellRangeAddress.valueOf(new CellRangeAddress(INTSTARTROW,intRow-1,10,10).formatAsString())
        };
        sheetConditionalFormatting.addConditionalFormatting(regions, rule);
        
        //Create the name range for use in the Pivottable.
        /////////////////////////////////////////////
        CellRangeAddress userRange1 = new CellRangeAddress(INTSTARTROW-1,intRow-1,0,INTENDCOL);
        Name namedRange1 = workbook.createName();
        namedRange1.setNameName(strSheetName);
        String reference1 = userRange1.formatAsString(strSheetName, true);
        namedRange1.setRefersToFormula(reference1);

        logger.info("Creating " + strSheetName + " for Pivottables and the Filter Feature!");
        
        sheet1.setAutoFilter(userRange1);
        /////////////////////
        
        sheet1.setColumnHidden(4, true);
        sheet1.getRow(0).setZeroHeight(true);
	}

}