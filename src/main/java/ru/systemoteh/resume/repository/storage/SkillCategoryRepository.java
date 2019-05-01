package ru.systemoteh.resume.repository.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.systemoteh.resume.domain.SkillCategory;

import java.util.List;

public interface SkillCategoryRepository extends JpaRepository<SkillCategory, Long> {

    @Query("select new SkillCategory (sc.id, sc.name) from SkillCategory sc")
    List<SkillCategory> findAll(Sort sort);
}
