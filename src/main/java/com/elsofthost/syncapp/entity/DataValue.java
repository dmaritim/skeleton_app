package com.elsofthost.syncapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"id",
"dataElementCategoryOptionCombo",
"orgUnit",
"period",
"value",
"dataElement",
"categoryOptionCombo",
"attributeOptionCombo",
"deleted",
"comment",
"followup"
})

@Entity
@Table(name = "datavalue_downloads")
public class DataValue {

@JsonProperty("id")
@Id
@Column(name = "id")
private String id;

@JsonProperty("dataElementCategoryOptionCombo")
@Column(name = "dataElementCategoryOptionCombo")
private String dataElementCategoryOptionCombo;

@JsonProperty("orgUnit")
@Column(name = "orgUnit")
private String orgUnit;


@JsonProperty("period")
@Column(name = "period")
private String period;


@JsonProperty("value")
@Column(name = "value")
private String value;


@JsonProperty("dataElement")
@Column(name = "dataElement")
private String dataElement;


@JsonProperty("categoryOptionCombo")
@Column(name = "categoryOptionCombo")
private String categoryOptionCombo;


@JsonProperty("attributeOptionCombo")
@Column(name = "attributeOptionCombo")
private String attributeOptionCombo;


@JsonProperty("deleted")
@Column(name = "deleted")
private Boolean deleted = false;


@JsonProperty("comment")
@Column(name = "comment")
private String comment;


@JsonProperty("followup")
@Column(name = "followup")
private Boolean followup;

@JsonProperty("storedBy")
@Column(name = "stored_by")
private String storedBy;

@JsonProperty("created")
@Column(name = "created")
private String created;

@JsonProperty("lastUpdated")
@Column(name = "lastUpdated")
private String lastUpdated;



/**
* No args constructor for use in serialization
*
*/
public DataValue() {
}

/**
*
* @param followup
* @param period
* @param deleted
* @param dataElementCategoryOptionCombo
* @param orgUnit
* @param attributeOptionCombo
* @param categoryOptionCombo
* @param comment
* @param id
* @param dataElement
* @param value
*/
public DataValue(String id, String dataElementCategoryOptionCombo, String orgUnit, String period, String value, String dataElement, String categoryOptionCombo, String attributeOptionCombo, Boolean deleted, String comment, Boolean followup) {
super();
this.id = id;
this.dataElementCategoryOptionCombo = dataElementCategoryOptionCombo;
this.orgUnit = orgUnit;
this.period = period;
this.value = value;
this.dataElement = dataElement;
this.categoryOptionCombo = categoryOptionCombo;
this.attributeOptionCombo = attributeOptionCombo;
this.deleted = deleted;
this.comment = comment;
this.followup = followup;
}

@JsonProperty("id")
public String getId() {
	if(this.id==null) {
		if (this.categoryOptionCombo != null) {
			this.id = orgUnit + "_" + dataElement + "_" + categoryOptionCombo + "_" + period;
		}else {
			this.id = orgUnit + "_" + dataElement + "_" + period;
		}
	}
return id;
}

@JsonProperty("id")
public void setId(String id) {
this.id = id;
}

@JsonProperty("dataElementCategoryOptionCombo")
public String getDataElementCategoryOptionCombo() {
	if(this.dataElementCategoryOptionCombo==null) {
		if (this.categoryOptionCombo != null) {
			this.dataElementCategoryOptionCombo = dataElement + "_" + categoryOptionCombo;
		}else {
			this.dataElementCategoryOptionCombo = dataElement;
		}
	}
	return dataElementCategoryOptionCombo;
}

@JsonProperty("dataElementCategoryOptionCombo")
public void setDataElementCategoryOptionCombo(String dataElementCategoryOptionCombo) {
this.dataElementCategoryOptionCombo = dataElementCategoryOptionCombo;
}



@JsonProperty("period")
public String getPeriod() {
return period;
}

@JsonProperty("period")
public void setPeriod(String period) {
this.period = period;
}

@JsonProperty("value")
public String getValue() {
return value;
}

@JsonProperty("value")
public void setValue(String value) {
this.value = value;
}

@JsonProperty("dataElement")
public String getDataElement() {
return dataElement;
}

@JsonProperty("dataElement")
public void setDataElement(String dataElement) {
this.dataElement = dataElement;
}

@JsonProperty("categoryOptionCombo")
public String getCategoryOptionCombo() {
return categoryOptionCombo;
}

@JsonProperty("categoryOptionCombo")
public void setCategoryOptionCombo(String categoryOptionCombo) {
this.categoryOptionCombo = categoryOptionCombo;
}

@JsonProperty("attributeOptionCombo")
public String getAttributeOptionCombo() {
return attributeOptionCombo;
}

@JsonProperty("attributeOptionCombo")
public void setAttributeOptionCombo(String attributeOptionCombo) {
this.attributeOptionCombo = attributeOptionCombo;
}

@JsonProperty("deleted")
public Boolean getDeleted() {
return deleted;
}

@JsonProperty("deleted")
public void setDeleted(Boolean deleted) {
this.deleted = deleted;
}

@JsonProperty("comment")
public String getComment() {
return comment;
}

@JsonProperty("comment")
public void setComment(String comment) {
this.comment = comment;
}

@JsonProperty("followup")
public Boolean getFollowup() {
return followup;
}

@JsonProperty("followup")
public void setFollowup(Boolean followup) {
this.followup = followup;
}


public String getOrgUnit() {
	return orgUnit;
}

public String getStoredBy() {
	return storedBy;
}

public String getCreated() {
	return created;
}

public String getLastUpdated() {
	return lastUpdated;
}

public void setOrgUnit(String orgUnit) {
	this.orgUnit = orgUnit;
}

public void setStoredBy(String storedBy) {
	this.storedBy = storedBy;
}

public void setCreated(String created) {
	this.created = created;
}

public void setLastUpdated(String lastUpdated) {
	this.lastUpdated = lastUpdated;
}

@Override
public String toString() {
	return "DataValue [id=" + getId() + ", dataElementCategoryOptionCombo=" + getDataElementCategoryOptionCombo() + ", orgUnit="
			+ orgUnit + ", period=" + period + ", value=" + value + ", dataElement=" + dataElement
			+ ", categoryOptionCombo=" + categoryOptionCombo + ", attributeOptionCombo=" + attributeOptionCombo
			+ ", deleted=" + deleted + ", comment=" + comment + ", followup=" + followup + "]";
}

}