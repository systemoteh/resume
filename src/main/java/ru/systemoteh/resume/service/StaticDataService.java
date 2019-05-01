package ru.systemoteh.resume.service;

import ru.systemoteh.resume.domain.Hobby;
import ru.systemoteh.resume.model.LanguageLevel;
import ru.systemoteh.resume.model.LanguageType;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StaticDataService {

    @Nonnull
    Map<Integer, String> findMonthMap();

    @Nonnull
    List<Integer> findPracticesYears();

    @Nonnull
    List<Integer> findCoursesYears();

    @Nonnull
    List<Integer> findEducationYears();

    @Nonnull
    Collection<LanguageType> findAllLanguageTypes();

    @Nonnull
    Collection<LanguageLevel> findAllLanguageLevels();

    @Nonnull
    Collection<Hobby> createHobbyEntitiesByNames(@Nonnull List<String> names);

    @Nonnull
    Set<Hobby> findAllHobbies();
}
