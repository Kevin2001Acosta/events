package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.Adittional;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface AdittionalRepository extends MongoRepository <Adittional, String> {
}
