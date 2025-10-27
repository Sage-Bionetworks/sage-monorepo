package org.sagebionetworks.bixarena.api.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.MessageCreateDto;
import org.sagebionetworks.bixarena.api.model.entity.MessageEntity;
import org.sagebionetworks.bixarena.api.model.mapper.MessageMapper;
import org.sagebionetworks.bixarena.api.model.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

  private final MessageRepository messageRepository;
  private final MessageMapper messageMapper = new MessageMapper();

  @Transactional
  public UUID createMessage(MessageCreateDto payload) {
    if (payload == null) return null;

    MessageEntity entity = messageMapper.convertCreateToEntity(payload);
    MessageEntity saved = messageRepository.save(entity);
    messageRepository.flush();
    log.info("Created message {}", saved.getId());
    return saved.getId();
  }
}
