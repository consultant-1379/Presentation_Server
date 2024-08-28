-- Create configuration for the cache migration delay and set the default value to 24 hours
insert into configuration(config_key, config_value)
       values ('cache.migration.delay.hours','24');
