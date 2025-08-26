package org.example.repository;

import org.example.Entity.TeamRegistration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeamRegistrationRepository extends MongoRepository<TeamRegistration, String> {

    List<TeamRegistration> findByTournamentIdAndSubmittedById(String tournamentId, String userId);

    List<TeamRegistration> findByTournamentId(String tournamentId);
}
