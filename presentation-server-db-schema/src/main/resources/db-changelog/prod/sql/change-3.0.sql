-- Creates configuration table
create table configuration
(
    id serial not null,
    config_key varchar(150) not null,
    config_value varchar(250)
);

create unique index configuration_config_key_uindex
    on configuration (config_key);

create unique index configuration_id_uindex
    on configuration (id);

alter table configuration
    add constraint configuration_pk
        primary key (id);

-- Give configuration ownership and privileges to psuser
GRANT ALL privileges ON TABLE configuration TO psuser;
ALTER TABLE configuration OWNER TO psuser;
