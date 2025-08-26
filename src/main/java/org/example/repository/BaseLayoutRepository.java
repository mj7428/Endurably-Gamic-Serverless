package org.example.repository;

import org.example.Entity.BaseLayout;
import org.example.Entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseLayoutRepository extends MongoRepository<BaseLayout, String> {

    Page<BaseLayout> findAllBySubmittedBy(Users user, Pageable pageable);

    Page<BaseLayout> findAllByStatusAndTownhallLevel(BaseLayout.BaseStatus status, Integer townhallLevel, Pageable pageable);

    Page<BaseLayout> findAllByStatus(BaseLayout.BaseStatus status, Pageable pageable);
}
