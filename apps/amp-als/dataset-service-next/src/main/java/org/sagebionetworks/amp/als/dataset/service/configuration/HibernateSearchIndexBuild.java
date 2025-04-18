package org.sagebionetworks.amp.als.dataset.service.configuration;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class HibernateSearchIndexBuild implements ApplicationListener<ApplicationReadyEvent> {

  private final EntityManager entityManager;

  public HibernateSearchIndexBuild(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  @Transactional
  public void onApplicationEvent(ApplicationReadyEvent event) {
    log.info("Started Initializing Indexes");

    SearchSession searchSession = Search.session(entityManager);
    MassIndexer indexer = searchSession
      .massIndexer()
      .idFetchSize(150)
      .batchSizeToLoadObjects(25)
      .threadsToLoadObjects(12);

    try {
      indexer.startAndWait();
    } catch (InterruptedException e) {
      log.warn("Failed to load data from database", e);
      Thread.currentThread().interrupt();
    }

    log.info("Completed Indexing");
  }
}
