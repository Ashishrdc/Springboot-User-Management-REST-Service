package com.develop.assignmentPart2.service;

import com.develop.assignmentPart2.dto.GetUser;
import com.develop.assignmentPart2.dto.UserDTO;
import com.develop.assignmentPart2.exporter.FileUploadUtil;
import com.develop.assignmentPart2.model.Role;
import com.develop.assignmentPart2.model.User;
import com.develop.assignmentPart2.repository.RoleRepository;
import com.develop.assignmentPart2.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{
    public static String uploadDir = System.getProperty("user.dir") + "\\src\\main\\webapp\\resources\\images";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;



    public boolean checkEmail(String email){
        return this.userRepository.existsByEmail(email);
    }

    public boolean checkUsername(String username){
        return this.userRepository.existsByUsername(username);
    }

    @Override  // This method is called when post request is hit.
    public UserDTO createUser(UserDTO userDTO, MultipartFile file) throws IOException{
        User user = this.dtoToUser(userDTO); // Converting the DTO to user

        if (file == null){
            user.setPhoto("default.jpg"); //if users selects no image it uses the default image.
        }
        else{
            String fileName = StringUtils.cleanPath(file.getOriginalFilename()); // Get filename
            user.setPhoto(fileName);                                             // Set filename
            FileUploadUtil.saveFile(uploadDir, fileName, file);                  // Save file in the directory
        }
        String encryptpass = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encryptpass);
        user.setEnabled(true);
        user.setStatus("ACTIVE");
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findById(2).get());      // Setting the role (2)[ROLE_USER] for authentication as user.
        user.setRoles(roles);
        user.setRole("USER");
        User savedUser = this.userRepository.save(user);
        return this.userToDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO, MultipartFile file) throws IOException {
        User user = this.userRepository.findById(userDTO.getId()).orElseThrow(()-> new RuntimeException("User not found at id = " + userDTO.getId()));
        User tempUser = this.userRepository.getOne(userDTO.getId());
        if (file == null){
            tempUser.setPhoto(user.getPhoto()); //Keep the same picture while updating.
        }
        else{
            String fileName = StringUtils.cleanPath(file.getOriginalFilename()); // Get filename
            tempUser.setPhoto(fileName);                                             // Set filename
            FileUploadUtil.saveFile(uploadDir, fileName, file);                  // Save file in the directory
        }
        tempUser.setUsername(userDTO.getUsername());
        tempUser.setEmail(userDTO.getEmail());
        tempUser.setPhoto(user.getPhoto());
        String password = userDTO.getPassword();
        if (password.isEmpty()){
            tempUser.setPassword(user.getPassword());
        }
        else{tempUser.setPassword(bCryptPasswordEncoder.encode(password));}
        User updatedUser = this.userRepository.save(tempUser);
        return this.userToDTO(updatedUser);
    }

    @Override // This method is used for creation of user through the register page view.
    public UserDTO registerUser(UserDTO userDTO, MultipartFile file) throws IOException {
        User user = this.dtoToUser(userDTO);

        if (file.isEmpty()){
            user.setPhoto("default.jpg"); //if users selects no image it uses the default image.
        }
        else{
            String fileName = StringUtils.cleanPath(file.getOriginalFilename()); // Get filename
            user.setPhoto(fileName);                                             // Set filename
            FileUploadUtil.saveFile(uploadDir, fileName, file);                  // Save file in the directory
        }
        // Storing the pass in bcrypt
        String encryptpass = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encryptpass);
        user.setEnabled(true);
        user.setStatus("ACTIVE");
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findById(2).get());      // Setting the role (2)[ROLE_USER] for authentication as user.
        user.setRoles(roles);
        user.setRole("USER");
        User savedUser = this.userRepository.save(user);
        return this.userToDTO(savedUser);
    }

    @Override
    public UserDTO getUserById(Integer id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found at id = " + id));
        return this.userToDTO(user);
    }

    @Override
    public List<GetUser> getAllUsers() {
        List<User> users = this.userRepository.findAll();

        // Converting the users list to list of userDTO and exposing only the required information.
        List<GetUser> userDTOS = users.stream().map(user -> this.userToGDTO(user)).collect(Collectors.toList());
        return userDTOS;
    }

    @Override
    public String deleteUserById(Integer id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found at id = " + id));
        user.setEnabled(false);
        user.setStatus("DISABLED");
        this.userRepository.save(user);
        return "User Deleted Successfully";
    }

    @Override
    public String activateUserById(Integer id){
        User user = this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found at id = " + id));
        user.setEnabled(true);
        user.setStatus("ACTIVE");
        this.userRepository.save(user);
        return "User Activated Successfully";
    }

    @Override
    public List<User> listAll(){
        List<User> user = userRepository.findAll();
        return user;
    }

    @Override
    public UserDTO getJson(String user) {
        UserDTO userJson = new UserDTO();
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            userJson = objectMapper.readValue(user, UserDTO.class);
        }
        catch (IOException ioe){
            System.out.println("Error while converting");
        }
        return userJson;
    }

    @Override
    public Boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public Boolean existsByUsername(String username){
        return this.userRepository.existsByUsername(username);
    }

    // DTO TO USER ENTITY
    public User dtoToUser(UserDTO userDTO){
        // Creating the new object
        User user = new User();

        // Setting values (DTO to user) object
        user.setPhoto(userDTO.getPhoto());
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        return user;
    }

    // ENTITY TO DTO
    public UserDTO userToDTO(User user){
        UserDTO userDTO = new UserDTO();

        userDTO.setPhoto(user.getPhoto());
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        return userDTO;
    }

    public GetUser userToGDTO(User user){
        GetUser getUser = new GetUser();

        getUser.setPhoto(user.getPhoto());
        getUser.setId(user.getId());
        getUser.setUsername(user.getUsername());
        getUser.setEmail(user.getEmail());
        getUser.setStatus(user.getStatus());
        getUser.setRole(user.getRole());
        return getUser;
    }


}
