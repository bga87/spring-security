package org.example.controllers;

import org.example.dto.UserDto;
import org.example.model.User;
import org.example.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;


@Controller
@RequestMapping("/users")
@SessionAttributes(names = {"newUserData", "userData"})
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

//    @Autowired
    @ModelAttribute("newUserData")
    public UserDto getNewUserData(UserDto userData) {
        System.out.println("got " + userData);

        return userData;
    }

    @GetMapping()
    public String index() {
        return "redirect:/users/list";
    }

    @GetMapping("/list")
    @ModelAttribute("users")
    public List<User> listUsers() {
        return usersService.listUsers();
    }

    @GetMapping(params = "action=show")
    public String showUser(@RequestParam("userId") long id, Model model) {
        User user = usersService.getUserById(id);
        model.addAttribute("user", user);
        return "userInfo";
    }

    @GetMapping(params = "action=delete")
    public String deleteUser(@RequestParam("userId") long id) {
        usersService.delete(id);
        return "redirect:/users/list";
    }

    @PostMapping(params = "action=showCreateUserForm")
    public String showCreateUserForm() {
        return "createUserForm";
    }

    @PostMapping(params = "action=create")
    public String create(@ModelAttribute("newUserData") UserDto userDto, SessionStatus sessionStatus) {
        System.out.println("in create() got " + userDto);
        usersService.save(userDto.getUser());
        sessionStatus.setComplete();
        return "redirect:/users/list";
    }

    @GetMapping(params = "action=showUpdateUserForm")
    public String showUpdateUserForm(@RequestParam("userId") long id, UserDto userDto, Model model) {
//        UserDto userDto = new UserDto();
        userDto.extractDataFrom(usersService.getUserById(id));
        model.addAttribute("userData", userDto);
        return "updateUserForm";
    }

    @PostMapping(params = "action=update")
    public String updateUser(@ModelAttribute("userData") UserDto userDto, SessionStatus sessionStatus) {
        usersService.update(userDto.getId(), userDto.getUser());
        sessionStatus.setComplete();
        return "redirect:/users/list";
    }

    @ExceptionHandler
    public String handleException(Exception ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "errorInfo";
    }
}