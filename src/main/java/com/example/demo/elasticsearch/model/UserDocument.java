package com.example.demo.elasticsearch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDocument {
    @org.springframework.data.annotation.Id
    private Long id;

    private String name;
    private String email;
    private Integer age;
}
