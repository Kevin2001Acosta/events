package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<Event, String> {

}
