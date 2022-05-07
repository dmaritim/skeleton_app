package com.elsofthost.syncapp.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elsofthost.syncapp.component.ExcelFileExporter;
import com.elsofthost.syncapp.component.ExcelFileReader;
import com.elsofthost.syncapp.component.RestDHIS2Util;
import com.elsofthost.syncapp.entity.DataValue;
import com.elsofthost.syncapp.entity.OrgUnit;
import com.elsofthost.syncapp.response.DataValues;
import com.elsofthost.syncapp.service.DataValueService;
import com.elsofthost.syncapp.service.OrgUnitService;



@RestController
@RequestMapping("/dhis")
public class DHIS2Controller {
	
	@Autowired
	RestDHIS2Util restDHIS2Util;
//	
	@Autowired
	OrgUnitService orgUnitService;
	
	@Autowired
	DataValueService dataValueService;
	
	
	@Autowired
	ExcelFileExporter excelFileExporter;
	

	@GetMapping("/setup/orgunits")
	public List<OrgUnit> getAllOrgUnits(){
		//OrgUnits orgUnits = ExcelFileReader.getOrgUnits();
		//getAllOrgUnit
		//List<OrgUnit> orgUnits1 = orgUnitService.findByCounty("Busia County");
		List<OrgUnit> orgUnits1 = orgUnitService.getAllOrgUnit();
		return orgUnits1;
	}
	
	@GetMapping("/load/orgunits")
	public List<OrgUnit> getAllorgUnits(){
		List<OrgUnit> orgUnits = ExcelFileReader.getOrgUnits();
		List<OrgUnit> orgUnits1 = orgUnitService.saveAllOrgUnit(orgUnits);
		return orgUnits1;
	}
	
	@GetMapping("/htsdatagapanalysis")
	public void htsdatagapanalysis(HttpServletResponse response) throws IOException {
		
		//List of Counties to allow for looping through one by one
		List<String> strCounties = new ArrayList<String>();
		strCounties.add("Elgeyo Marakwet County");
		strCounties.add("Uasin Gishu County");
		strCounties.add("Trans Nzoia County");
		strCounties.add("West Pokot County");		
		
        List<String> strPeriods = new ArrayList<String>();
        
        // add 4 different values to list
//        strPeriods.add("202110");
//        strPeriods.add("202111");
//        strPeriods.add("202112");
        strPeriods.add("202201");
        strPeriods.add("202202");
        strPeriods.add("202203");
        
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd_HH:mm:ss");
        String currentDateTime  = dateFormatter.format(new Date());
        String fileName = "Data_Gaps_as_of_" + currentDateTime + ".xlsx";
        
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        ByteArrayInputStream stream = excelFileExporter.htsDataGapsDownload(strCounties,strPeriods);
        IOUtils.copy(stream, response.getOutputStream());
	}	
	
	@GetMapping("/khisdownload")
	public void khisDownload(HttpServletResponse response) throws IOException {
		
		//List of Counties to allow for looping through one by one
		List<String> strCounties = new ArrayList<String>();
		strCounties.add("Elgeyo Marakwet County");
		strCounties.add("Uasin Gishu County");
		strCounties.add("Trans Nzoia County");
		strCounties.add("West Pokot County");		
		
        List<String> strPeriods = new ArrayList<String>();
        
        // add 4 different values to list
        strPeriods.add("202110");
        strPeriods.add("202111");
        strPeriods.add("202112");
        strPeriods.add("202201");
        strPeriods.add("202202");
        strPeriods.add("202203");
        
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd_HH:mm:ss");
        String currentDateTime  = dateFormatter.format(new Date());
        String fileName = "KHIS_Download_" + currentDateTime + ".xlsx";
        
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        ByteArrayInputStream stream = excelFileExporter.KHISDownload(strCounties,strPeriods);
        IOUtils.copy(stream, response.getOutputStream());
	}
	
	@GetMapping("/amepdatashare")
	public void GetAmepDataShare(HttpServletResponse response) throws IOException {
		
	}	
	
	@GetMapping("/sec/dhisdownload")
	public List<DataValue> getDHISDownload(){
		
		
		List<OrgUnit> countyOrgUnits = new ArrayList<OrgUnit>();
		countyOrgUnits = orgUnitService.findByCountyAndHts("Trans Nzoia County",1);
		String strOrgUnits = "";
		
		//Loop the list of list of OrgUnits.
        for (int j = 0; j < countyOrgUnits.size(); j++) {
        	//logger.info(countyOrgUnits.get(j).getUuid());

            if (j==0) {
            	strOrgUnits = "orgUnit="   + countyOrgUnits.get(j).getUuid();
            }else {
                strOrgUnits = strOrgUnits + "&orgUnit="   +  countyOrgUnits.get(j).getUuid();       	
            }
        }
        strOrgUnits = strOrgUnits + ";";
	        
        List<String> lstPeriods = new ArrayList<String>();
        
        // add 4 different values to list
        lstPeriods.add("202110");
        lstPeriods.add("202111");
        lstPeriods.add("202112");
        lstPeriods.add("202201");
        lstPeriods.add("202202");
        lstPeriods.add("202203");
        String strPeriods = "";
		//Prepare the Period to be fetched. Can be a Period or more.
        for (int i = 0; i < lstPeriods.size(); i++) {
            //System.out.println(periods.get(i));
            if (i==0) {
            	strPeriods = "period=" + lstPeriods.get(i);
            }else {
            	strPeriods = strPeriods + "&period="   +  lstPeriods.get(i);       	
            }
        }
        strPeriods = strPeriods + ";";
        
        List<DataValue> dataValue = getKHISDataValueSets(strOrgUnits,strPeriods);
        
		//List<DataValue> dataValue1 = dataValueService.saveAllDataValue(dataValue);
		return dataValue;
	}
		
	private List<DataValue> getKHISDataValueSets(String strOrgUnit,String strPeriod){
		//DVGkqxYfoaN;ULVYtRIxS5m;nUdivCZGEVq;DBxz4p6h1Tk;xuJoK3XVfW1;KaHPsyo8ZIq
		String strDatasets ="dataSet=Vo4KDrUFwnA&dataSet=ptIUGFkE6jn&dataSet=kAofV66isvC";
		strDatasets = strDatasets +"&dataSet=yrYwif6R6sH&dataSet=xUesg8lcmDs&dataSet=GGgrU5QkjVs&dataSet=bbMNLyKCnkm&dataSet=cvyzaRp8OlE&dataSet=UpS2bTVcClZ";
		ResponseEntity<DataValues> responseEntity =  restDHIS2Util.getSecDataValueSets(strDatasets,strOrgUnit,strPeriod,true,true);
		DataValues dataValues = responseEntity.getBody();
		return dataValues.getDataValues();
	}	
}
