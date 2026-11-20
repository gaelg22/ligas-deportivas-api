package com.upiiz.ligas_deportivas.services;

import com.upiiz.ligas_deportivas.entities.Entrenador;
import com.upiiz.ligas_deportivas.entities.Equipo;
import com.upiiz.ligas_deportivas.entities.Liga;
import com.upiiz.ligas_deportivas.repositories.EntrenadorRepository;
import com.upiiz.ligas_deportivas.repositories.EquipoRepository;
import com.upiiz.ligas_deportivas.repositories.LigaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquipoServiceTest {

    @Mock
    private EquipoRepository equipoRepository;

    @Mock
    private LigaRepository ligaRepository;

    @Mock
    private EntrenadorRepository entrenadorRepository;

    @InjectMocks
    private EquipoService equipoService;

    @Test
    void create_asignaLigaYEntrenador() {
        Liga liga = new Liga("Liga Zacatecana", "Mexico", "2023-2026");
        Entrenador entrenador = new Entrenador("Juan Perez", 10, "Mexicana");

        when(ligaRepository.findById(1L)).thenReturn(Optional.of(liga));
        when(entrenadorRepository.findById(2L)).thenReturn(Optional.of(entrenador));
        when(equipoRepository.save(any(Equipo.class))).thenAnswer(inv -> inv.getArgument(0));

        Equipo equipo = equipoService.create(new Equipo("Mineros", "Zacatecas"), 1L, 2L);

        assertThat(equipo.getLiga()).isSameAs(liga);
        assertThat(equipo.getEntrenador()).isSameAs(entrenador);
    }

    @Test
    void create_sinLigaNiEntrenador_guardaElEquipoSolo() {
        when(equipoRepository.save(any(Equipo.class))).thenAnswer(inv -> inv.getArgument(0));

        Equipo equipo = equipoService.create(new Equipo("Mineros", "Zacatecas"), null, null);

        assertThat(equipo.getLiga()).isNull();
        assertThat(equipo.getEntrenador()).isNull();
    }

    @Test
    void create_conLigaInexistente_lanzaExcepcionYNoGuarda() {
        when(ligaRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> equipoService.create(new Equipo("Mineros", "Zacatecas"), 77L, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Liga no encontrada");
        verify(equipoRepository, never()).save(any());
    }
}
