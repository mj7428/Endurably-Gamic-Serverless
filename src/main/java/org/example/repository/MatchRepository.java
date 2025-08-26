//package org.example.repository;
//
//import org.example.Entity.Match;
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface MatchRepository extends MongoRepository<Match, String> {
//
//    // This method signature is updated to use a String ID for MongoDB
//    List<Match> findByTournamentIdOrderByRoundNumberAscMatchNumberAsc(String tournamentId);
//
//}
