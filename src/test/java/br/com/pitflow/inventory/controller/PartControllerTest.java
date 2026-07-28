package br.com.pitflow.inventory.controller;

import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.core.usecase.part.inputPort.*;
import br.com.pitflow.inventory.infrastructure.web.dto.CreatePartRequest;
import br.com.pitflow.inventory.infrastructure.web.dto.UpdatePartRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PartControllerTest {
    private final CreatePart create = mock(CreatePart.class);
    private final FindPartById findById = mock(FindPartById.class);
    private final FindPartBySku findBySku = mock(FindPartBySku.class);
    private final ListParts list = mock(ListParts.class);
    private final UpdatePart update = mock(UpdatePart.class);
    private final DeletePart delete = mock(DeletePart.class);
    private final PartController controller =
            new PartController(create, findById, findBySku, list, update, delete);

    @Test
    void delegatesCreateAndFindOperations() {
        var part = part();
        when(create.execute(any())).thenReturn(part);
        when(findById.execute(part.getId())).thenReturn(part);
        when(findBySku.execute("SKU-1")).thenReturn(part);

        var created = controller.create(new CreatePartRequest(
                "SKU-1", "Filtro", "Descrição", new BigDecimal("25.00"), 8));

        assertEquals(part.getId(), created.id());
        assertEquals("SKU-1", controller.findPartById(part.getId()).sku());
        assertEquals("Filtro", controller.findPartBySku("SKU-1").name());
        verify(create).execute(argThat(command ->
                command.sku().equals("SKU-1") && command.initialStock() == 8));
    }

    @Test
    void listsUpdatesAndDeletes() {
        var part = part();
        when(list.execute()).thenReturn(List.of(part));
        when(findById.execute(part.getId())).thenReturn(part);

        assertEquals(1, controller.listParts().size());
        var response = controller.updatePart(part.getId(), new UpdatePartRequest(
                "SKU-2", "Filtro premium", "Atualizado", new BigDecimal("30.00"), 4));
        controller.deletePart(part.getId());

        assertEquals(part.getId(), response.id());
        verify(update).execute(eq(part.getId()), argThat(command ->
                command.sku().equals("SKU-2") && command.stockQuantity() == 4));
        verify(delete).execute(part.getId());
    }

    private Part part() {
        return new Part("SKU-1", "Filtro", "Descrição", new BigDecimal("25.00"), 8);
    }
}
