package com.elsofthost.syncapp.response;

import java.util.ArrayList;
import java.util.List;

import com.elsofthost.syncapp.entity.OrgUnit;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"orgUnits"
})
public class OrgUnits {
	
	@JsonProperty("orgUnits")
	private List<OrgUnit> orgUnits = new ArrayList<>();

	public OrgUnits() {
		super();
	}

	public OrgUnits(List<OrgUnit> orgUnits) {
		super();
		this.orgUnits = orgUnits;
	}

	public List<OrgUnit> getOrgUnit() {
		return orgUnits;
	}

	public void setOrgUnit(List<OrgUnit> orgUnits) {
		this.orgUnits = orgUnits;
	}

}
