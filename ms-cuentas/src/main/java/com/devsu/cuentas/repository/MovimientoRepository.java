package com.devsu.cuentas.repository;

import com.devsu.cuentas.domain.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    @Query("""
            SELECT m FROM Movimiento m
            JOIN m.cuenta c
            WHERE c.clienteId = :clienteId
              AND m.fecha BETWEEN :inicio AND :fin
            ORDER BY m.fecha ASC
            """)
    List<Movimiento> findByClienteAndRango(@Param("clienteId") String clienteId,
                                           @Param("inicio") LocalDateTime inicio,
                                           @Param("fin") LocalDateTime fin);
}
