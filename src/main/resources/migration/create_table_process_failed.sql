CREATE TABLE IF NOT EXISTS process_failed (
                                process_failed_id  BIGSERIAL PRIMARY KEY,
                                uuid                UUID NOT NULL UNIQUE,
                                unpacked_package_uuids TEXT
);

CREATE TABLE IF NOT EXISTS process_failed_unpacked_packages (
                           process_failed_uuid  UUID NOT NULL REFERENCES process_failed(uuid) ON DELETE CASCADE,
                           package_uuid           UUID NOT NULL
    );