package org.example.controllers;

import org.example.dto.UserDto;
import org.example.model.Job;
import org.example.model.User;
import org.example.services.UsersService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @ModelAttribute("newUserData")
    public UserDto getNewUserData() {
        UserDto userData = new UserDto();
        userData.setUser(new User("[Введите имя]", "[Ведите фамилию]", (byte) -1, null));
        userData.setJob(new Job("[Введите профессию]", -1));
        System.out.println("returning " + userData);
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
        return user.getJob().isPresent() ? "employeedUserInfo" : "unemployeedUserInfo";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        usersService.delete(id);
        return "redirect:/users";
    }

    @PostMapping(params = "action=createNewUserForm")
    public String showCreateUserForm() {
        return "createUserForm";
    }

    @GetMapping("/update/{id}")
    public String updateUser(@PathVariable("id") long id, @ModelAttribute("userData") UserDto userDto) {
        User user = usersService.getUserById(id);
        userDto.setUser(user);
        user.getJob().ifPresent(userDto::setJob);
        return "update";
    }

    @PostMapping("/update/{id}")
    public String update (@PathVariable("id") long id, @ModelAttribute("userData") UserDto userDto, Model model) {
        User user = userDto.extractUser();
        usersService.update(id, user);
        return "redirect:/users";
    }

    @PostMapping(params = "action=create")
    public String create(@ModelAttribute("newUserData") UserDto userDto, Model model) {
        System.out.println("got " + userDto);
        User user = userDto.extractUser();
        usersService.save(user);
        return "redirect:/users";
    }

    @ExceptionHandler
    public String handleException(Exception ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "errorInfo";
    }
}