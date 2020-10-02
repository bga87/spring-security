package org.example.controllers;

import org.example.dto.UserDto;
import org.example.exceptions.UserAlreadyExistsException;
import org.example.model.Job;
import org.example.model.User;
import org.example.services.UsersService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("users", usersService.listUsers());
        return "index";
    }

    @GetMapping("/{id}")
    public String showUser(@PathVariable("id") long id, Model model) {
        User user = usersService.getUserById(id);
        model.addAttribute("user", user);
        return user.getJob().isPresent() ? "show_employeed" : "show_unemployeed";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        usersService.delete(id);
        return "redirect:/users";
    }

    @GetMapping("/new")
    public String newUser(@ModelAttribute("userData") UserDto userDto) {
        return "new";
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
        decodeStringsFromLatin1ToUtf8(user);

        try {
            usersService.update(id, user);
        } catch (UserAlreadyExistsException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "duplicate_user_info";
        }

        return "redirect:/users";
    }

    @PostMapping()
    public String create(@ModelAttribute("userData") UserDto userDto, Model model) {
        User user = userDto.extractUser();
        decodeStringsFromLatin1ToUtf8(user);

        try {
            usersService.save(user);
        } catch (UserAlreadyExistsException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "duplicate_user_info";
        }

        return "redirect:/users";
    }

    private void decodeStringsFromLatin1ToUtf8(User user) {
        user.setName(new String(user.getName().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
        user.setSurname(new String(user.getSurname().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
        user.getJob().ifPresent(
                job -> job.setName(new String(job.getName().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8))
        );
    }
}