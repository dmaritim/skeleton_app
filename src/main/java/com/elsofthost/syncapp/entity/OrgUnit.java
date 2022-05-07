package com.elsofthost.syncapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"uuid",
"county",
"subCounty",
"ward",
"name",
"datimName",
"datimUuid",
"hts",
"pmtct",
"ct",
"level",
"mfl"
})

@Entity
@Table(name="orgunit")
public class OrgUnit {
	
	@JsonProperty("uuid")
	@Id
	private String uuid;
	
	@JsonProperty("county")
	@Column(name = "county", nullable = false)
	private String county;
	
	@JsonProperty("subCounty")
	@Column(name = "sub_county", nullable = false)
	private String subCounty;
	
	
	@JsonProperty("ward")
	@Column(name = "ward", nullable = false)
	private String ward;
	
	@JsonProperty("name")
	@Column(name = "name", nullable = false)
	private String name;
	
	@JsonProperty("datimName")
	@Column(name = "datim_name")
	private String datimName;
	
	@JsonProperty("datimUuid")
	@Column(name = "datim_uuid")
	private String datimUuid;
	
	@JsonProperty("hts")
	@Column(name = "hts", nullable = false)
	private Integer hts;
	
	@JsonProperty("pmtct")
	@Column(name = "pmtct", nullable = false)
	private Integer pmtct;
	
	@JsonProperty("ct")
	@Column(name = "ct", nullable = false)
	private Integer ct;
	
	@JsonProperty("mfl")
	@Column(name = "mfl")
	private String mfl;
	
	@JsonProperty("level")
	@Column(name = "level")
	private Integer level;
	
	
	
	public OrgUnit() {
		super();
	}


	public OrgUnit(String uuid, String county, String subCounty, String ward, String name, String datimName,
			String datimUuid, Integer hts, Integer pmtct, Integer ct, Integer level,String mfl) {
		super();
		this.uuid = uuid;
		this.county = county;
		this.subCounty = subCounty;
		this.ward = ward;
		this.name = name;
		this.datimName = datimName;
		this.datimUuid = datimUuid;
		this.hts = hts;
		this.pmtct = pmtct;
		this.ct = ct;
		this.level = level;
		this.mfl = mfl;
	}


	public String getUuid() {
		return uuid;
	}
	public String getCounty() {
		return county;
	}
	public String getSubCounty() {
		return subCounty;
	}
	public String getWard() {
		return ward;
	}
	public String getName() {
		return name;
	}
	public String getDatimName() {
		return datimName;
	}
	public String getDatimUuid() {
		return datimUuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public void setSubCounty(String subCounty) {
		this.subCounty = subCounty;
	}
	public void setWard(String ward) {
		this.ward = ward;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setDatimName(String datimName) {
		this.datimName = datimName;
	}
	public void setDatimUuid(String datimUuid) {
		this.datimUuid = datimUuid;
	}
	public Integer getHts() {
		return hts;
	}
	public Integer getPmtct() {
		return pmtct;
	}
	public Integer getCt() {
		return ct;
	}
	public void setHts(Integer hts) {
		this.hts = hts;
	}
	public void setPmtct(Integer pmtct) {
		this.pmtct = pmtct;
	}
	public void setCt(Integer ct) {
		this.ct = ct;
	}


	public String getMfl() {
		return mfl;
	}


	public Integer getLevel() {
		return level;
	}


	public void setMfl(String mfl) {
		this.mfl = mfl;
	}


	public void setLevel(Integer level) {
		this.level = level;
	}
	
}
