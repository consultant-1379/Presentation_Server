
-- Creates Settings Group table
create table if not exists ui_setting_group
(
  id bigserial not null,
  name varchar(100) not null,
  username varchar(250) not null,
  application varchar(200) not null,
  unique(application, name, username),
  primary key(id)
);

-- Give Setting Group ownership and privileges to psuser
GRANT ALL privileges ON TABLE ui_setting_group TO psuser;
ALTER TABLE ui_setting_group OWNER TO psuser;


-- Create Setting table
create table if not exists ui_setting (
  id bigserial not null,
  name varchar(100) not null,
  value varchar(5000) not null,
  setting_group_id bigint not null,
  created timestamp not null,
  lastUpdated timestamp,
  primary key(id),
  unique (name, setting_group_id),
  constraint ui_setting_to_ui_setting_group_by_id_fkey foreign key (setting_group_id)
    references ui_setting_group (id) match simple
    on update cascade on delete cascade
);

-- Give Setting Group ownership and privileges to psuser
GRANT ALL privileges ON TABLE ui_setting TO psuser;
ALTER TABLE ui_setting OWNER TO psuser;