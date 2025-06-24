package com.example.demo.mapper;

import com.example.demo.contract.UserDTO;
import com.example.demo.model.User;
import com.example.demo.elasticsearch.model.UserDocument;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // Converts JPA Entity to DTO
    public UserDTO toDTO(User user) {
        if (user == null) return null;
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .build();
    }

    // Converts DTO to JPA Entity
    public User toEntity(UserDTO dto) {
        if (dto == null) return null;
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .age(dto.getAge())
                .build();
    }

    // Converts JPA Entity to Elasticsearch Document
    public UserDocument toDocument(User user) {
        if (user == null) return null;
        return UserDocument.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .build();
    }

    // Converts Elasticsearch Document to DTO
    public UserDTO toDTO(UserDocument doc) {
        if (doc == null) return null;
        return UserDTO.builder()
                .id(doc.getId())
                .name(doc.getName())
                .email(doc.getEmail())
                .age(doc.getAge())
                .build();
    }
}
