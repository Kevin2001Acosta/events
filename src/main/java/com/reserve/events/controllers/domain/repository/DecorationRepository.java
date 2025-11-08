package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.Decoration;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DecorationRepository extends MongoRepository<Decoration, String> {
}
