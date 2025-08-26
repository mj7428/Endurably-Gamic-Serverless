package org.example.repository;

import org.example.Entity.PlayerSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PlayerSubmissionRepository extends MongoRepository<PlayerSubmission, String> {
}
