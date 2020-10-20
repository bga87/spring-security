package org.example.controllers;

import org.example.dto.UserDto;
import org.example.model.Role;
import org.example.model.User;
import org.example.services.UsersService;
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

    @ModelAttribute("newUserData")
    public UserDto getNewUserData() {
        return new UserDto();
    }

    @GetMapping("/admin")
    public String adminAccess() {
        return "redirect:/users";
    }

    @GetMapping("/user")
    public String userAccess() {
        return "redirect:/users?action=show&userId=1000";
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
        usersService.save(usersService.createUserFromDto(userDto));
        sessionStatus.setComplete();
        return "redirect:/users/list";
    }

    @GetMapping(params = "action=showUpdateUserForm")
    public String showUpdateUserForm(@RequestParam("userId") long id, Model model) {
        UserDto userData = new UserDto(usersService.getUserById(id));
        model.addAttribute("userData", userData);
        return "updateUserForm";
    }

    @PostMapping(params = "action=update")
    public String updateUser(@ModelAttribute("userData") UserDto userDto, SessionStatus sessionStatus) {
        usersService.update(userDto.getId(), usersService.createUserFromDto(userDto));
        sessionStatus.setComplete();
        return "redirect:/users/list";
    }

    @ExceptionHandler
    public String handleException(Exception ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "errorInfo";
    }
}