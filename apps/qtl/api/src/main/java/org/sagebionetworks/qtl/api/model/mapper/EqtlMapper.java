package org.sagebionetworks.qtl.api.model.mapper;

import org.sagebionetworks.qtl.api.exception.DataIntegrityException;
import org.sagebionetworks.qtl.api.model.document.EqtlDocument;
import org.sagebionetworks.qtl.api.model.document.EqtlDocument.GenomicLocation;
import org.sagebionetworks.qtl.api.model.dto.EffectDirectionDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlIdentifier;
import org.sagebionetworks.qtl.api.model.dto.GenomicLocationDto;
import org.sagebionetworks.qtl.api.util.EnumConverter;
import org.springframework.stereotype.Component;

@Component
public class EqtlMapper {

  private static final String RECORD_TYPE = "eQTL record";

  public EqtlDto toDto(EqtlDocument document) {
    String variant = requireValue(document.getVariant(), "variant");
    String ensemblGeneId = requireValue(document.getEnsemblGeneId(), "ensembl_gene_id");
    String cellTypeId = requireValue(document.getCellTypeId(), "cell_type_id");

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
      .geneSymbol(requireValue(document.getGeneSymbol(), "gene_symbol"))
      .ensemblGeneId(ensemblGeneId)
      .geneLocation(toGenomicLocationDto(document.getGeneLocation(), "gene_location"))
      .beta(requireValue(document.getBeta(), "beta"))
      .adjPVal(requireValue(document.getAdjPVal(), "adj_p_val"))
      .adjPValNumeric(requireValue(document.getAdjPValNumeric(), "adj_p_val_numeric"))
      .maf(requireValue(document.getMaf(), "maf"))
      .minorAllele(requireValue(document.getMinorAllele(), "minor_allele"))
      .referenceAllele(requireValue(document.getReferenceAllele(), "reference_allele"))
      .cellTypeId(cellTypeId)
      .cellClass(requireValue(document.getCellClass(), "cell_class"))
      .cellClassShort(requireValue(document.getCellClassShort(), "cell_class_short"))
      .cellSubclass(requireValue(document.getCellSubclass(), "cell_subclass"))
      .cellSubclassShort(requireValue(document.getCellSubclassShort(), "cell_subclass_short"))
      .build();
  }

  private GenomicLocationDto toGenomicLocationDto(GenomicLocation location, String fieldName) {
    GenomicLocation required = requireValue(location, fieldName);
    return GenomicLocationDto.builder()
      .chr(requireValue(required.getChr(), fieldName + " chr"))
      .start(requireValue(required.getStart(), fieldName + " start"))
      .end(requireValue(required.getEnd(), fieldName + " end"))
      .build();
  }

  private static <T> T requireValue(T value, String fieldName) {
    if (value == null) {
      throw new DataIntegrityException("Missing " + fieldName + " in " + RECORD_TYPE);
    }
    return value;
  }
}
