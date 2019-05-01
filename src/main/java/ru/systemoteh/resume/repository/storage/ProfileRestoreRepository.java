package ru.systemoteh.resume.repository.storage;

import org.springframework.data.repository.CrudRepository;
import ru.systemoteh.resume.domain.ProfileRestore;

public interface ProfileRestoreRepository extends CrudRepository<ProfileRestore, Long> {

    ProfileRestore findByToken(String token);

}
