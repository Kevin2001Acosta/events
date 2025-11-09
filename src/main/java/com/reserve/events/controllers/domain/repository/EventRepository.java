package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    boolean existsById(String id);
    void deleteById(String id);
}
