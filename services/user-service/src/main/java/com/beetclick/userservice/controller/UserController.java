package com.beetclick.userservice.controller;

import com.beetclick.common.dto.user.response.UpdateUserRequest;
import com.beetclick.common.dto.user.response.UserResponse;
import com.beetclick.userservice.service.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@RequestHeader("X-User-Email") String email) throws BadRequestException, ChangeSetPersister.NotFoundException {

        log.debug("GET /users/me email={}", email);
        return ResponseEntity.ok(service.me(email));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> all() {
        log.debug("GET /users/all");
        return ResponseEntity.ok(service.all());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> byId(@PathVariable UUID id) throws ChangeSetPersister.NotFoundException {
        log.debug("GET /users/{}", id);
        return ResponseEntity.ok(service.byId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest req) throws BadRequestException, ChangeSetPersister.NotFoundException {
        log.debug("PATCH /users/{} body={}", id, req);
        return ResponseEntity.ok(service.update(id, req));
    }

    private UUID parseUuid(String value, String headerName) throws BadRequestException {
        try {
            return UUID.fromString(value);
        } catch (Exception e) {
            throw new BadRequestException("Header " + headerName + " invalide (UUID attendu)");
        }
    }
}

