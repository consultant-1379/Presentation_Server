-- Creates a view which lists all objects in the database and their respective sizes
create or replace view database_relations as
    select relname as "name",
           pg_size_pretty(pg_total_relation_size(C.oid)) as "total_size",
           case relkind
               when 'r' then 'TABLE'
               when 'm' then 'MATERIALIZED_VIEW'
               when 'i' then 'INDEX'
               when 'S' then 'SEQUENCE'
               when 'v' then 'VIEW'
               when 'c' then 'TYPE'
               else 'UNKNOWN'
               end as "type"
    from pg_class C
             left join pg_namespace N on (N.oid = C.relnamespace)
    where nspname not in ('pg_catalog', 'information_schema')
      and C.relkind <> 'i'
      and nspname !~ '^pg_toast'
    order by pg_total_relation_size(C.oid) DESC;
