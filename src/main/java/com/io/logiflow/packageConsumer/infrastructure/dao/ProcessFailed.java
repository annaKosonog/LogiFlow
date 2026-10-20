package com.io.logiflow.packageConsumer.infrastructure.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "process_failed")
public class ProcessFailed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long processFailedId;

    @Column(unique = true, nullable = false)
    private UUID uuid = UUID.randomUUID();

    @ElementCollection
    @CollectionTable(
            name = "process_failed_unpacked_packages",
            joinColumns = @JoinColumn(name = "process_failed_uuid",
                    referencedColumnName = "uuid")
    )

    @Column(name = "package_uuid")
    @Setter
    private List<UUID> unpackedPackageUuids;
}
