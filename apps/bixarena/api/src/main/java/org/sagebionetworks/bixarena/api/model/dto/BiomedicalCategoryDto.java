package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * A category slug used to classify biomedical content.
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public enum BiomedicalCategoryDto {
  
  BIOCHEMISTRY("biochemistry"),
  
  BIOENGINEERING("bioengineering"),
  
  BIOINFORMATICS("bioinformatics"),
  
  CANCER_BIOLOGY("cancer-biology"),
  
  CELL_BIOLOGY("cell-biology"),
  
  CLINICAL_TRIALS("clinical-trials"),
  
  DEVELOPMENTAL_BIOLOGY("developmental-biology"),
  
  EPIDEMIOLOGY("epidemiology"),
  
  EVOLUTIONARY_BIOLOGY("evolutionary-biology"),
  
  GENETICS("genetics"),
  
  GENOMICS("genomics"),
  
  IMMUNOLOGY("immunology"),
  
  MICROBIOLOGY("microbiology"),
  
  MOLECULAR_BIOLOGY("molecular-biology"),
  
  NEUROSCIENCE("neuroscience"),
  
  PATHOLOGY("pathology"),
  
  PHARMACOLOGY_AND_TOXICOLOGY("pharmacology-and-toxicology"),
  
  PHYSIOLOGY("physiology"),
  
  SYNTHETIC_BIOLOGY("synthetic-biology"),
  
  SYSTEMS_BIOLOGY("systems-biology");

  private final String value;

  BiomedicalCategoryDto(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static BiomedicalCategoryDto fromValue(String value) {
    for (BiomedicalCategoryDto b : BiomedicalCategoryDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

