-- Add migration date field
alter table ui_setting_group
    add migration_date timestamp;
