package com.elsofthost.syncapp.service;

import java.util.List;

import com.elsofthost.syncapp.entity.OrgUnit;


public interface OrgUnitService {
	OrgUnit saveOrgUnit(OrgUnit orgUnit);
	List<OrgUnit> getAllOrgUnit();
	List<OrgUnit> findByCounty(String strCounty);
	List<OrgUnit> findBySubCounty(String strSubCounty);
	List<OrgUnit> findByCountyAndHts(String strSubCounty,Integer hts);
	List<OrgUnit> findByCountyAndHtsAndLevel(String strSubCounty,Integer hts,Integer level);
	List<OrgUnit> findByWard(String strWard);
	List<OrgUnit> findByUuid(String uuid);
	OrgUnit getOrgUnitById(String uuid);
	OrgUnit updateOrgUnit(OrgUnit orgUnit, String uuid);
	void deleteOrgUnit(String uuid);
	List<OrgUnit> saveAllOrgUnit(List<OrgUnit> orgUnitList);
}
