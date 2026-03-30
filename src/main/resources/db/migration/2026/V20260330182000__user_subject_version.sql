alter table if exists t_user_subject
    add column if not exists version bigint;
