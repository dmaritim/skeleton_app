package com.elsofthost.syncapp.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.elsofthost.syncapp.entity.OrgUnit;
import com.elsofthost.syncapp.repository.OrgUnitRepository;
import com.elsofthost.syncapp.service.OrgUnitService;

@Service
public class OrgUnitServiceImpl implements OrgUnitService{
	
	private OrgUnitRepository orgUnitRepository;
	
	public OrgUnitServiceImpl(OrgUnitRepository orgUnitRepository) {
		super();
		this.orgUnitRepository = orgUnitRepository;
	}

//	
	@Override
	public OrgUnit saveOrgUnit(OrgUnit orgUnit) {
		return orgUnitRepository.save(orgUnit);
	}

	@Override
	public List<OrgUnit> getAllOrgUnit() {

		return (List<OrgUnit>) orgUnitRepository.findAll();
	}

	@Override
	public OrgUnit getOrgUnitById(String uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrgUnit updateOrgUnit(OrgUnit orgUnit, String uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteOrgUnit(String uuid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<OrgUnit> saveAllOrgUnit(List<OrgUnit> orgUnitList) {
		List<OrgUnit> response = (List<OrgUnit>) orgUnitRepository.saveAll(orgUnitList);
		return response;
	}

	@Override
	public List<OrgUnit> findByCounty(String strCounty) {
		return (List<OrgUnit>) orgUnitRepository.findByCounty(strCounty);
	}

	@Override
	public List<OrgUnit> findBySubCounty(String strSubCounty) {
		return (List<OrgUnit>) orgUnitRepository.findBySubCounty(strSubCounty);
	}

	@Override
	public List<OrgUnit> findByWard(String strWard) {
		return (List<OrgUnit>) orgUnitRepository.findByWard(strWard);
	}

	@Override
	public List<OrgUnit> findByUuid(String uuid) {
		return (List<OrgUnit>) orgUnitRepository.findByUuid(uuid);
	}

	@Override
	public List<OrgUnit> findByCountyAndHts(String strSubCounty, Integer hts) {
		return (List<OrgUnit>) orgUnitRepository.findByCountyAndHts( strSubCounty,  hts);
	}

	@Override
	public List<OrgUnit> findByCountyAndHtsAndLevel(String strSubCounty, Integer hts, Integer level) {
		return (List<OrgUnit>) orgUnitRepository.findByCountyAndHtsAndLevel(strSubCounty, hts, level);
	}
}
