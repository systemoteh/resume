package ru.systemoteh.resume.repository.storage;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;

@NoRepositoryBean
public interface AbstractProfileEntityRepository<T> extends Repository<T, Long> {

    void deleteByProfileId(Long idProfile);

    List<T> findByProfileIdOrderByIdAsc(Long idProfile);

    <S extends T> S saveAndFlush(S entity);

    void flush();
}
