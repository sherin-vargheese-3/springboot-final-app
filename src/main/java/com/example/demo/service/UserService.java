package com.example.demo.service;

import com.example.demo.contract.UserDTO;
import com.example.demo.elasticsearch.service.UserSearchService;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, UserDTO> userRedisTemplate;
    private final MessageService messageService;

    private final UserSearchService userSearchService; //  Elasticsearch Integration
    private final UserMapper userMapper;               //  Using UserMapper

    public UserDTO getUserById(Long id) {
        String key = "users::" + id;
        ValueOperations<String, UserDTO> valueOps = userRedisTemplate.opsForValue();

        UserDTO cached = valueOps.get(key);
        if (cached != null) {
            return cached;
        }

        try {
            Thread.sleep(3000); // Simulated delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        UserDTO dto = userMapper.toDTO(user); // Using mapper
        valueOps.set(key, dto, 30, TimeUnit.MINUTES);
        return dto;
    }

    @Cacheable(value = "allUsers")
    public List<UserDTO> getAllUsers() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO) // Using mapper
                .collect(Collectors.toList());
    }

    @CacheEvict(value = {"users", "allUsers"}, allEntries = true)
    public UserDTO createUser(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO); // Using mapper
        User saved = userRepository.save(user);

        // Index to Elasticsearch
        userSearchService.saveUser(userMapper.toDocument(saved));

        return userMapper.toDTO(saved); // Using mapper
    }

    @CacheEvict(value = {"users", "allUsers"}, allEntries = true)
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        User updated = User.builder()
                .id(existingUser.getId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .age(userDTO.getAge())
                .build();

        userRepository.save(updated);

        messageService.processUserMessage("User '" + updated.getName() + "' updated.");

        // Update in Elasticsearch
        userSearchService.saveUser(userMapper.toDocument(updated));

        return userMapper.toDTO(updated); // Using mapper
    }

    @CacheEvict(value = {"users", "allUsers"}, allEntries = true)
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);

            // Delete from Elasticsearch
            userSearchService.deleteUser(id);
        } else {
            throw new UserNotFoundException(id);
        }
    }

    // Search in Elasticsearch
    public List<UserDTO> searchUsers(String keyword) {
        return userSearchService.searchUsersByName(keyword)
                .stream()
                .map(userMapper::toDTO) //  Using mapper
                .collect(Collectors.toList());
    }

    public List<UserDTO> searchUsersByAge(int age, int page, int size) {
        return userSearchService.searchUsersByAge(age, page, size)
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
}
