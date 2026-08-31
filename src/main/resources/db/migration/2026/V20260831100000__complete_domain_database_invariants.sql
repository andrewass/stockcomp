do
$$
begin
    if (select count(*) from t_leaderboard) > 1 then
        raise exception 'Expected at most one leaderboard before enforcing the singleton invariant';
    end if;

    if exists (
        select participant_id, symbol
        from t_investment
        group by participant_id, symbol
        having count(*) > 1
    ) then
        raise exception 'Duplicate investments exist for the same participant and symbol';
    end if;

    if exists (
        select 1
        from t_leaderboard_job job
        left join t_contest contest on contest.contest_id = job.contest_id
        where contest.contest_id is null
    ) then
        raise exception 'Leaderboard jobs reference contests that no longer exist';
    end if;
end
$$;

alter table t_leaderboard
    add column singleton_key boolean not null default true,
    add constraint uq_t_leaderboard_singleton_key unique (singleton_key);

alter table t_investment
    add constraint uq_t_investment_participant_symbol unique (participant_id, symbol);

drop index idx_t_investment_participant_symbol;

alter table t_leaderboard_job
    add constraint fk_t_leaderboard_job_contest
        foreign key (contest_id) references t_contest (contest_id) on delete cascade;

alter table t_participant
    drop constraint t_participant_contest_id_fkey,
    add constraint fk_t_participant_contest
        foreign key (contest_id) references t_contest (contest_id) on delete cascade;

alter table t_investment
    drop constraint t_investment_participant_id_fkey,
    add constraint fk_t_investment_participant
        foreign key (participant_id) references t_participant (participant_id) on delete cascade;

alter table t_investment_order
    drop constraint t_investment_order_participant_id_fkey,
    add constraint fk_t_investment_order_participant
        foreign key (participant_id) references t_participant (participant_id) on delete cascade;

alter table t_medal
    drop constraint t_medal_contest_id_fkey,
    add constraint fk_t_medal_contest
        foreign key (contest_id) references t_contest (contest_id) on delete cascade;

alter table t_user
    add constraint ck_t_user_role_known check (user_role in ('USER', 'ADMIN')),
    add constraint ck_t_user_status_known check (user_status in ('ACTIVE', 'INACTIVE', 'SUSPENDED'));

alter table t_user_subject
    add constraint ck_t_user_subject_provider_known check (subject_provider in ('GOOGLE'));

alter table t_contest
    add constraint ck_t_contest_status_known
        check (contest_status in ('AWAITING_START', 'RUNNING', 'STOPPED', 'AWAITING_COMPLETION', 'COMPLETED')),
    add constraint ck_t_contest_valid_time_range check (start_time < end_time);

alter table t_investment_order
    add constraint ck_t_investment_order_transaction_type_known check (transaction_type in ('BUY', 'SELL')),
    add constraint ck_t_investment_order_status_known check (order_status in ('ACTIVE', 'COMPLETED', 'FAILED', 'TERMINATED')),
    add constraint ck_t_investment_order_expiration_after_creation check (expiration_time > date_created);

alter table t_leaderboard_job
    add constraint ck_t_leaderboard_job_status_known check (job_status in ('CREATED', 'FAILED', 'COMPLETED'));

alter table t_medal
    alter column medal_value set not null,
    alter column position set not null,
    add constraint ck_t_medal_value_known check (medal_value in ('GOLD', 'SILVER', 'BRONZE')),
    add constraint ck_t_medal_position_matches_value check (
        (medal_value = 'GOLD' and position = 1) or
        (medal_value = 'SILVER' and position = 2) or
        (medal_value = 'BRONZE' and position = 3)
    );
