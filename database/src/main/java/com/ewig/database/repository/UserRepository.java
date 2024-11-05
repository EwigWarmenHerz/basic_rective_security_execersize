package com.ewig.database.repository;

import com.ewig.database.entity.MyUser;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<MyUser, Long> {
    @Query("SELECT * FROM user u WHERE u.email = :email")
    Mono<MyUser> findByEmail(@Param("email")String email);
}
