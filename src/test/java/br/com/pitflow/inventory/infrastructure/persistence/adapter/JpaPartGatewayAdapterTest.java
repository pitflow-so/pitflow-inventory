package br.com.pitflow.inventory.infrastructure.persistence.adapter;

import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.infrastructure.persistence.entity.PartJpa;
import br.com.pitflow.inventory.infrastructure.persistence.repository.SpringPartRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JpaPartGatewayAdapterTest {
    private final SpringPartRepository repository = mock(SpringPartRepository.class);
    private final JpaPartGatewayAdapter adapter = new JpaPartGatewayAdapter(repository);

    @Test
    void mapsSaveQueriesListAndDelete() {
        var id = UUID.randomUUID();
        var part = new Part("SKU-1", "Filtro", "Descrição", new BigDecimal("25.00"), 8);
        part.setId(id);
        var entity = new PartJpa(id, "SKU-1", "Filtro", "Descrição", new BigDecimal("25.00"), 8);
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.findBySku("SKU-1")).thenReturn(Optional.of(entity));
        when(repository.findAll()).thenReturn(List.of(entity));

        adapter.save(part);
        assertEquals(id, adapter.findById(id).orElseThrow().getId());
        assertEquals("SKU-1", adapter.findBySku("SKU-1").orElseThrow().getSku());
        assertEquals(1, adapter.findAll().size());
        adapter.deleteById(id);

        verify(repository).save(any(PartJpa.class));
        verify(repository).deleteById(id);
    }

    @Test
    void returnsEmptyWhenPartDoesNotExist() {
        var id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        when(repository.findBySku("missing")).thenReturn(Optional.empty());
        assertTrue(adapter.findById(id).isEmpty());
        assertTrue(adapter.findBySku("missing").isEmpty());
    }
}
