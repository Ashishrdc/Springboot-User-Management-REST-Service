package com.develop.assignmentPart2.controller;

import com.develop.assignmentPart2.dto.GetUser;
import com.develop.assignmentPart2.dto.LoginDTO;
import com.develop.assignmentPart2.dto.UserDTO;
import com.develop.assignmentPart2.exporter.excelExporter;
import com.develop.assignmentPart2.exporter.pdfExporter;
import com.develop.assignmentPart2.model.User;
import com.develop.assignmentPart2.service.UserService;
import com.lowagie.text.DocumentException;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/")
public class UserControllerAPI {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/auth/login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDTO loginDTO){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new ResponseEntity<>("Logged in successfully!", HttpStatus.OK);
    }

    // POST - CREATE USER
    @PostMapping(value = "/auth/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createUser(@RequestPart("user") String user,
                                        @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        UserDTO userDTO = this.userService.getJson(user);
        if (this.userService.existsByEmail(userDTO.getEmail())){
            return new ResponseEntity<>("Email already exists!", HttpStatus.BAD_REQUEST);
        }
        if (this.userService.existsByUsername(userDTO.getUsername())){
            return new ResponseEntity<>("Username already exists!", HttpStatus.BAD_REQUEST);
        }
        this.userService.createUser(userDTO, file);

        return new ResponseEntity<>("File Received & User Created", HttpStatus.OK);
    }

    @PostMapping("/updateUser")
    public ResponseEntity<UserDTO> updateUser(@RequestPart("user") String user, @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        UserDTO userDTO = this.userService.getJson(user);
        UserDTO updateUserDTO = this.userService.updateUser(userDTO, file);
        return new ResponseEntity<>(updateUserDTO, HttpStatus.OK);
    }

    // GET - GET USERS
    @ApiOperation("Fetches all the users from the repository.")
    @GetMapping("/users")
    public ResponseEntity<List<GetUser>> getAllUsers(){
        return  ResponseEntity.ok(this.userService.getAllUsers());
    }

    @ApiOperation("Fetches the user by id.")
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable int id){
        return ResponseEntity.ok(this.userService.getUserById(id));
    }



    // DISABLE & ACTIVATE- USER
    @ApiOperation("Sets the status of the user to disabled instead of deleting.")
    @GetMapping("/users/delete/{id}")
    // Sets the status of the user to disabled
    public ResponseEntity<String> deleteUserById(@PathVariable int id){
        return ResponseEntity.ok(this.userService.deleteUserById(id));
    }
    @ApiOperation("Sets the status of the user to active.")
    @GetMapping("/users/activate/{id}")
    // Activates the user
    public ResponseEntity<String> activateUserById(@PathVariable int id){
        return ResponseEntity.ok(this.userService.activateUserById(id));
    }

    @ApiOperation("Exports the User List as a PDF.")
    @GetMapping("/users/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=userList_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<User> userList = userService.listAll();
        pdfExporter pdf = new pdfExporter(userList);
        pdf.export(response);
    }

    @ApiOperation("Exports the User List as Excel Sheet.")
    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException{
        response.setContentType("application/octet-stream");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=usersList_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<User> userList = userService.listAll();
        excelExporter excel = new excelExporter(userList);
        excel.export(response);
    }
}