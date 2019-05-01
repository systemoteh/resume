package ru.systemoteh.resume.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import ru.systemoteh.resume.domain.Profile;
import ru.systemoteh.resume.repository.search.ProfileSearchRepository;
import ru.systemoteh.resume.service.FindProfileService;

import javax.annotation.PostConstruct;

@Service
public class ElasticSearchIndexingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchIndexingService.class);

    @Value("${index.all.during.startup}")
    private boolean indexAllDuringStartup;

    @Autowired
    private ProfileSearchRepository profileSearchRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private FindProfileService findProfileService;

    @PostConstruct
    private void postConstruct() {
        if (indexAllDuringStartup) {
            LOGGER.info("Detected index all command");
            LOGGER.info("Clear old index");
            elasticsearchOperations.deleteIndex(Profile.class);
            LOGGER.info("Start index of profiles");
            for (Profile p : findProfileService.findAllForIndexing()) {
                profileSearchRepository.save(p);
                LOGGER.info("Successful indexing of profile: " + p.getUid());
            }
            LOGGER.info("Finish index of profiles");
        } else {
            LOGGER.info("indexAllDuringStartup is disabled");
        }
    }
}
