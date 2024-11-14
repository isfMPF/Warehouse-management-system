package com.project.wms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreatedDate
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name = "product",referencedColumnName = "code")
    private ProductEntity code;
    private int amount; //кол-во упаковок
    @ManyToOne
    @JoinColumn(name = "client",referencedColumnName = "codeClient")
    private ClientEntity codeClient;
    private Double total; //сумма заказа

}
