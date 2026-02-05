package tn.enis.pfa.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tn.enis.pfa.entity.User;
import tn.enis.pfa.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping
	public User create(@RequestBody User user) {
		return userService.save(user);
	}

	@GetMapping
	public List<User> getAll() {
		return userService.findAll();
	}

	@GetMapping("/test")
	public String test() {
		return "Backend PFA is running";
	}
}
