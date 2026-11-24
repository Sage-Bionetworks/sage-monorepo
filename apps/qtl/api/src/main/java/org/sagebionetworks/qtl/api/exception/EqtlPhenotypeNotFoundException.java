package org.sagebionetworks.qtl.api.exception;

import lombok.experimental.StandardException;

@StandardException
public class EqtlPhenotypeNotFoundException extends RuntimeException {

  public EqtlPhenotypeNotFoundException(String variant, String ensemblGeneId, String cellTypeId) {
    super(
      String.format(
        "eQTL phenotype not found for variant: %s, ensemblGeneId: %s, cellTypeId: %s",
        variant,
        ensemblGeneId,
        cellTypeId
      )
    );
  }
}
