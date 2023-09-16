package com.elsofthost.syncapp.response;

import java.util.ArrayList;
import java.util.List;
import com.elsofthost.syncapp.entity.DataValue;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"dataValues"
})
public class DataValues {

@JsonProperty("dataValues")
private List<DataValue> dataValues = new ArrayList<>();

/**
* No args constructor for use in serialization
*
*/
public DataValues() {
}

/**
*
* @param dataValues
*/
public DataValues(List<DataValue> dataValues) {
	super();
	this.dataValues = dataValues;
}

@JsonProperty("dataValues")
public List<DataValue> getDataValues() {
return dataValues;
}

@JsonProperty("dataValues")
public void setDataValues(List<DataValue> dataValues) {
this.dataValues = dataValues;
}

@Override
public String toString() {
	return "DataValues [dataValues=" + dataValues + "]";
}

}