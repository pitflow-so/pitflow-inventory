package br.com.pitflow.inventory.infrastructure.persistence.adapter;

import br.com.pitflow.inventory.core.entity.Service;
import br.com.pitflow.inventory.infrastructure.persistence.entity.ServiceJpa;
import br.com.pitflow.inventory.infrastructure.persistence.repository.SpringServiceRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JpaServiceGatewayAdapterTest {
    private final SpringServiceRepository repository = mock(SpringServiceRepository.class);
    private final JpaServiceGatewayAdapter adapter = new JpaServiceGatewayAdapter(repository);

    @Test
    void mapsSaveQueriesListAndDelete() {
        var id = UUID.randomUUID();
        var service = new Service("Alinhamento", "Completo", new BigDecimal("150.00"));
        service.setId(id);
        var entity = new ServiceJpa(id, "Alinhamento", "Completo", new BigDecimal("150.00"));
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.findAll()).thenReturn(List.of(entity));

        adapter.save(service);
        assertEquals(id, adapter.findById(id).orElseThrow().getId());
        assertEquals(1, adapter.findAll().size());
        adapter.deleteById(id);

        verify(repository).save(any(ServiceJpa.class));
        verify(repository).deleteById(id);
    }

    @Test
    void returnsEmptyWhenServiceDoesNotExist() {
        var id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertTrue(adapter.findById(id).isEmpty());
    }
}
