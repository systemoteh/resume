package ru.systemoteh.resume.repository.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.systemoteh.resume.domain.Profile;

import java.sql.Timestamp;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Page<Profile> findAllByCompletedTrue(Pageable pageable);

    Profile findByUid(String uid);

    Profile findByEmail(String email);

    Profile findByPhone(String phone);

    Profile findByUidOrEmailOrPhone(String uid, String email, String phone);

    int countByUid(String uid);

    @Modifying
    @Query("delete from Profile p where p.completed = false and p.created < ?1")
    int deleteNotCompleted(Timestamp oldDate);
}
