package com.example.booking.controller;

import com.example.booking.dto.*;
import com.example.booking.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resources")
public class ResourceController {
    private final ResourceService service;
    public ResourceController(ResourceService service) { this.service = service; }
    @GetMapping
    public Page<ResourceResponse> list(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.all(pageable);
    }
    @GetMapping("/{id}")
    public ResourceResponse get(@PathVariable Long id) { return ResourceResponse.from(service.get(id)); }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceResponse create(@Valid @RequestBody ResourceRequest request) { return service.create(request); }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResourceResponse update(@PathVariable Long id, @Valid @RequestBody ResourceRequest request) {
        return service.update(id, request);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
