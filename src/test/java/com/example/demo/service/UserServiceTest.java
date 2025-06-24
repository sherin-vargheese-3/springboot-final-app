package com.example.demo.service;

import com.example.demo.contract.UserDTO;
import com.example.demo.elasticsearch.service.UserSearchService;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisTemplate<String, UserDTO> redisTemplate;

    @Mock
    private ValueOperations<String, UserDTO> valueOperations;

    @Mock
    private MessageService messageService;

    @Mock
    private UserSearchService userSearchService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).name("John").email("john@example.com").age(30).build();
        userDTO = UserDTO.builder().id(1L).name("John").email("john@example.com").age(30).build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testGetUserById_CacheHit() {
        when(valueOperations.get("users::1")).thenReturn(userDTO);
        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("John", result.getName());
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void testGetUserById_CacheMiss() {
        when(valueOperations.get("users::1")).thenReturn(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(1L);
        assertNotNull(result);
        verify(valueOperations).set("users::1", userDTO, 30, TimeUnit.MINUTES);
    }

    @Test
    void testGetAllUsers() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        List<UserDTO> result = userService.getAllUsers();
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getName());
    }

    @Test
    void testCreateUser() {
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.createUser(userDTO);
        verify(userSearchService).saveUser(any());
        assertEquals("John", result.getName());
    }

    @Test
    void testUpdateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO updateDTO = UserDTO.builder().name("Updated").email("updated@example.com").age(40).build();
        UserDTO result = userService.updateUser(1L, updateDTO);

        assertEquals("John", result.getName()); // because the mock still returns original
        verify(userSearchService).saveUser(any());
        verify(messageService).processUserMessage(contains("User"));
    }

    @Test
    void testDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
        verify(userSearchService).deleteUser(1L);
    }

    @Test
    void testSearchUsers() {
        when(userSearchService.searchUsersByName("John")).thenReturn(List.of());
        List<UserDTO> result = userService.searchUsers("John");

        assertNotNull(result);
    }

    @Test
    void testSearchUsersByAge() {
        when(userSearchService.searchUsersByAge(30, 0, 10)).thenReturn(List.of());
        List<UserDTO> result = userService.searchUsersByAge(30, 0, 10);

        assertNotNull(result);
    }
}
