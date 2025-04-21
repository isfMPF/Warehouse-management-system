package com.project.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "client",
        uniqueConstraints = @UniqueConstraint(name = "uk_client_barcode", columnNames = "barcode"))

public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "barcode", unique = true)
    private Long codeClient; // код холодильника
    @Column(name = "store")
    private String name; //название торговой точки
    private String address;
    private String fio;
    private String phone;
    private DayOfWeek dayOfWeek;
}
