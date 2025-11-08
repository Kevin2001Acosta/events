package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.Establishment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EstablishmentRepository extends MongoRepository<Establishment, String> {
}

