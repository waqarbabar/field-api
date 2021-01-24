package com.task.tech.models;

import com.task.tech.dtos.GeoJsonDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "boundaries")
public class Boundary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID boundaryUuid;
    private String polygonId;
    private ZonedDateTime created;
    private ZonedDateTime updated;
    String geoJsonType;
    @Transient
    private Property properties = new Property();
    private String geometryType;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "boundary_id_fk")
    private List<Coordinate> coordinates = new ArrayList<>();
}
