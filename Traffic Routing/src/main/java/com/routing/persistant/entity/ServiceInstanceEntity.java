package com.routing.persistant.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_instance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceInstanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private Integer weight;

    @Column(nullable = false)
    private Boolean active;
}