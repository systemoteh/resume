package ru.systemoteh.resume.service.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.systemoteh.resume.domain.Hobby;
import ru.systemoteh.resume.model.LanguageLevel;
import ru.systemoteh.resume.model.LanguageType;
import ru.systemoteh.resume.service.StaticDataService;

import javax.annotation.Nonnull;
import java.util.*;

@Service
public class StaticDataServiceImpl implements StaticDataService {

    @Value("${practice.years.ago}")
    private int practiceYearsAgo;

    @Value("${course.years.ago}")
    private int courseYearsAgo;

    @Value("${education.years.ago}")
    private int educationYearsAgo;

    private final Set<Hobby> allHobbies;

    private final Set<String> allHobbyNames;

    public StaticDataServiceImpl() {
        super();
        this.allHobbies    = Collections.unmodifiableSet(createAllHobbiesSet());
        this.allHobbyNames = Collections.unmodifiableSet(createAllHobbyNamesSet());
    }

    protected Set<Hobby> createAllHobbiesSet() {
        return new TreeSet<>(Arrays.asList(new Hobby[] { new HobbyReadOnlyEntity("Cycling"), new HobbyReadOnlyEntity("Handball"), new HobbyReadOnlyEntity("Football"), new HobbyReadOnlyEntity("Basketball"),
                new HobbyReadOnlyEntity("Bowling"), new HobbyReadOnlyEntity("Boxing"), new HobbyReadOnlyEntity("Volleyball"), new HobbyReadOnlyEntity("Baseball"), new HobbyReadOnlyEntity("Skating"),
                new HobbyReadOnlyEntity("Skiing"), new HobbyReadOnlyEntity("Table tennis"), new HobbyReadOnlyEntity("Tennis"), new HobbyReadOnlyEntity("Weightlifting"),
                new HobbyReadOnlyEntity("Automobiles"), new HobbyReadOnlyEntity("Book reading"), new HobbyReadOnlyEntity("Cricket"), new HobbyReadOnlyEntity("Photo"),
                new HobbyReadOnlyEntity("Shopping"), new HobbyReadOnlyEntity("Cooking"), new HobbyReadOnlyEntity("Codding"), new HobbyReadOnlyEntity("Animals"), new HobbyReadOnlyEntity("Traveling"),
                new HobbyReadOnlyEntity("Movie"), new HobbyReadOnlyEntity("Painting"), new HobbyReadOnlyEntity("Darts"), new HobbyReadOnlyEntity("Fishing"), new HobbyReadOnlyEntity("Kayak slalom"),
                new HobbyReadOnlyEntity("Kite"), new HobbyReadOnlyEntity("Ice hockey"), new HobbyReadOnlyEntity("Roller skating"), new HobbyReadOnlyEntity("Swimming"),
                new HobbyReadOnlyEntity("Diving"), new HobbyReadOnlyEntity("Golf"), new HobbyReadOnlyEntity("Shooting"), new HobbyReadOnlyEntity("Rowing"), new HobbyReadOnlyEntity("Camping"),
                new HobbyReadOnlyEntity("Archery"), new HobbyReadOnlyEntity("Pubs"), new HobbyReadOnlyEntity("Music"), new HobbyReadOnlyEntity("Computer games"), new HobbyReadOnlyEntity("Authorship"),
                new HobbyReadOnlyEntity("Singing"), new HobbyReadOnlyEntity("Foreign lang"), new HobbyReadOnlyEntity("Billiards"), new HobbyReadOnlyEntity("Skateboarding"),
                new HobbyReadOnlyEntity("Collecting"), new HobbyReadOnlyEntity("Badminton"), new HobbyReadOnlyEntity("Disco") }));
    }

    protected Set<String> createAllHobbyNamesSet() {
        Set<String> set = new HashSet<>();
        for (Hobby h : allHobbies) {
            set.add(h.getName());
        }
        return set;
    }



    @Nonnull
    @Override
    public Map<Integer, String> findMonthMap() {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        Map<Integer, String> map = new LinkedHashMap<>();
        for (int i = 0; i < months.length; i++) {
            map.put(i + 1, months[i]);
        }
        return map;
    }

    @Nonnull
    @Override
    public List<Integer> findPracticesYears() {
        return listYears(practiceYearsAgo);
    }

    protected List<Integer> listYears(int count) {
        List<Integer> years = new ArrayList<>();
        int now = DateTime.now().getYear();
        for (int i = 0; i < count; i++) {
            years.add(now - i);
        }
        return years;
    }

    @Nonnull
    @Override
    public List<Integer> findCoursesYears() {
        return listYears(courseYearsAgo);
    }

    @Nonnull
    @Override
    public List<Integer> findEducationYears() {
        return listYears(educationYearsAgo);
    }

    @Nonnull
    @Override
    public Collection<LanguageType> findAllLanguageTypes() {
        return EnumSet.allOf(LanguageType.class);
    }

    @Nonnull
    @Override
    public Collection<LanguageLevel> findAllLanguageLevels() {
        return EnumSet.allOf(LanguageLevel.class);
    }

    @Nonnull
    @Override
    public Collection<Hobby> createHobbyEntitiesByNames(@Nonnull List<String> names) {
        Collection<Hobby> result = new ArrayList<>(names.size());
        for (String name : names) {
            if (allHobbyNames.contains(name)) {
                result.add(new Hobby(name));
            }
        }
        return result;
    }

    @Nonnull
    @Override
    public Set<Hobby> findAllHobbies() {
        return allHobbies;
    }

    protected static final class HobbyReadOnlyEntity extends Hobby {

        protected HobbyReadOnlyEntity(String name) {
            super(name);
        }

        @Override
        public void setName(String name) {
            throw new UnsupportedOperationException("This hobby instance is readonly instance!");
        }

        @Override
        public void setSelected(boolean selected) {
            throw new UnsupportedOperationException("This hobby instance is readonly instance!");
        }
    }
}
