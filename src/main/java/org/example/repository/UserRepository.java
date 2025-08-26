package org.example.repository;

import org.example.Entity.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<Users, String> { // Changed from JpaRepository<Users, Long>

    // This method signature works perfectly with MongoDB
    Optional<Users> findByEmail(String email);
}
