create table t_user_subject
(
    user_subject_id      bigserial primary key,
    user_id              bigserial    not null,
    subject_provider     varchar(50)  not null,
    external_subject_id  varchar(255) not null,
    is_valid             boolean      not null,
    date_created         timestamp    not null,
    date_updated         timestamp    not null,
    foreign key (user_id) references t_user (user_id) on delete cascade,
    constraint uk_user_subject unique (user_id, subject_provider, external_subject_id)
);
