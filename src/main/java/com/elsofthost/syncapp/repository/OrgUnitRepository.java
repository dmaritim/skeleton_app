package com.elsofthost.syncapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elsofthost.syncapp.entity.OrgUnit;


public interface OrgUnitRepository extends JpaRepository<OrgUnit, String>{
	List<OrgUnit> findByCounty(String strCounty);
	List<OrgUnit> findByWard(String strWard);
	List<OrgUnit> findBySubCounty(String strSubCounty);//uuid
	List<OrgUnit> findByUuid(String uuid);
	List<OrgUnit> findByCountyAndHts(String strSubCounty, Integer hts);
	List<OrgUnit> findByCountyAndHtsAndLevel(String strSubCounty, Integer hts,Integer level);
}

