package org.example.repository;

import org.example.Entity.FieldValue;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FieldValueRepository extends MongoRepository<FieldValue, String> {
}
