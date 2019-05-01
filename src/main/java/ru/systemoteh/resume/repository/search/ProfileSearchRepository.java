package ru.systemoteh.resume.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ru.systemoteh.resume.domain.Profile;

public interface ProfileSearchRepository extends ElasticsearchRepository<Profile, Long> {

    /**
     * http://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.query-methods.criterions
     */
    Page<Profile> findByObjectiveLikeOrSummaryLikeOrInfoLikeOrCertificatesNameLikeOrLanguagesNameLikeOrPracticesCompanyLikeOrPracticesPositionLikeOrPracticesResponsibilitiesLikeOrSkillsNameLikeOrCoursesNameLikeOrCoursesSchoolLike(
            String objective,
            String info,
            String summary,
            String certificateName,
            String languageName,
            String practiceCompany,
            String practicePosition,
            String practiceResponsibility,
            String skillName,
            String courseName,
            String courseSchool,
            Pageable pageable
    );
}
