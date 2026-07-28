package br.com.pitflow.inventory.controller;

import br.com.pitflow.inventory.core.entity.Service;
import br.com.pitflow.inventory.core.usecase.service.inputPort.*;
import br.com.pitflow.inventory.infrastructure.web.dto.CreateServiceRequest;
import br.com.pitflow.inventory.infrastructure.web.dto.UpdateServiceRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServiceControllerTest {
    private final CreateService create = mock(CreateService.class);
    private final FindServiceById find = mock(FindServiceById.class);
    private final ListServices list = mock(ListServices.class);
    private final UpdateService update = mock(UpdateService.class);
    private final DeleteService delete = mock(DeleteService.class);
    private final ServiceController controller = new ServiceController(create, find, list, update, delete);

    @Test
    void performsCompleteServiceCrud() {
        var service = new Service("Alinhamento", "Completo", new BigDecimal("150.00"));
        when(create.execute(any())).thenReturn(service);
        when(find.execute(service.getId())).thenReturn(service);
        when(list.execute()).thenReturn(List.of(service));

        assertEquals(service.getId(), controller.create(
                new CreateServiceRequest("Alinhamento", "Completo", new BigDecimal("150.00"))).id());
        assertEquals("Alinhamento", controller.findById(service.getId()).name());
        assertEquals(1, controller.findAll().size());
        assertEquals(service.getId(), controller.update(service.getId(),
                new UpdateServiceRequest("Balanceamento", "Atualizado", new BigDecimal("180.00"))).id());
        controller.delete(service.getId());

        verify(update).execute(eq(service.getId()), argThat(command -> command.name().equals("Balanceamento")));
        verify(delete).execute(service.getId());
    }
}
