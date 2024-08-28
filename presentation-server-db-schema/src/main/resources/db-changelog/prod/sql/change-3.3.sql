-- Enable migration for UI settings
insert into configuration(config_key, config_value)
       values ('cache.migration.enabled','true');
