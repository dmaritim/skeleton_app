package com.elsofthost.syncapp.service;

import java.util.List;

import com.elsofthost.syncapp.entity.DataValue;



public interface DataValueService {
	List<DataValue> getAllDataValue();
	List<DataValue> saveAllDataValue(List<DataValue> dataValueList);
}
