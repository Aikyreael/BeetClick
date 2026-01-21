package com.beetclick.userservice.service;


import com.beetclick.common.dto.user.response.UpdateUserRequest;
import com.beetclick.common.dto.user.response.UserResponse;
import com.beetclick.userservice.entity.User;
import com.beetclick.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public UserResponse me(String email) throws ChangeSetPersister.NotFoundException {
        User u = repo.findByEmail(email)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        return toResponse(u);
    }

    public List<UserResponse> all() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse byId(UUID id) throws ChangeSetPersister.NotFoundException {
        User u = repo.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        return toResponse(u);
    }

    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest req) throws BadRequestException, ChangeSetPersister.NotFoundException {
        if (req.role() != null && !req.role().isBlank()) {
            throw new BadRequestException("Le champ 'role' est géré par auth-service (token JWT).");
        }

        User u = repo.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        if (req.countryCode() != null && !req.countryCode().isBlank()) {
            u.setCountryCode(req.countryCode());
        }
        if (req.rank() != null) {
            u.setRank(req.rank());
        }

        log.info("User updated id={}", id);
        return toResponse(u);
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getCountryCode(), u.getRank());
    }
}

