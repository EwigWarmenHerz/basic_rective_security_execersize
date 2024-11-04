package com.ewig.database.repository;

import com.ewig.database.entity.MyUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<MyUser, Long> {
    Mono<MyUser> findByEmailAndPassword(String email, String password);
}
