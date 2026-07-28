package br.com.pitflow.inventory.infrastructure.web;

import br.com.pitflow.inventory.controller.PartController;
import br.com.pitflow.inventory.infrastructure.web.dto.CreatePartRequest;
import br.com.pitflow.inventory.infrastructure.web.dto.UpdatePartRequest;
import br.com.pitflow.inventory.presenter.dto.PartResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PartRestAdapterTest {
    private final PartController controller = mock(PartController.class);
    private final PartRestAdapter adapter = new PartRestAdapter(controller);
    private final UUID id = UUID.randomUUID();
    private final PartResponse response =
            new PartResponse(id, "SKU-1", "Filtro", "Descrição", new BigDecimal("25.00"), 8);

    @Test
    void delegatesEveryEndpointAndReturnsExpectedStatuses() {
        var create = new CreatePartRequest("SKU-1", "Filtro", "Descrição", new BigDecimal("25.00"), 8);
        var update = new UpdatePartRequest("SKU-2", "Filtro 2", "Nova", new BigDecimal("30.00"), 4);
        when(controller.create(create)).thenReturn(response);
        when(controller.findPartById(id)).thenReturn(response);
        when(controller.findPartBySku("SKU-1")).thenReturn(response);
        when(controller.listParts()).thenReturn(List.of(response));
        when(controller.updatePart(id, update)).thenReturn(response);

        assertEquals(HttpStatus.CREATED, adapter.create(create).getStatusCode());
        assertEquals(response, adapter.getById(id).getBody());
        assertEquals(response, adapter.getBySku("SKU-1").getBody());
        assertEquals(1, adapter.listAll().getBody().size());
        assertEquals(response, adapter.update(id, update).getBody());
        assertEquals(HttpStatus.NO_CONTENT, adapter.delete(id).getStatusCode());
        verify(controller).deletePart(id);
    }
}
