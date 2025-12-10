package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.document.ModelDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneticInfoDto;
import org.sagebionetworks.model.ad.api.next.model.dto.IndividualDataDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDataDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SexDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

  public ModelDto toDto(@Nullable ModelDocument document) {
    if (document == null) {
      return null;
    }

    List<String> matchedControls = document.getMatchedControls() == null
      ? List.of()
      : List.copyOf(document.getMatchedControls());

    List<String> aliases = document.getAliases() == null
      ? List.of()
      : List.copyOf(document.getAliases());

    List<GeneticInfoDto> geneticInfo = document.getGeneticInfo() == null
      ? List.of()
      : document.getGeneticInfo().stream().map(this::toGeneticInfoDto).toList();

    List<ModelDataDto> biomarkers = document.getBiomarkers() == null
      ? List.of()
      : document.getBiomarkers().stream().map(this::toModelDataDto).toList();

    List<ModelDataDto> pathology = document.getPathology() == null
      ? List.of()
      : document.getPathology().stream().map(this::toModelDataDto).toList();

    return new ModelDto(
      document.getName(),
      matchedControls,
      document.getModelType(),
      document.getContributingGroup(),
      document.getStudySynid(),
      document.getRrid(),
      document.getJaxId(),
      document.getAlzforumId(),
      document.getGenotype(),
      aliases,
      document.getGeneExpression(),
      document.getDiseaseCorrelation(),
      document.getSpatialTranscriptomics(),
      geneticInfo,
      biomarkers,
      pathology
    );
  }

  private GeneticInfoDto toGeneticInfoDto(ModelDocument.GeneticInfo geneticInfo) {
    return new GeneticInfoDto(
      geneticInfo.getModifiedGene(),
      geneticInfo.getEnsemblGeneId(),
      geneticInfo.getAllele(),
      geneticInfo.getAlleleType(),
      geneticInfo.getMgiAlleleId()
    );
  }

  private ModelDataDto toModelDataDto(ModelDocument.ModelData modelData) {
    List<IndividualDataDto> data = modelData.getData() == null
      ? List.of()
      : modelData.getData().stream().map(this::toIndividualDataDto).toList();

    return new ModelDataDto(
      modelData.getName(),
      modelData.getEvidenceType(),
      modelData.getTissue(),
      modelData.getAge(),
      modelData.getUnits(),
      modelData.getYAxisMax(),
      data
    );
  }

  private IndividualDataDto toIndividualDataDto(ModelDocument.IndividualData individualData) {
    SexDto sex = SexDto.fromValue(individualData.getSex());

    return new IndividualDataDto(
      individualData.getGenotype(),
      sex,
      individualData.getIndividualId(),
      individualData.getValue()
    );
  }
}
