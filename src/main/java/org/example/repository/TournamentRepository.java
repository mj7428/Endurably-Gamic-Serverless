package org.example.repository;

import org.example.Entity.Tournament;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TournamentRepository extends MongoRepository<Tournament, String> {
}
