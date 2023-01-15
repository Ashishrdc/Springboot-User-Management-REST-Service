package com.develop.assignmentPart2.controller;

import com.develop.assignmentPart2.dto.UserDTO;
import com.develop.assignmentPart2.exporter.FileUploadUtil;
import com.develop.assignmentPart2.exporter.excelExporter;
import com.develop.assignmentPart2.exporter.pdfExporter;
import com.develop.assignmentPart2.model.User;
import com.develop.assignmentPart2.repository.UserRepository;
import com.develop.assignmentPart2.service.UserService;
import com.develop.assignmentPart2.service.UserServiceImpl;
import com.lowagie.text.DocumentException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.develop.assignmentPart2.service.UserServiceImpl.uploadDir;

@Controller
public class ViewController {


    @Autowired
    private UserService userService;
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // LoginPage View
    @GetMapping({"/", "/login"})
    public String loginPage(){
        return "login";
    }

    //--------------------------------------------------------------------//
    // RegisterPage View -------------------------------------------------//
    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("userDTO", new UserDTO());
        return "register";
    }

    @PostMapping(value = "/register")
    public String afterRegister(@ModelAttribute UserDTO userDTO, BindingResult bindingResult,
                                Model model,
                                @RequestParam("photo") MultipartFile file) throws IOException {

        Boolean chkEmail = userServiceImpl.checkEmail(userDTO.getEmail());
        Boolean chkUsername = userServiceImpl.checkUsername(userDTO.getUsername());
        if (chkEmail && chkUsername) {
            model.addAttribute("errEmail", "Email already exists!");
            model.addAttribute("errUsername", "Username already exists!");
            model.addAttribute("fieldEmail", "err");
            model.addAttribute("fieldUsername", "err");
            return "register";
        } else if (chkEmail) {
            model.addAttribute("errEmail", "Email already exists!");
            model.addAttribute("fieldEmail", "err");
            return "register";
        } else if (chkUsername) {
            model.addAttribute("errUsername", "Username already exists!");
            model.addAttribute("fieldUsername", "err");
            return "register";
        } else {
            this.userService.registerUser(userDTO, file);
        }
        return "redirect:/profile";
    }
    //--------------------------------------------------------------------//


    // Profile View -----------------------------------------------------//
    @GetMapping("/profile")
    public String profilePage(Model model, Principal principal){
        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        model.addAttribute("photo", user.getPhoto());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("id", user.getId());
        return "profile";
    }

    @GetMapping("/profile/edituser/{id}")
    public String editUser(@PathVariable int id, Model model){
        UserDTO user = userService.getUserById(id);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setPhoto(user.getPhoto());
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());

        model.addAttribute("edituser", userDTO);
        return "edituser";
    }

    @PostMapping("/profile/edituser/{id}")
    public String postEditUser(@ModelAttribute("edituser") UserDTO userDTO, BindingResult bindingResult,
                               Principal principal, Model model,
                               @RequestParam("photo") MultipartFile file) throws IOException{
        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        User tempUser = userRepository.getOne(user.getId());

        if(file.isEmpty()){
            tempUser.setPhoto(user.getPhoto());
        }
        else{
            String fileName = StringUtils.cleanPath((file.getOriginalFilename()));
            tempUser.setPhoto(fileName);
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        }
        // If updated password, username, & email.
        tempUser.setUsername(userDTO.getUsername());
        tempUser.setEmail(userDTO.getEmail());
        String password = userDTO.getPassword();
        if (password.isEmpty()){
            tempUser.setPassword(user.getPassword());
        }
        else{tempUser.setPassword(bCryptPasswordEncoder.encode(password));}
        this.userRepository.save(tempUser);
        model.addAttribute("classes", "text-center alert-success successmsg");
        model.addAttribute("saved", "Profile Saved");

        return editUser(user.getId(), model);
    }

    @GetMapping("/profile/delete/{id}") //Disables the user and logs the user out.
    public String deleteUser(@PathVariable int id){
        User user = userRepository.getOne(id);
        this.userService.deleteUserById(id);
        return "redirect:/logout";
    }

    //--------------------------------------------------------------------//

    // UserList View -----------------------------------------------------//
    // GetMapping
    @GetMapping("/userlist")
    public String userList(Model model) {
        List<User> userlist = userService.listAll(); // Providing the list to the table to iterate and print data.
        model.addAttribute("users", userlist);
        return "userlist";
    }

    // Disables the user but doesn't log them out. But ones they log out they can't log back in.
    @GetMapping("/userlist/delete/{id}")
    public String disableUser(@PathVariable int id){
        this.userService.deleteUserById(id);
        return "redirect:/userlist";
    }

    @GetMapping("/userlist/activate/{id}")
    public String activateUser(@PathVariable int id){
        this.userService.activateUserById(id);
        return "redirect:/userlist";
    }
    //--------------------------------------------------------------------//

    // EXPORTING LIST TO EXCEL & PDF -------------------------------------//
    @GetMapping("/userlist/export/pdf")
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

    @GetMapping("/userlist/export/excel")
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
    //--------------------------------------------------------------------//
}
