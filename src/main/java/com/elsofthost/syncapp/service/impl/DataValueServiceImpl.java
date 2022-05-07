package com.elsofthost.syncapp.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.elsofthost.syncapp.entity.DataValue;
import com.elsofthost.syncapp.repository.DatavalueRepository;
import com.elsofthost.syncapp.service.DataValueService;

@Service
public class DataValueServiceImpl implements DataValueService{
	
	private DatavalueRepository datavalueRepository;
	
	

	public DataValueServiceImpl(DatavalueRepository datavalueRepository) {
		super();
		this.datavalueRepository = datavalueRepository;
	}



	@Override
	public List<DataValue> saveAllDataValue(List<DataValue> dataValueList) {
		return (List<DataValue>) datavalueRepository.saveAll(dataValueList);
	}



	@Override
	public List<DataValue> getAllDataValue() {
		return (List<DataValue>) datavalueRepository.findAll();
	}

}
