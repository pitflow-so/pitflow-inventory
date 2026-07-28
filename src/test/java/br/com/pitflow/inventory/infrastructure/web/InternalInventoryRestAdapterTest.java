package br.com.pitflow.inventory.infrastructure.web;

import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.core.entity.Service;
import br.com.pitflow.inventory.core.gateway.PartGateway;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InternalInventoryRestAdapterTest {
    private final PartGateway parts = mock(PartGateway.class);
    private final ServiceGateway services = mock(ServiceGateway.class);
    private final InternalInventoryRestAdapter adapter = new InternalInventoryRestAdapter(parts, services);

    @Test
    void reservesPartAndReturnsCatalogData() {
        var part = new Part("SKU-1", "Filtro", "Descrição", new BigDecimal("25.00"), 8);
        when(parts.findById(part.getId())).thenReturn(Optional.of(part));

        var body = adapter.reservePart(part.getId(),
                new InternalInventoryRestAdapter.ReservePartRequest(3)).getBody();

        assertNotNull(body);
        assertEquals(part.getId(), body.id());
        assertEquals(5, part.getStockQuantity());
        verify(parts).save(part);
    }

    @Test
    void returnsServiceAndRejectsMissingCatalogItems() {
        var service = new Service("Alinhamento", "Completo", new BigDecimal("150.00"));
        when(services.findById(service.getId())).thenReturn(Optional.of(service));
        assertEquals(service.getName(), adapter.getService(service.getId()).getBody().name());

        var missingPart = UUID.randomUUID();
        var missingService = UUID.randomUUID();
        when(parts.findById(missingPart)).thenReturn(Optional.empty());
        when(services.findById(missingService)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> adapter.reservePart(missingPart, new InternalInventoryRestAdapter.ReservePartRequest(1)));
        assertThrows(IllegalArgumentException.class, () -> adapter.getService(missingService));
    }
}
