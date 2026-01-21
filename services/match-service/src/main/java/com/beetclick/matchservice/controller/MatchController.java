package com.beetclick.matchservice.controller;

import com.beetclick.common.dto.match.request.*;
import com.beetclick.common.dto.match.response.MatchResponse;
import com.beetclick.matchservice.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/{id}")
    public MatchResponse get(@PathVariable UUID id) throws ChangeSetPersister.NotFoundException {
        return matchService.get(id);
    }

    @GetMapping
    public Page<MatchResponse> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) String team,
            Pageable pageable
    ) {
        return matchService.list(status, from, to, team, pageable);
    }
}
