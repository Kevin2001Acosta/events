package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.Entertainment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntertainmentRepository extends MongoRepository<Entertainment, String> {
}
