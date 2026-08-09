create table t_participant_maintenance_cursor (
    job_name varchar(64) primary key,
    last_participant_id bigint,
    date_created timestamp(6) not null,
    date_updated timestamp(6) not null,
    version bigint
);
