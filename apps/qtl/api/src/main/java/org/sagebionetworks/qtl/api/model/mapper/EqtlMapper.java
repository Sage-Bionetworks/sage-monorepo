package org.sagebionetworks.qtl.api.model.mapper;

import org.sagebionetworks.qtl.api.model.document.EqtlDocument;
import org.sagebionetworks.qtl.api.model.document.EqtlDocument.GenomicLocation;
import org.sagebionetworks.qtl.api.model.dto.EffectDirectionDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlIdentifier;
import org.sagebionetworks.qtl.api.model.dto.GenomicLocationDto;
import org.sagebionetworks.qtl.api.util.EnumConverter;
import org.sagebionetworks.qtl.api.util.RequiredFieldValidator;
import org.springframework.stereotype.Component;

@Component
public class EqtlMapper {

  private static final String RECORD_TYPE = "eQTL record";

  private static final RequiredFieldValidator REQUIRED_FIELDS = new RequiredFieldValidator(
    RECORD_TYPE
  );

  public EqtlDto toDto(EqtlDocument document) {
    String variant = REQUIRED_FIELDS.require(document.getVariant(), "variant");
    String ensemblGeneId = REQUIRED_FIELDS.require(document.getEnsemblGeneId(), "ensembl_gene_id");
    String cellTypeId = REQUIRED_FIELDS.require(document.getCellTypeId(), "cell_type_id");

    return EqtlDto.builder()
      .compositeId(
        EqtlIdentifier.builder()
          .variant(variant)
          .ensemblGeneId(ensemblGeneId)
          .cellTypeId(cellTypeId)
          .build()
          .toCompositeId()
      )
      .variant(variant)
      .variantLocation(toGenomicLocationDto(document.getVariantLocation(), "variant_location"))
      .effectDirection(
        EnumConverter.toEnum(
          document.getEffectDirection(),
          EffectDirectionDto::fromValue,
          "effect_direction",
          RECORD_TYPE
        )
      )
      .geneSymbol(REQUIRED_FIELDS.require(document.getGeneSymbol(), "gene_symbol"))
      .ensemblGeneId(ensemblGeneId)
      .geneLocation(toGenomicLocationDto(document.getGeneLocation(), "gene_location"))
      .beta(REQUIRED_FIELDS.require(document.getBeta(), "beta"))
      .adjPVal(REQUIRED_FIELDS.require(document.getAdjPVal(), "adj_p_val"))
      .adjPValNumeric(REQUIRED_FIELDS.require(document.getAdjPValNumeric(), "adj_p_val_numeric"))
      .maf(REQUIRED_FIELDS.require(document.getMaf(), "maf"))
      .minorAllele(REQUIRED_FIELDS.require(document.getMinorAllele(), "minor_allele"))
      .referenceAllele(REQUIRED_FIELDS.require(document.getReferenceAllele(), "reference_allele"))
      .cellTypeId(cellTypeId)
      .cellClass(REQUIRED_FIELDS.require(document.getCellClass(), "cell_class"))
      .cellClassShort(REQUIRED_FIELDS.require(document.getCellClassShort(), "cell_class_short"))
      .cellSubclass(REQUIRED_FIELDS.require(document.getCellSubclass(), "cell_subclass"))
      .cellSubclassShort(
        REQUIRED_FIELDS.require(document.getCellSubclassShort(), "cell_subclass_short")
      )
      .build();
  }

  private GenomicLocationDto toGenomicLocationDto(GenomicLocation location, String fieldName) {
    GenomicLocation required = REQUIRED_FIELDS.require(location, fieldName);
    return GenomicLocationDto.builder()
      .chr(REQUIRED_FIELDS.require(required.getChr(), fieldName + " chr"))
      .start(REQUIRED_FIELDS.require(required.getStart(), fieldName + " start"))
      .end(REQUIRED_FIELDS.require(required.getEnd(), fieldName + " end"))
      .build();
  }
}
