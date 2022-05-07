package com.elsofthost.syncapp.component;


import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.elsofthost.syncapp.exception.ApiRestTemplateErrorHandler;
import com.elsofthost.syncapp.response.DataValues;


import java.util.Arrays;


@Component
public class RestDHIS2Util
{
	
	private static final Logger logger = LoggerFactory.getLogger(RestDHIS2Util.class);
	
	
	private RestTemplate restTemplate;
	
	@Value("${primary.dhis.scheme}")
	private String priDhisScheme;
	
	@Value("${primary.dhis.url}")
	private String priDhisUrl;
	
	@Value("${primary.dhis.username}")
	private String priDhisUsername;
	
	@Value("${primary.dhis.password}")
	private String priDhisPass;

	@Value("${secondary.dhis.scheme}")
	private String secDhisScheme;
	
	@Value("${secondary.dhis.url}")
	private String secDhisUrl;
	
	@Value("${secondary.dhis.username}")
	private String secDhisUsername;
	
	@Value("${secondary.dhis.password}")
	private String secDhisPass;
	
	public String getPriDhisScheme() {
		return priDhisScheme;
	}

	public String getPriDhisUrl() {
		return priDhisUrl;
	}

	public String getPriDhisUsername() {
		return priDhisUsername;
	}

	public String getPriDhisPass() {
		return priDhisPass;
	}

	public String getSecDhisScheme() {
		return secDhisScheme;
	}

	public String getSecDhisUrl() {
		return secDhisUrl;
	}

	public String getSecDhisUsername() {
		return secDhisUsername;
	}

	public String getSecDhisPass() {
		return secDhisPass;
	}

	public void setPriDhisScheme(String priDhisScheme) {
		this.priDhisScheme = priDhisScheme;
	}

	public void setPriDhisUrl(String priDhisUrl) {
		this.priDhisUrl = priDhisUrl;
	}

	public void setPriDhisUsername(String priDhisUsername) {
		this.priDhisUsername = priDhisUsername;
	}

	public void setPriDhisPass(String priDhisPass) {
		this.priDhisPass = priDhisPass;
	}

	public void setSecDhisScheme(String secDhisScheme) {
		this.secDhisScheme = secDhisScheme;
	}

	public void setSecDhisUrl(String secDhisUrl) {
		this.secDhisUrl = secDhisUrl;
	}

	public void setSecDhisUsername(String secDhisUsername) {
		this.secDhisUsername = secDhisUsername;
	}

	public void setSecDhisPass(String secDhisPass) {
		this.secDhisPass = secDhisPass;
	}

	public RestDHIS2Util(RestTemplateBuilder restTemplateBuilder) {
		super();
	    this.restTemplate = restTemplateBuilder
	            .errorHandler(new ApiRestTemplateErrorHandler())
	            .build();
	}
	//Helper function to help in building the Headers
    private HttpHeaders getHeaders (String strUserName, String strPassWord )
    {	
        String adminuserCredentials = strUserName + ":" + strPassWord;
        String encodedCredentials =
                new String(Base64.encodeBase64(adminuserCredentials.getBytes()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Basic " + encodedCredentials);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return httpHeaders;
    }
    
    //Helper method to help in building the RestTemplate. You can have all RestTemplate related Configuration here.
//    private RestTemplate getRestTemplate() {
//
//        RestTemplate restTemplate = new RestTemplate();
//    	return restTemplate;
//    }

 
    public ResponseEntity<DataValues> getPriDataValueSets(String strDataSets,String strOrgUnit,String strPeriod,Boolean includeDeleted,Boolean includeDimension)
    {
    	//period=202108,202109
    	//dataSet=HNhYCTukQ6h
    	//orgUnit=MdNfM770c0k
    	//dimension=co
    	//includeDeleted=true
    	String strQuery = "period={periods}&dataSet={dataSets}&orgUnit={orgUnits}";
    	
    	//Add includeDeleted=true if required
    	if(includeDeleted) {
    		strQuery = strQuery + "&includeDeleted=true";
    	}
    	
    	//Add dimension=co if finer age is required.
    	if(includeDimension) {
    		strQuery = strQuery + "&dimension=co";
    	}
	    UriComponents uriComponents = UriComponentsBuilder.newInstance()
	  	      .scheme(this.priDhisScheme)
	  	      .host(this.priDhisUrl)
	  	      .path("/api/dataValueSets.json")
	  	      .query(strQuery)
	  	      .buildAndExpand(strPeriod,strDataSets,strOrgUnit);
    	
	    String strURL = uriComponents.toUriString();

        HttpHeaders httpHeaders = getHeaders(priDhisUsername,priDhisPass);

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<DataValues> responseEntity = restTemplate.exchange(strURL,HttpMethod.GET, httpEntity, DataValues.class);        
        return responseEntity;
    }
    
    public ResponseEntity<DataValues> getPriAnalysticsDataValueSets(String strDataelements,String strOrgUnit,String strPeriod)
    {
	    UriComponents uriComponents = UriComponentsBuilder.newInstance()
	  	      .scheme(this.priDhisScheme).host(this.priDhisUrl)
	  	      .path("/api/analytics/dataValueSet.json")
	  	      .query("dimension=dx:{dataelements}&dimension=ou:{orgUnits}&dimension=pe:{periods}")
	  	      .buildAndExpand(strDataelements,strOrgUnit,strPeriod);
    	
	    String strURL = uriComponents.toUriString();
  
        HttpHeaders httpHeaders = getHeaders(priDhisUsername,priDhisPass);

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<DataValues> responseEntity = restTemplate.exchange(strURL,HttpMethod.GET, httpEntity, DataValues.class);        
        return responseEntity;
    }
    
    public ResponseEntity<DataValues> getSecDataValueSets(String strDataSets,String strOrgUnit,String strPeriod,Boolean includeDeleted,Boolean includeDimension)
    {
    	//period=202108,202109
    	//dataSet=HNhYCTukQ6h
    	//orgUnit=MdNfM770c0k
    	//dimension=co
    	//includeDeleted=true
    	String strQuery = strDataSets + "&" + strOrgUnit + "&" + strPeriod;
    	//strQuery = strQuery + "&dataSet=Vo4KDrUFwnA&dataSet=ptIUGFkE6jn&dataSet=kAofV66isvC&dataSet=yrYwif6R6sH&dataSet=xUesg8lcmDs&dataSet=GGgrU5QkjVs&dataSet=bbMNLyKCnkm&dataSet=cvyzaRp8OlE&dataSet=UpS2bTVcClZ";
    	
    	//Add includeDeleted=true if required
    	if(includeDeleted) {
    		strQuery = strQuery + "&includeDeleted=true";
    	}
    	
    	//Add dimension=co if finer age is required.
    	if(includeDimension) {
    		strQuery = strQuery + "&dimension=co";
    	}
	    UriComponents uriComponents = UriComponentsBuilder.newInstance()
	  	      .scheme(this.secDhisScheme)
	  	      .host(this.secDhisUrl)
	  	      .path("/api/dataValueSets.json")
	  	      .query(strQuery).build();
    	
	    String strURL = uriComponents.toUriString();
	    logger.info(strURL);

        HttpHeaders httpHeaders = getHeaders(this.secDhisUsername,this.secDhisPass);

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<DataValues> responseEntity = restTemplate.exchange(strURL,HttpMethod.GET, httpEntity, DataValues.class);        
        return responseEntity;
    }
}
