package com.bol.mancala.infra.adapter.data.mongo.respository;

import com.bol.mancala.infra.adapter.data.mongo.document.MancalaGameDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MancalaMongoRepository extends ReactiveMongoRepository<MancalaGameDocument, String> {

}
