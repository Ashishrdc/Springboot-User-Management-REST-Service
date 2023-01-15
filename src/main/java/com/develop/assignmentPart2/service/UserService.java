package com.develop.assignmentPart2.service;

import com.develop.assignmentPart2.dto.GetUser;
import com.develop.assignmentPart2.dto.UserDTO;
import com.develop.assignmentPart2.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {

    UserDTO createUser(UserDTO userDTO, MultipartFile file) throws IOException;

    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);

    UserDTO updateUser(UserDTO userDTO, MultipartFile file) throws IOException;

    UserDTO registerUser(UserDTO user, MultipartFile file) throws IOException;

    UserDTO getUserById(Integer id);

    List<GetUser> getAllUsers();

    List<User> listAll();

    String deleteUserById(Integer id);
    String activateUserById(Integer id);

    UserDTO getJson(String user);
}
