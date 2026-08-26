package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.math.BigDecimal;
import org.sagebionetworks.model.ad.api.next.model.document.FoldChangeResult;
import org.sagebionetworks.model.ad.api.next.model.dto.FoldChangeResultDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class FoldChangeMapper {

  public @Nullable FoldChangeResultDto toNullableDto(@Nullable FoldChangeResult document) {
    if (document == null) {
      return null;
    }
    Double log2Fc = document.getLog2Fc();
    Double adjustedPvalue = document.getAdjPVal();
    if (log2Fc == null || adjustedPvalue == null) {
      return null;
    }
    return new FoldChangeResultDto(BigDecimal.valueOf(log2Fc), BigDecimal.valueOf(adjustedPvalue));
  }
}
