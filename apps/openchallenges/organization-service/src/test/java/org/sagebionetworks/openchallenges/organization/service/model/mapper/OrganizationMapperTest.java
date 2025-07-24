package org.sagebionetworks.openchallenges.organization.service.model.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;

public class OrganizationMapperTest {

  @Test
  public void ConvertToEntity_ShouldReturnDtoProperties_WhenDtoPropertiesPassed() {
    // create new Dto with properties to be copied to organization entity
    OrganizationDto dto = new OrganizationDto();
    dto.setName("Test Organization");
    dto.setDescription("This is a test organization");
    dto.setId(4599L);
    dto.setLogin("login");
    dto.setAvatarKey("avatar Key");
    dto.setWebsiteUrl("url");
    dto.setAcronym(null);

    OrganizationMapper mapper = new OrganizationMapper();

    OrganizationEntity entity = mapper.convertToEntity(dto);

    // verify the entity properties were copied
    Assertions.assertEquals(entity.getId(), dto.getId());
    Assertions.assertEquals(entity.getName(), dto.getName());
    Assertions.assertEquals(entity.getLogin(), dto.getLogin());
    Assertions.assertEquals(entity.getAvatarKey(), dto.getAvatarKey());
    Assertions.assertEquals(entity.getWebsiteUrl(), dto.getWebsiteUrl());
    Assertions.assertEquals(entity.getDescription(), dto.getDescription());
    Assertions.assertEquals(entity.getAcronym(), dto.getAcronym());
  }

  @Test
  public void ConvertToDto_ShouldReturndDtoWithMatchingEntityProperties_WhenEntityPropertiesPassed() {
    OrganizationEntity entity = new OrganizationEntity();

    entity.setName("Test Organization");
    entity.setDescription("This is a test organization");
    entity.setId(4599L);
    entity.setLogin("login");
    entity.setAvatarKey("avatar Key");
    entity.setWebsiteUrl("url");
    entity.setAcronym(null);

    OrganizationMapper mapper = new OrganizationMapper();

    OrganizationDto dto = mapper.convertToDto(entity);

    // verify the entity properties were copied
    Assertions.assertEquals(entity.getId(), dto.getId());
    Assertions.assertEquals(entity.getName(), dto.getName());
    Assertions.assertEquals(entity.getLogin(), dto.getLogin());
    Assertions.assertEquals(entity.getAvatarKey(), dto.getAvatarKey());
    Assertions.assertEquals(entity.getWebsiteUrl(), dto.getWebsiteUrl());
    Assertions.assertEquals(entity.getDescription(), dto.getDescription());
    Assertions.assertEquals(entity.getAcronym(), dto.getAcronym());
  }
}
