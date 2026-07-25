package br.com.pitflow.inventory.infrastructure.web;

import br.com.pitflow.inventory.core.gateway.PartGateway;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/internal/inventory")
public class InternalInventoryRestAdapter {
    private final PartGateway partGateway;
    private final ServiceGateway serviceGateway;

    public InternalInventoryRestAdapter(PartGateway partGateway, ServiceGateway serviceGateway) {
        this.partGateway = partGateway;
        this.serviceGateway = serviceGateway;
    }

    @PostMapping("/parts/{id}/reservations")
    @Transactional
    public ResponseEntity<CatalogItemResponse> reservePart(
            @PathVariable UUID id, @RequestBody ReservePartRequest request) {
        var part = partGateway.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Part not found in inventory"));
        part.removeStock(request.quantity());
        partGateway.save(part);
        return ResponseEntity.ok(new CatalogItemResponse(part.getId(), part.getName(), part.getPrice()));
    }

    @GetMapping("/services/{id}")
    public ResponseEntity<CatalogItemResponse> getService(@PathVariable UUID id) {
        var service = serviceGateway.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found in inventory"));
        return ResponseEntity.ok(new CatalogItemResponse(service.getId(), service.getName(), service.getPrice()));
    }

    public record ReservePartRequest(int quantity) {}
    public record CatalogItemResponse(UUID id, String name, BigDecimal price) {}
}
