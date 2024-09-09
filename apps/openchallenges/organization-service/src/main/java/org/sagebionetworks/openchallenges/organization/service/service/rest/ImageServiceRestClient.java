package org.sagebionetworks.openchallenges.organization.service.service.rest;

import org.sagebionetworks.openchallenges.organization.service.model.rest.response.ImageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
  name = "openchallenges-image-service",
  url = "http://openchallenges-image-service:8086"
)
public interface ImageServiceRestClient {
  @RequestMapping(method = RequestMethod.GET, value = "/v1/images")
  ImageResponse getImage(@RequestParam(value = "objectKey") String objectKey);
}
