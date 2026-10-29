package app.commerce.api;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import app.commerce.api.ApiDtos.UserInput;
import app.commerce.api.ApiDtos.UserView;
import app.commerce.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserView>> findAll() {
        return ResponseEntity.ok(
            userService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserView> findById(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(
            userService.findById(id)
        );
    }

    @PostMapping
    public ResponseEntity<UserView> create(
        @RequestBody UserInput input
    ) {
        UserView created = userService.create(input);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id)
            .toUri();

        return ResponseEntity
            .created(location)
            .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserView> update(
        @PathVariable Long id,
        @RequestBody UserInput input
    ) {
        return ResponseEntity.ok(
            userService.update(id, input)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable Long id
    ) {
        userService.delete(id);

        return ResponseEntity
            .noContent()
            .build();
    }
}