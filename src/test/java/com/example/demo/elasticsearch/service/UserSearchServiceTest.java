package com.example.demo.elasticsearch.service;

import com.example.demo.elasticsearch.model.UserDocument;
import com.example.demo.elasticsearch.repository.UserSearchRepository;
import com.example.demo.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserSearchServiceTest {

    @Mock
    private UserSearchRepository userSearchRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserSearchService userSearchService;

    private UserDocument userDoc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userDoc = UserDocument.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .age(25)
                .build();
    }

    @Test
    void testSaveUser_ShouldCallRepositorySave() {
        // Act
        userSearchService.saveUser(userDoc);

        // Assert
        verify(userSearchRepository, times(1)).save(userDoc);
    }

    @Test
    void testDeleteUser_ShouldCallRepositoryDelete() {
        // Act
        userSearchService.deleteUser(1L);

        // Assert
        verify(userSearchRepository, times(1)).deleteById(1L);
    }

    @Test
    void testSearchUsersByName_ShouldReturnMatchingDocuments() {
        // Arrange
        when(userSearchRepository.findByNameContainingIgnoreCase("ali"))
                .thenReturn(List.of(userDoc));

        // Act
        List<UserDocument> result = userSearchService.searchUsersByName("ali");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getName());
        verify(userSearchRepository).findByNameContainingIgnoreCase("ali");
    }

    @Test
    void testSearchUsersByAge_ShouldReturnPagedResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDocument> page = new PageImpl<>(List.of(userDoc));
        when(userSearchRepository.findByAge(25, pageable)).thenReturn(page);

        // Act
        List<UserDocument> result = userSearchService.searchUsersByAge(25, 0, 10);

        // Assert
        assertEquals(1, result.size());
        assertEquals(25, result.get(0).getAge());
        verify(userSearchRepository).findByAge(25, pageable);
    }
}
