package com.example.demo.elasticsearch.repository;

import com.example.demo.elasticsearch.model.UserDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface UserSearchRepository extends ElasticsearchRepository<UserDocument, Long> {
    List<UserDocument> findByNameContainingIgnoreCase(String name);
}
