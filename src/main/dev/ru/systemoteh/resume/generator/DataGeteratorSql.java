package ru.systemoteh.resume.generator;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.*;
import java.util.*;

/**
 * Please add postgresql JDBC driver to classpath before launch this generator
 */
public class DataGeteratorSql {

    // JDBC setting for database
    private static final String JDBC_URL = "jdbc:postgresql://localhost/resume";
    private static final String JDBC_USERNAME = "resume";
    private static final String JDBC_PASSWORD = "password";

    private static final String PHOTO_PATH = "external/generate-data/photo/";
    private static final String CERTIFICATE_PATH = "external/generate-data/certificate/";
    private static final String MEDIA_DIR = "/home/systemoteh/java/resume/src/main/webapp/media";
    private static final String COUTRY = "Russia";
    private static final String[] CITIES = {"Moscow", "Saint-Petersburg", "Rostov-on-Don"};
    private static final String[] FOREGIN_LANGUAGES = {"Spanish", "French", "German", "Italian"};
    private static final String PASSWORD_HASH = "$2a$10$q7732w6Rj3kZGhfDYSIXI.wFp.uwTSi2inB2rYHvm1iDIAf1J1eVq";
    private static final String[] HOBBIES = {"Cycling", "Handball", "Football", "Basketball", "Bowling", "Boxing", "Volleyball", "Baseball", "Skating", "Skiing", "Table tennis", "Tennis",
            "Weightlifting", "Automobiles", "Book reading", "Cricket", "Photo", "Shopping", "Cooking", "Codding", "Animals", "Traveling", "Movie", "Painting", "Darts", "Fishing", "Kayak slalom",
            "Kite", "Ice hockey", "Roller skating", "Swimming", "Diving", "Golf", "Shooting", "Rowing", "Camping", "Archery", "Pubs", "Music", "Computer games", "Authorship", "Singing",
            "Foreign lang", "Billiards", "Skateboarding", "Collecting", "Badminton", "Disco"};
    // Sentences for content generation
    private static final String DUMMY_CONTENT_TEXT = "Sea esse deserunt ei, no diam ubique euripidis has. Nec labore cetero theophrastus no, ei vero facer veritus nec. Solum vituperata definitiones te vis, vis alia falli doming ea. An nam debet instructior, commodo mediocrem id cum. Mandamus abhorreant deseruisse mea at, mea elit deserunt persequeris at, in putant fuisset honestatis qui. Magna copiosae apeirian ius at. Ius dicat feugiat no, vix cu modo dicat principes. Per cu iracundia splendide. Sea esse deserunt ei, no diam ubique euripidis has. Nec labore cetero theophrastus no, ei vero facer veritus nec. Eam id posse dictas voluptua, veniam laoreet oportere no mea, quis regione suscipiantur mea an. Per cu iracundia splendide. Vel in dicant cetero phaedrum, usu populo interesset cu, eum ea facer nostrum pericula. Oratio accumsan et mea. Elitr accommodare deterruisset eam te, vim munere pertinax consetetur at. Mandamus abhorreant deseruisse mea at, mea elit deserunt persequeris at, in putant fuisset honestatis qui.";
    private static final List<String> SENTENCES;
    private static final Random r = new Random();
    private static final Map<String, Long> skillCategoryMap = createSkillCategoryMap();
    private static List<String> languageTypes = new ArrayList<>(Arrays.asList("ALL", "SPOKEN", "WRITING"));
    private static List<String> languageLevels = new ArrayList<>(Arrays.asList("BEGINNER", "ELEMENTARY", "PRE_INTERMEDIATE", "INTERMEDIATE", "UPPER_INTERMEDIATE", "ADVANCED", "PROFICIENCY"));
    private static int idProfile = 0;
    private static java.sql.Timestamp birthDay = null;

    static {
        String[] sentences = DUMMY_CONTENT_TEXT.split("\\.");
        List<String> sentencesList = new ArrayList<>(sentences.length);
        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (sentence.length() > 0) {
                sentencesList.add(sentence + ".");
            }
        }
        SENTENCES = Collections.unmodifiableList(sentencesList);
    }

    public static void main(String[] args) throws Exception {
        clearMedia();
        List<Certificate> certificates = loadCertificates();
        List<Profile> profiles = loadProfiles();
        List<ProfileConfig> profileConfigs = getProfileConfigs();
        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD)) {
            c.setAutoCommit(false);
            clearDb(c);
            insertSkillCategories(c);
            for (Profile p : profiles) {
                ProfileConfig profileConfig = profileConfigs.get(r.nextInt(profileConfigs.size()));
                createProfile(c, p, profileConfig, certificates);
                System.out.println("Created profile for " + p.firstName + " " + p.lastName);
            }
            c.commit();
            System.out.println("Data generated successful");
        }
    }

    private static void clearMedia() throws IOException {
        if (Files.exists(Paths.get(MEDIA_DIR))) {
            Files.walkFileTree(Paths.get(MEDIA_DIR), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        System.out.println("Media dir cleared");
    }

    private static void clearDb(Connection c) throws SQLException {
        Statement st = c.createStatement();
        st.executeUpdate("delete from profile");
        st.executeUpdate("delete from skill_category");
        st.executeQuery("select setval('profile_seq', 1, false)");
        st.executeQuery("select setval('language_seq', 1, false)");
        st.executeQuery("select setval('skill_category_seq', 1, false)");
        st.executeQuery("select setval('hobby_seq', 1, false)");
        st.executeQuery("select setval('skill_seq', 1, false)");
        st.executeQuery("select setval('practice_seq', 1, false)");
        st.executeQuery("select setval('course_seq', 1, false)");
        st.executeQuery("select setval('certificate_seq', 1, false)");
        st.executeQuery("select setval('education_seq', 1, false)");
        st.executeQuery("select setval('profile_restore_seq', 1, false)");
        System.out.println("Db cleared");
    }

    private static List<Profile> loadProfiles() {
        File[] photos = new File(PHOTO_PATH).listFiles();
        List<Profile> list = new ArrayList<>(photos.length);
        for (File f : photos) {
            String[] names = f.getName().replace(".jpg", "").split(" ");
            list.add(new Profile(names[0], names[1], f.getAbsolutePath()));
        }
        Collections.sort(list, new Comparator<Profile>() {
            @Override
            public int compare(Profile o1, Profile o2) {
                int firstNameCompare = o1.firstName.compareTo(o2.firstName);
                if (firstNameCompare != 0) {
                    return firstNameCompare;
                } else {
                    return o1.lastName.compareTo(o2.lastName);
                }
            }
        });
        return list;
    }

    private static List<Certificate> loadCertificates() {
        File[] files = new File(CERTIFICATE_PATH).listFiles();
        List<Certificate> list = new ArrayList<>(files.length);
        for (File f : files) {
            String name = f.getName().replace("-", " ");
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            list.add(new Certificate(name, f.getAbsolutePath()));
        }
        return list;
    }

    private static void insertSkillCategories(Connection c) throws SQLException {
        Map<String, Set<String>> categories = createSkillMap();
        PreparedStatement ps = c.prepareStatement("insert into skill_category values (nextval('skill_category_seq'),?)");
        for (String category : categories.keySet()) {
            ps.setString(1, category);
            ps.addBatch();
        }

        ps.executeBatch();
        ps.close();
    }

    private static void createProfile(Connection c, Profile profile, ProfileConfig profileConfig, List<Certificate> certificates) throws SQLException, IOException {
        insertProfileData(c, profile, profileConfig);
        insertLanguages(c);
        insertHobbies(c);
        insertSkills(c, profileConfig);
        insertPractices(c, profileConfig);
        if (profileConfig.certificates > 0) {
            insertCertificates(c, profileConfig.certificates, certificates);
        }
        insertCourses(c);
        insertEducation(c);
    }

    private static void insertProfileData(Connection c, Profile profile, ProfileConfig profileConfig) throws SQLException, IOException {
        PreparedStatement ps = c.prepareStatement("insert into profile values (nextval('profile_seq'),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,true,?,?,?,?,?,?)");
        ps.setString(1, (profile.firstName + "-" + profile.lastName).toLowerCase());
        ps.setString(2, profile.firstName);
        ps.setString(3, profile.lastName);
        ps.setString(4, PASSWORD_HASH);
        ps.setString(5, (profile.firstName + "-" + profile.lastName).toLowerCase() + "@gmail.com");
        ps.setString(6, generatePhone());
        birthDay = randomBirthDate();
        ps.setTimestamp(7, birthDay);
        ps.setString(8, COUTRY);
        ps.setString(9, CITIES[r.nextInt(CITIES.length)]);
        ps.setString(10, profileConfig.objective);
        ps.setString(11, profileConfig.summary);
        // large photo
        String uid = UUID.randomUUID().toString() + ".jpg";
        File photo = new File(MEDIA_DIR + "/avatar/" + uid);
        if (!photo.getParentFile().exists()) {
            photo.getParentFile().mkdirs();
        }
        Files.copy(Paths.get(profile.photo), Paths.get(photo.getAbsolutePath()));
        Thumbnails.of(photo).size(400, 400).toFile(photo);
        ps.setString(12, "/media/avatar/" + uid);
        // small photo
        String smallUid = uid.replace(".jpg", "-sm.jpg");
        Thumbnails.of(photo).size(110, 110).toFile(new File(MEDIA_DIR + "/avatar/" + smallUid));
        ps.setString(13, "/media/avatar/" + smallUid);

        if (r.nextBoolean()) {
            ps.setString(14, getInfo());
        } else {
            ps.setNull(14, Types.VARCHAR);
        }
        ps.setTimestamp(15, new Timestamp(System.currentTimeMillis()));
        if (r.nextBoolean()) {
            ps.setString(16, (profile.firstName + "-" + profile.lastName).toLowerCase());
        } else {
            ps.setNull(16, Types.VARCHAR);
        }
        if (r.nextBoolean()) {
            ps.setString(17, "https://vk.com/" + (profile.firstName + "-" + profile.lastName).toLowerCase());
        } else {
            ps.setNull(17, Types.VARCHAR);
        }
        if (r.nextBoolean()) {
            ps.setString(18, "https://facebook.com/" + (profile.firstName + "-" + profile.lastName).toLowerCase());
        } else {
            ps.setNull(18, Types.VARCHAR);
        }
        if (r.nextBoolean()) {
            ps.setString(19, "https://linkedin.com/" + (profile.firstName + "-" + profile.lastName).toLowerCase());
        } else {
            ps.setNull(19, Types.VARCHAR);
        }
        if (r.nextBoolean()) {
            ps.setString(20, "https://github.com/" + (profile.firstName + "-" + profile.lastName).toLowerCase());
        } else {
            ps.setNull(20, Types.VARCHAR);
        }
        if (r.nextBoolean()) {
            ps.setString(21, "https://stackoverflow.com/" + (profile.firstName + "-" + profile.lastName).toLowerCase());
        } else {
            ps.setNull(21, Types.VARCHAR);
        }

        ps.executeUpdate();
        ps.close();
        idProfile++;
    }

    private static void insertLanguages(Connection c) throws SQLException {
        List<String> languages = new ArrayList<>();
        languages.add("English");
        if (r.nextBoolean()) {
            int cnt = r.nextInt(1) + 1;
            List<String> otherLng = new ArrayList<>(Arrays.asList(FOREGIN_LANGUAGES));
            Collections.shuffle(otherLng);
            for (int i = 0; i < cnt; i++) {
                languages.add(otherLng.remove(0));
            }
        }
        PreparedStatement ps = c.prepareStatement("insert into language values (nextval('language_seq'),?,?,?,?)");
        for (String language : languages) {
            String langType = languageTypes.get(r.nextInt(languageTypes.size()));
            String langLevel = languageLevels.get(r.nextInt(languageLevels.size()));
            ps.setLong(1, idProfile);
            ps.setString(2, language);
            ps.setString(3, langLevel);
            ps.setString(4, langType);
            ps.addBatch();
        }
        ps.executeBatch();
        ps.close();
    }


    private static void insertHobbies(Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement("insert into hobby values (nextval('hobby_seq'),?,?)");
        List<String> hobbies = new ArrayList<>(Arrays.asList(HOBBIES));
        Collections.shuffle(hobbies);
        for (int i = 0; i < 5; i++) {
            ps.setLong(1, idProfile);
            ps.setString(2, hobbies.remove(0));
            ps.addBatch();
        }
        ps.executeBatch();
        ps.close();
    }

    private static void insertSkills(Connection c, ProfileConfig profileConfig) throws SQLException {
        PreparedStatement ps = c.prepareStatement("insert into skill values (nextval('skill_seq'),?,?,?)");
        Map<String, Set<String>> skills = createSkillMap();
        for (Course course : profileConfig.courses) {
            for (String key : skills.keySet()) {
                skills.get(key).addAll(course.skills.get(key));
            }
        }
        for (Map.Entry<String, Set<String>> entry : skills.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                ps.setLong(1, idProfile);
                ps.setLong(2, skillCategoryMap.get(entry.getKey()));
                String skillValue = entry.getValue().toString();
                ps.setString(3, skillValue.substring(1, skillValue.length() - 1));
                ps.addBatch();
            }
        }
        ps.executeBatch();
        ps.close();
    }

    private static void insertPractices(Connection c, ProfileConfig profileConfig) throws SQLException {
        PreparedStatement ps = c.prepareStatement("insert into practice values (nextval('practice_seq'),?,?,?,?,?,?,?,?)");
        boolean currentCourse = r.nextBoolean();
        java.sql.Timestamp finish = addField(new java.sql.Timestamp(System.currentTimeMillis()), Calendar.MONTH, -(r.nextInt(3) + 1), false);
        for (Course course : profileConfig.courses) {
            ps.setLong(1, idProfile);
            ps.setString(2, course.name);
            ps.setString(3, course.company);
            if (currentCourse) {
                ps.setTimestamp(4, addField(new java.sql.Timestamp(System.currentTimeMillis()), Calendar.MONTH, -1, false));
                ps.setNull(5, Types.DATE);
            } else {
                ps.setTimestamp(4, addField(finish, Calendar.MONTH, -1, false));
                ps.setTimestamp(5, finish);
                finish = addField(finish, Calendar.MONTH, -(r.nextInt(3) + 1), false);
            }
            ps.setString(6, course.responsibilities);
            if (course.demo == null) {
                ps.setNull(7, Types.VARCHAR);
            } else {
                ps.setString(7, course.demo);
            }
            if (course.github == null) {
                ps.setNull(8, Types.VARCHAR);
            } else {
                ps.setString(8, course.github);
            }
            ps.addBatch();
        }
        ps.executeBatch();
        ps.close();
    }

    private static void insertCertificates(Connection c, int certificatesCount, List<Certificate> certificates) throws SQLException, IOException {
        Collections.shuffle(certificates);
        PreparedStatement ps = c.prepareStatement("insert into certificate values (nextval('certificate_seq'),?,?,?,?)");
        for (int i = 0; i < certificatesCount && i < certificates.size(); i++) {
            Certificate certificate = certificates.get(i);
            ps.setLong(1, idProfile);
            ps.setString(2, certificate.name);
            String uid = UUID.randomUUID().toString() + ".jpg";
            File photo = new File(MEDIA_DIR + "/certificate/" + uid);
            if (!photo.getParentFile().exists()) {
                photo.getParentFile().mkdirs();
            }
            String smallUid = uid.replace(".jpg", "-sm.jpg");
            Files.copy(Paths.get(certificate.largeImg), Paths.get(photo.getAbsolutePath()));
            ps.setString(3, "/media/certificate/" + uid);
            Thumbnails.of(photo).size(100, 100).toFile(Paths.get(photo.getAbsolutePath().replace(".jpg", "-sm.jpg")).toFile());
            ps.setString(4, "/media/certificate/" + smallUid);
            ps.addBatch();
        }

        ps.executeBatch();
        ps.close();
    }

    private static void insertCourses(Connection c) throws SQLException {
        if (r.nextBoolean()) {
            PreparedStatement ps = c.prepareStatement("insert into course values (nextval('course_seq'),?,?,?,?)");
            ps.setLong(1, idProfile);
            ps.setString(2, "Java Advanced Course");
            ps.setString(3, "SourceIt");
            java.sql.Timestamp finish = randomFinishEducation();
            if (finish.getTime() > System.currentTimeMillis()) {
                ps.setNull(4, Types.DATE);
            } else {
                ps.setTimestamp(4, finish);
            }

            ps.executeUpdate();
            ps.close();
        }
    }

    private static void insertEducation(Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement("insert into education values (nextval('education_seq'),?,?,?,?,?,?)");
        ps.setLong(1, idProfile);
        ps.setString(2, "The specialist degree in Electronic Engineering");
        java.sql.Timestamp finish = randomFinishEducation();
        java.sql.Timestamp begin = addField(finish, Calendar.YEAR, -5, true);
        ps.setInt(3, new DateTime(begin).getYear());
        if (finish.getTime() > System.currentTimeMillis()) {
            ps.setNull(4, Types.INTEGER);
        } else {
            ps.setInt(4, new DateTime(finish).getYear());
        }
        ps.setString(5, "Southern Federal University, Russia");
        ps.setString(6, "Computer Science");

        ps.executeUpdate();
        ps.close();
    }

    private static java.sql.Timestamp addField(java.sql.Timestamp finish, int field, int value, boolean isBeginEducation) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(finish.getTime());
        cl.add(field, value);
        if (isBeginEducation) {
            cl.set(Calendar.DAY_OF_MONTH, 1);
            cl.set(Calendar.MONTH, Calendar.SEPTEMBER);
        }
        return new java.sql.Timestamp(cl.getTimeInMillis());
    }

    private static java.sql.Timestamp randomFinishEducation() {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(birthDay.getTime());
        cl.set(Calendar.DAY_OF_MONTH, 30);
        cl.set(Calendar.MONTH, Calendar.JUNE);
        int year = cl.get(Calendar.YEAR) + 21;
        cl.set(Calendar.YEAR, year + r.nextInt(3));
        return new java.sql.Timestamp(cl.getTimeInMillis());
    }

    private static String getInfo() {
        int endIndex = r.nextInt(SENTENCES.size());
        if (endIndex > 0) {
            int startIndex = r.nextInt(endIndex);
            if (endIndex - startIndex > 4) {
                endIndex = startIndex + 3;
            }
            return StringUtils.join(SENTENCES.subList(startIndex, endIndex), " ");
        }
        return "Java developer. Married. Have a dother";
    }

    private static String generatePhone() {
        StringBuilder phone = new StringBuilder("+7495");
        for (int i = 0; i < 7; i++) {
            int code = '1' + r.nextInt(9);
            phone.append(((char) code));
        }
        return phone.toString();
    }

    private static java.sql.Timestamp randomBirthDate() {
        Calendar cl = Calendar.getInstance();
        cl.set(Calendar.DAY_OF_MONTH, r.nextInt(30));
        cl.set(Calendar.MONTH, r.nextInt(12));
        int year = cl.get(Calendar.YEAR) - 30;
        cl.set(Calendar.YEAR, year + r.nextInt(10));
        return new Timestamp(cl.getTimeInMillis());
    }

    private static List<ProfileConfig> getProfileConfigs() {
        List<ProfileConfig> res = new ArrayList<>();
        res.add(new ProfileConfig("Junior java trainee position", "Java core course with developing one simple console application", new Course[]{Course.createCoreCourse()}, 0));
        res.add(new ProfileConfig("Junior java trainee position", "One Java professional course with developing web application blog (Link to demo is provided)",
                new Course[]{Course.createBaseCourse()}, 0));
        res.add(new ProfileConfig("Junior java developer position", "One Java professional course with developing web application portal (Link to demo is provided)",
                new Course[]{Course.createAdvancedCourse()}, 0));
        res.add(new ProfileConfig("Junior java developer position", "One Java professional course with developing web application portal (Link to demo is provided)",
                new Course[]{Course.createAdvancedCourse()}, 1));
        res.add(new ProfileConfig("Junior java developer position", "Two Java professional profileCourses with developing two web applications: blog and portal (Links to demo are provided)",
                new Course[]{Course.createAdvancedCourse(), Course.createBaseCourse()}, 1));
        res.add(new ProfileConfig("Junior java developer position", "Two Java professional profileCourses with developing two web applications: blog and portal (Links to demo are provided)",
                new Course[]{Course.createAdvancedCourse(), Course.createBaseCourse()}, 1));
        res.add(new ProfileConfig("Junior java developer position", "Two Java professional profileCourses with developing two web applications: blog and portal (Links to demo are provided)",
                new Course[]{Course.createAdvancedCourse(), Course.createBaseCourse()}, 1));
        res.add(new ProfileConfig("Junior java developer position", "Two Java professional profileCourses with developing two web applications: blog and portal (Links to demo are provided)",
                new Course[]{Course.createAdvancedCourse(), Course.createBaseCourse()}, 2));
        res.add(new ProfileConfig("Junior java developer position",
                "Three Java professional profileCourses with developing one console application and two web applications: blog and portal (Links to demo are provided)",
                new Course[]{Course.createAdvancedCourse(), Course.createBaseCourse(), Course.createCoreCourse()}, 2));
        return res;
    }

    private static Map<String, Set<String>> createSkillMap() {
        Map<String, Set<String>> skills = new LinkedHashMap<>();
        skills.put("Languages", new LinkedHashSet<String>());
        skills.put("DBMS", new LinkedHashSet<String>());
        skills.put("Web", new LinkedHashSet<String>());
        skills.put("Java", new LinkedHashSet<String>());
        skills.put("IDE", new LinkedHashSet<String>());
        skills.put("CVS", new LinkedHashSet<String>());
        skills.put("Web Servers", new LinkedHashSet<String>());
        skills.put("Build system", new LinkedHashSet<String>());
        skills.put("Cloud", new LinkedHashSet<String>());
        return skills;
    }

    private static Map<String, Long> createSkillCategoryMap() {
        Map<String, Long> skillCategories = new LinkedHashMap<>();
        skillCategories.put("Languages", 1l);
        skillCategories.put("DBMS", 2l);
        skillCategories.put("Web", 3l);
        skillCategories.put("Java", 4l);
        skillCategories.put("IDE", 5l);
        skillCategories.put("CVS", 6l);
        skillCategories.put("Web Servers", 7l);
        skillCategories.put("Build system", 8l);
        skillCategories.put("Cloud", 9l);
        return skillCategories;
    }

    private static final class ProfileConfig {
        private final String objective;
        private final String summary;
        private final Course[] courses;
        private final int certificates;

        private ProfileConfig(String objective, String summary, Course[] courses, int certificates) {
            super();
            this.objective = objective;
            this.summary = summary;
            this.courses = courses;
            this.certificates = certificates;
        }
    }

    private static final class Profile {
        private final String firstName;
        private final String lastName;
        private final String photo;

        private Profile(String firstName, String lastName, String photo) {
            super();
            this.firstName = firstName;
            this.lastName = lastName;
            this.photo = photo;
        }

        @Override
        public String toString() {
            return String.format("Profile [firstName=%s, lastName=%s]", firstName, lastName);
        }
    }

    private static final class Certificate {
        private final String name;
        private final String largeImg;

        private Certificate(String name, String largeImg) {
            super();
            this.name = name;
            this.largeImg = largeImg;
        }
    }

    private static final class Course {
        private final String name;
        private final String company;
        private final String github;
        private final String responsibilities;
        private final String demo;
        private final Map<String, Set<String>> skills;

        private Course(String name, String company, String github, String responsibilities, String demo, Map<String, Set<String>> skills) {
            super();
            this.name = name;
            this.company = company;
            this.github = github;
            this.responsibilities = responsibilities;
            this.demo = demo;
            this.skills = skills;
        }

        static Course createCoreCourse() {
            Map<String, Set<String>> skills = createSkillMap();
            skills.get("Languages").add("Java");
            skills.get("DBMS").add("Mysql");
            skills.get("Java").add("Threads");
            skills.get("Java").add("IO");
            skills.get("Java").add("JAXB");
            skills.get("Java").add("GSON");
            skills.get("IDE").add("Eclipse for JEE Developer");
            skills.get("CVS").add("Git");
            skills.get("CVS").add("Github");
            skills.get("Build system").add("Maven");

            return new Course("Java Core Course", "Devstudy.net", null, "Developing the java console application which imports XML, JSON, Properties, CVS to Db via JDBC", null, skills);
        }

        static Course createBaseCourse() {
            Map<String, Set<String>> skills = createSkillMap();
            skills.get("Languages").add("Java");
            skills.get("Languages").add("SQL");
            skills.get("DBMS").add("Postgresql");
            skills.get("Web").add("HTML");
            skills.get("Web").add("CSS");
            skills.get("Web").add("JS");
            skills.get("Web").add("JS");
            skills.get("Web").add("Foundation");
            skills.get("Web").add("JQuery");
            skills.get("Java").add("Servlets");
            skills.get("Java").add("Logback");
            skills.get("Java").add("JSP");
            skills.get("Java").add("JSTL");
            skills.get("Java").add("JDBC");
            skills.get("Java").add("Apache Commons");
            skills.get("Java").add("Google+ Social API");
            skills.get("IDE").add("Eclipse for JEE Developer");
            skills.get("CVS").add("Git");
            skills.get("CVS").add("Github");
            skills.get("Web Servers").add("Tomcat");
            skills.get("Build system").add("Maven");
            skills.get("Cloud").add("OpenShift");

            return new Course("Java Base Course", "Devstudy.net", "https://github.com/TODO",
                    "Developing the web application 'blog' using free HTML template, downloaded from intenet. Populating database by test data and uploading web project to OpenShift free hosting",
                    "http://LINK_TO_DEMO_SITE", skills);
        }

        static Course createAdvancedCourse() {
            Map<String, Set<String>> skills = createSkillMap();
            skills.get("Languages").add("Java");
            skills.get("Languages").add("SQL");
            skills.get("Languages").add("PLSQL");
            skills.get("DBMS").add("Postgresql");
            skills.get("Web").add("HTML");
            skills.get("Web").add("CSS");
            skills.get("Web").add("JS");
            skills.get("Web").add("JS");
            skills.get("Web").add("Bootstrap");
            skills.get("Web").add("JQuery");
            skills.get("Java").add("Spring MVC");
            skills.get("Java").add("Logback");
            skills.get("Java").add("JSP");
            skills.get("Java").add("JSTL");
            skills.get("Java").add("Spring Data JPA");
            skills.get("Java").add("Apache Commons");
            skills.get("Java").add("Spring Security");
            skills.get("Java").add("Hibernate JPA");
            skills.get("Java").add("Facebook Social API");
            skills.get("IDE").add("Eclipse for JEE Developer");
            skills.get("CVS").add("Git");
            skills.get("CVS").add("Github");
            skills.get("Web Servers").add("Tomcat");
            skills.get("Web Servers").add("Nginx");
            skills.get("Build system").add("Maven");
            skills.get("Cloud").add("AWS");

            return new Course("Java Advanced Course", "Devstudy.net", "https://github.com/TODO",
                    "Developing the web application 'online-portal' using bootstrap HTML template, downloaded from internet. Populating database by test data and uploading web project to AWS EC2 instance",
                    "http://LINK_TO_DEMO_SITE", skills);
        }
    }
}