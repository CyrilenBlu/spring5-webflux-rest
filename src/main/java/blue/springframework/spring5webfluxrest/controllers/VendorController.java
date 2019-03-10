package blue.springframework.spring5webfluxrest.controllers;

import blue.springframework.spring5webfluxrest.domain.Vendor;
import blue.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class VendorController {

    private final VendorRepository vendorRepository;

    public VendorController(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @GetMapping("/api/v1/vendors")
    private Flux<Vendor> list()
    {
        return vendorRepository.findAll();
    }

    @GetMapping("/api/v1/vendors/{id}")
    private Mono<Vendor> getById(@PathVariable String id)
    {
        return vendorRepository.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/vendors")
    private Mono<Void> create(@RequestBody Publisher<Vendor> vendorStream)
    {
        return vendorRepository.saveAll(vendorStream).then();
    }

    @PutMapping("/api/v1/vendors/{id}")
    private Mono<Vendor> update(@PathVariable String id, @RequestBody Vendor vendor)
    {
        vendor.setId(id);
        return vendorRepository.save(vendor);
    }
}
