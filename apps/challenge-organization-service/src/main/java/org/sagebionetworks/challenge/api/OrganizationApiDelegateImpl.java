package org.sagebionetworks.challenge.api;

import org.sagebionetworks.challenge.model.dto.OrganizationsPageDto;
import org.sagebionetworks.challenge.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class OrganizationApiDelegateImpl implements OrganizationApiDelegate {

  @Autowired OrganizationService organizationService;

  @Override
  public ResponseEntity<OrganizationsPageDto> listOrganizations(
      Integer pageNumber, Integer pageSize) {
    return ResponseEntity.ok(organizationService.listOrganizations(pageNumber, pageSize));
  }
}
