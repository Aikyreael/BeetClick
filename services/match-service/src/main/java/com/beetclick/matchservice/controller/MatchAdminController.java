package com.beetclick.matchservice.controller;

import com.beetclick.common.dto.match.request.*;
import com.beetclick.common.dto.match.response.MatchResponse;
import com.beetclick.common.dto.match.response.OddsHistoryResponse;
import com.beetclick.matchservice.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/matches")
public class MatchAdminController {

    private final MatchService matchService;

    public MatchAdminController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MatchResponse create(@Valid @RequestBody MatchCreateRequest req) {
        return matchService.create(req);
    }

    @PutMapping("/{id}")
    public MatchResponse update(@PathVariable UUID id, @RequestBody MatchUpdateRequest req) throws ChangeSetPersister.NotFoundException {
        return matchService.update(id, req);
    }

    @PatchMapping("/{id}/odds")
    public MatchResponse updateOdds(@PathVariable UUID id, @Valid @RequestBody MatchOddsUpdateRequest req) throws ChangeSetPersister.NotFoundException {
        return matchService.updateOdds(id, req);
    }

    @PatchMapping("/{id}/status")
    public MatchResponse updateStatus(@PathVariable UUID id, @Valid @RequestBody MatchStatusUpdateRequest req) throws ChangeSetPersister.NotFoundException {
        return matchService.updateStatus(id, req);
    }

    @PatchMapping("/{id}/result")
    public MatchResponse updateResult(@PathVariable UUID id, @Valid @RequestBody MatchResultUpdateRequest req) throws ChangeSetPersister.NotFoundException {
        return matchService.updateResult(id, req);
    }

    @PostMapping("/{id}/cancel")
    public MatchResponse cancel(@PathVariable UUID id) throws ChangeSetPersister.NotFoundException {
        return matchService.cancel(id);
    }

    @GetMapping("/{id}/odds-history")
    public List<OddsHistoryResponse> oddsHistory(@PathVariable UUID id) throws ChangeSetPersister.NotFoundException {
        return matchService.oddsHistory(id);
    }
}
