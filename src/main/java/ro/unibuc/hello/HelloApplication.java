package ro.unibuc.hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import ro.unibuc.hello.data.UserRepository;
import ro.unibuc.hello.dto.CreateTodoRequest;
import ro.unibuc.hello.dto.CreateUserRequest;
import ro.unibuc.hello.service.TodoService;
import ro.unibuc.hello.service.UserService;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableMongoRepositories
public class HelloApplication {

	@Autowired
	private UserService userService;

	@Autowired
	private TodoService todoService;

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}

	@PostConstruct
	public void runAfterObjectCreated() {
		if (userRepository.findByEmail("frodo@theshire.me") == null) {
			userService.createUser(new CreateUserRequest("Frodo Baggins", "frodo@theshire.me"));
			todoService.createTodo(new CreateTodoRequest("Take the ring to Mordor", "frodo@theshire.me"));
		}
	}
}
