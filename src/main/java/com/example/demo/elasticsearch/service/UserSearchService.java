package com.example.demo.elasticsearch.service;

import com.example.demo.elasticsearch.model.UserDocument;
import com.example.demo.elasticsearch.repository.UserSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSearchService {

    private final UserSearchRepository userSearchRepository;

    public void saveUser(UserDocument userDocument) {
        userSearchRepository.save(userDocument);
    }

    public void deleteUser(Long id) {
        userSearchRepository.deleteById(id);
    }

    public List<UserDocument> searchUsersByName(String name) {
        return userSearchRepository.findByNameContainingIgnoreCase(name);
    }
}
