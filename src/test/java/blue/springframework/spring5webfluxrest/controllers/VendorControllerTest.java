package blue.springframework.spring5webfluxrest.controllers;

import blue.springframework.spring5webfluxrest.domain.Vendor;
import blue.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class VendorControllerTest {

    VendorRepository vendorRepository;
    VendorController vendorController;
    WebTestClient webTestClient;

    @Before
    public void setUp() throws Exception {
        vendorRepository = Mockito.mock(VendorRepository.class);
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    public void list()
    {
        BDDMockito.given(vendorRepository.findAll())
                .willReturn(Flux.just(Vendor.builder().firstName("Jack").build(),
                        Vendor.builder().firstName("Luke").build()));

        webTestClient.get()
                .uri("/api/v1/vendors")
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    public void getById()
    {
        BDDMockito.given(vendorRepository.findById("someid"))
                .willReturn(Mono.just(Vendor.builder().firstName("Luke").build()));

        webTestClient.get()
                .uri("/api/v1/vendors/someid")
                .exchange()
                .expectBody(Vendor.class);
    }

    @Test
    public void createVendor()
    {
        BDDMockito.given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().build()));

        Mono<Vendor> vendorToSave = Mono.just(Vendor.builder().firstName("Luke").build());

        webTestClient.post()
                .uri("/api/v1/vendors")
                .body(vendorToSave, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void updateVendor()
    {
        BDDMockito.given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().firstName("test").build()));

        Mono<Vendor> vendorToSave = Mono.just(Vendor.builder().firstName("Luke").build());

        webTestClient.put()
                .uri("/api/v1/vendors/someid")
                .body(vendorToSave, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testPatchVendorWithChanges()
    {
        BDDMockito.given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder().build()));

        BDDMockito.given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorToPatchMono = Mono.just(Vendor.builder().firstName("Luke").lastName("Petzer").build());

        webTestClient.patch()
                .uri("/api/v1/vendors/someid")
                .body(vendorToPatchMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(vendorRepository).save(any());
    }

    @Test
    public void testPatchVendorNoChanges()
    {
        BDDMockito.given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder().build()));

        BDDMockito.given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorToPatchMono = Mono.just(Vendor.builder().build());

        webTestClient.patch()
                .uri("/api/v1/vendors/someid")
                .body(vendorToPatchMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(vendorRepository, never()).save(any());
    }
}