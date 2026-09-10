package com.example.booking.service;

import com.example.booking.dto.*;
import com.example.booking.entity.Resource;
import com.example.booking.exception.NotFoundException;
import com.example.booking.repository.ResourceRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.function.Function;

@Service
public class ResourceService {
    private final ResourceRepository resources;
    public ResourceService(ResourceRepository resources) { this.resources = resources; }
    public Page<ResourceResponse> all(Pageable pageable) { return resources.findAll(pageable).map(ResourceResponse::from); }
    public Resource get(Long id) { return resources.findById(id).orElseThrow(() -> new NotFoundException("Resource " + id + " not found")); }
    public ResourceResponse create(ResourceRequest request) {
        Resource resource = new Resource(request.name(), request.description(), request.price(),
                request.available() == null || request.available());
        return ResourceResponse.from(resources.save(resource));
    }
    public ResourceResponse update(Long id, ResourceRequest request) {
        Resource resource = get(id);
        resource.update(request.name(), request.description(), request.price(),
                request.available() == null || request.available());
        return ResourceResponse.from(resources.save(resource));
    }
    public void delete(Long id) { resources.delete(get(id)); }
}
