package br.com.pitflow.inventory.infrastructure.web;

import br.com.pitflow.inventory.controller.ServiceController;
import br.com.pitflow.inventory.infrastructure.web.dto.CreateServiceRequest;
import br.com.pitflow.inventory.infrastructure.web.dto.UpdateServiceRequest;
import br.com.pitflow.inventory.presenter.dto.ServiceResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ServiceRestAdapterTest {
    private final ServiceController controller = mock(ServiceController.class);
    private final ServiceRestAdapter adapter = new ServiceRestAdapter(controller);

    @Test
    void delegatesEveryEndpointAndReturnsExpectedStatuses() {
        var id = UUID.randomUUID();
        var response = new ServiceResponse(id, "Alinhamento", "Completo", new BigDecimal("150.00"));
        var create = new CreateServiceRequest("Alinhamento", "Completo", new BigDecimal("150.00"));
        var update = new UpdateServiceRequest("Balanceamento", "Novo", new BigDecimal("180.00"));
        when(controller.create(create)).thenReturn(response);
        when(controller.findById(id)).thenReturn(response);
        when(controller.findAll()).thenReturn(List.of(response));
        when(controller.update(id, update)).thenReturn(response);

        assertEquals(HttpStatus.CREATED, adapter.create(create).getStatusCode());
        assertEquals(response, adapter.getById(id).getBody());
        assertEquals(1, adapter.listAll().getBody().size());
        assertEquals(response, adapter.update(id, update).getBody());
        assertEquals(HttpStatus.NO_CONTENT, adapter.delete(id).getStatusCode());
        verify(controller).delete(id);
    }
}
