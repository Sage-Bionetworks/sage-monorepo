package org.sagebionetworks.bixarena.auth.service.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.sagebionetworks.bixarena.auth.service.model.dto.Echo200ResponseDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UtilityApiDelegateImpl implements UtilityApiDelegate {

  @Override
  public ResponseEntity<Echo200ResponseDto> echo() {
    HttpServletRequest req =
      ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    HttpSession session = req.getSession(false);
    String subject = null;
    List<String> roles = List.of();
    if (session != null) {
      subject = (String) session.getAttribute("AUTH_SUBJECT");
      @SuppressWarnings("unchecked")
      List<String> sessionRoles = (List<String>) session.getAttribute("AUTH_ROLES");
      if (sessionRoles != null) roles = sessionRoles;
    }
    var body = Echo200ResponseDto.builder().sub(subject).roles(roles).build();
    return ResponseEntity.ok()
      .header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
      .header("Pragma", "no-cache")
      .contentType(MediaType.APPLICATION_JSON)
      .body(body);
  }
}
