package com.example.demo.elasticsearch.service;

import com.example.demo.elasticsearch.model.UserDocument;
import com.example.demo.elasticsearch.repository.UserSearchRepository;
import com.example.demo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSearchService {

    private final UserSearchRepository userSearchRepository;
    private final UserMapper userMapper;

    public void saveUser(UserDocument userDocument) {
        userSearchRepository.save(userDocument);
    }

    public void deleteUser(Long id) {
        userSearchRepository.deleteById(id);
    }

    public List<UserDocument> searchUsersByName(String name) {
        return userSearchRepository.findByNameContainingIgnoreCase(name);
    }

    public List<UserDocument> searchUsersByAge(int age, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userSearchRepository.findByAge(age, pageable).getContent();
    }
}
