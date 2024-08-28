-- Updates the ui_setting tables value column to a bigger number
ALTER TABLE ui_setting ALTER COLUMN value TYPE varchar(64000);
