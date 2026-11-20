package com.upiiz.ligas_deportivas.services;

import com.upiiz.ligas_deportivas.entities.Liga;
import com.upiiz.ligas_deportivas.repositories.LigaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LigaServiceTest {

    @Mock
    private LigaRepository ligaRepository;

    @InjectMocks
    private LigaService ligaService;

    @Test
    void getAll_regresaLasLigasDelRepositorio() {
        when(ligaRepository.findAll()).thenReturn(List.of(
                new Liga("Liga MX", "Mexico", "2025-2026"),
                new Liga("Liga Zacatecana", "Mexico", "2023-2026")
        ));

        assertThat(ligaService.getAll()).hasSize(2);
    }

    @Test
    void getById_cuandoNoExiste_lanzaExcepcion() {
        when(ligaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ligaService.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Liga no encontrada");
    }

    @Test
    void update_copiaLosCamposYGuarda() {
        Liga existente = new Liga("Liga vieja", "Mexico", "2023-2024");
        existente.setId(1L);
        Liga cambios = new Liga("Liga nueva", "Colombia", "2025-2026");

        when(ligaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(ligaRepository.save(any(Liga.class))).thenAnswer(inv -> inv.getArgument(0));

        Liga resultado = ligaService.update(1L, cambios);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Liga nueva");
        assertThat(resultado.getPais()).isEqualTo("Colombia");
        assertThat(resultado.getTemporadaActual()).isEqualTo("2025-2026");
    }

    @Test
    void update_cuandoNoExiste_noGuardaNada() {
        when(ligaRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ligaService.update(5L, new Liga()))
                .isInstanceOf(RuntimeException.class);
        verify(ligaRepository, never()).save(any());
    }
}
