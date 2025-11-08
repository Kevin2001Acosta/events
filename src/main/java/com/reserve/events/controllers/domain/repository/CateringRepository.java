package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.Catering;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CateringRepository extends MongoRepository<Catering, String> {
}
