package com.william.usuario.infrastrucuture.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "telefone")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Telefone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero", length = 10)
    private String numero;
    @Column(name = "ddd", length = 3)
    private String ddd;
    @Column(name = "usuario_id")
    private Long usuario_id;

}
