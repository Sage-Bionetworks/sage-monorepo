// package org.sagebionetworks.openchallenges.organization.service.service;

// import java.util.Arrays;
// import java.util.List;
// import
// org.sagebionetworks.openchallenges.organization.service.exception.OrganizationNotFoundException;
// import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
// import
// org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationSearchQueryDto;
// import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationsPageDto;
// import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;
// import org.sagebionetworks.openchallenges.organization.service.model.mapper.OrganizationMapper;
// import
// org.sagebionetworks.openchallenges.organization.service.model.repository.OrganizationRepository;
// import org.sagebionetworks.openchallenges.organization.service.service.OrganizationService;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import java.time.OffsetDateTime;
// import java.util.Collections;
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.BeforeEach;

// public class OrganizationServiceTest {
//   private Long id = 1L;
//   private String name = "Test Organization";
//   private String avatarKey = "avatarKey";
//   private String websiteUrl = "https://example.com";
//   private Integer challengeCount = 5;
//   private String description = "Test description";
//   private OffsetDateTime createdAt = OffsetDateTime.now();
//   private OffsetDateTime updatedAt = OffsetDateTime.now();
//   private String acronym = "TO";

//   @BeforeEach
//   public void setup() {
//     private OrganizationEntity entityFromConstructor;
//     private OrganizationEntity entityFromConstructor2;
//     private final Logger LOG = LoggerFactory.getLogger(OrganizationService.class);
//     private OrganizationRepository organizationRepository;

//     entityFromConstructor = new OrganizationEntity();

//     entityFromConstructor.setId(id);
//     entityFromConstructor.setName(name);
//     entityFromConstructor.setDescription(description);
//     entityFromConstructor.setAvatarKey(avatarKey);
//     entityFromConstructor.setWebsiteUrl(websiteUrl);
//     entityFromConstructor.setChallengeCount(challengeCount);
//     entityFromConstructor.setCreatedAt(createdAt);
//     entityFromConstructor.setUpdatedAt(updatedAt);
//     entityFromConstructor.setAcronym(acronym);
//     entityFromConstructor.setCategories(Collections.emptyList());
//     entityFromConstructor.setChallengeContributions(Collections.emptyList());

//     entityFromConstructor2 = new OrganizationEntity();

//     entityFromConstructor2.setId(id);
//     entityFromConstructor2.setName(name);
//     entityFromConstructor2.setDescription(description);
//     entityFromConstructor2.setAvatarKey(avatarKey);
//     entityFromConstructor2.setWebsiteUrl(websiteUrl);
//     entityFromConstructor2.setChallengeCount(challengeCount);
//     entityFromConstructor2.setCreatedAt(createdAt);
//     entityFromConstructor2.setUpdatedAt(updatedAt);
//     entityFromConstructor2.setAcronym(acronym);
//     entityFromConstructor2.setCategories(Collections.emptyList());
//     entityFromConstructor2.setChallengeContributions(Collections.emptyList());

//   }

//   @Test
//   public void ConvertToEntity_ShouldReturnDtoProperties_WhenDtoPropertiesPassed() {
//   }

// }
