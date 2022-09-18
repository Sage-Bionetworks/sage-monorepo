package org.sagebionetworks.challenge.service.rest;

import org.sagebionetworks.challenge.configuration.CustomFeignClientConfiguration;
import org.sagebionetworks.challenge.model.rest.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "challenge-core-service", configuration = CustomFeignClientConfiguration.class)
public interface ChallengeCoreRestClient {

  @RequestMapping(method = RequestMethod.GET, value = "/api/v1/user/{identification}")
  UserResponse readUser(@PathVariable("identification") String identification);
}
