package org.sagebionetworks.openchallenges.organization.service.model.mapper;

import org.junit.jupiter.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;

public class OrganizationMapperTest {
  @Test
  public void
      ConvertToEntity_ShouldReturnEntityNameDescription_WhenEntityNameDescriptionKeysPassed() {

    OrganizationDto dto = new OrganizationDto();
    dto.setName("Test Organization");
    dto.setDescription("This is a test organization");

    OrganizationMapper mapper = new OrganizationMapper();

    OrganizationEntity entity = mapper.convertToEntity(dto);

    assertThat(entity.getName()).isEqualTo(dto.getName());
    assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
  }

  @Test
  public void ConvertToDto_ShouldReturndDtoNameDescription_WhenDtoNameDescriptionKeysPassed() {

    OrganizationEntity entity = new OrganizationEntity();
    entity.setName("Test Organization");
    entity.setDescription("This is a test organization");

    OrganizationMapper mapper = new OrganizationMapper();

    OrganizationDto dto = mapper.convertToDto(entity);

    assertThat(dto.getName()).isEqualTo(entity.getName());
    assertThat(dto.getDescription()).isEqualTo(entity.getDescription());
  }
}
