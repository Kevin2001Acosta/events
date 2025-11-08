package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

}
