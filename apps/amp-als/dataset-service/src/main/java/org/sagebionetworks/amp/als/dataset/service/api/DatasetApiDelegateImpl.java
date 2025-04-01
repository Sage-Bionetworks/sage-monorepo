package org.sagebionetworks.amp.als.dataset.service.api;

import java.util.List;
import java.util.Optional;
import org.sagebionetworks.amp.als.dataset.service.service.DatasetService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

@Component
public class DatasetApiDelegateImpl implements DatasetApiDelegate {

  private static final MediaType APPLICATION_JSON = MediaType.valueOf("application/json");

  private final DatasetService datasetService;

  private final NativeWebRequest request;

  public DatasetApiDelegateImpl(DatasetService datasetService, NativeWebRequest request) {
    this.datasetService = datasetService;
    this.request = request;
  }

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.ofNullable(request);
  }

  @Override
  public ResponseEntity<?> getDataset(Long datasetId) {
    for (MediaType mediaType : getAcceptedMediaTypes(getRequest())) {
      if (mediaType.isCompatibleWith(APPLICATION_JSON)) {
        return ResponseEntity.ok(datasetService.getDataset(datasetId));
      }
    }
    // TODO: Return an error object if this API does not support any of the accepted types
    return ResponseEntity.ok(datasetService.getDataset(datasetId));
  }

  public List<MediaType> getAcceptedMediaTypes(Optional<NativeWebRequest> requestOpt) {
    return requestOpt
      .map(request -> MediaType.parseMediaTypes(request.getHeader("Accept")))
      .orElseGet(List::of);
  }
}
