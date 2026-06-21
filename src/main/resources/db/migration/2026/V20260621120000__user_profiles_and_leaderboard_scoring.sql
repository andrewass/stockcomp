do
$$
begin
    if (select count(*) from t_leaderboard) > 1 then
        raise exception 'Expected at most one leaderboard before profile backfill';
    end if;

    if exists (
        select user_id
        from t_leaderboard_entry
        group by user_id
        having count(*) > 1
    ) then
        raise exception 'Duplicate leaderboard entries exist for the same user';
    end if;

    if exists (
        select user_id, contest_id
        from t_participant
        group by user_id, contest_id
        having count(*) > 1
    ) then
        raise exception 'Duplicate participant entries exist for the same user and contest';
    end if;
end
$$;

insert into t_leaderboard (
    leaderboard_id,
    contest_count,
    date_created,
    date_updated,
    version
)
select
    1,
    0,
    current_timestamp,
    current_timestamp,
    0
where not exists (select 1 from t_leaderboard);

with ranked_participants as (
    select
        p.participant_id,
        rank() over (
            partition by p.contest_id
            order by p.total_value desc
        )::int as calculated_rank
    from t_participant p
    join t_contest c on c.contest_id = p.contest_id
    where c.contest_status = 'COMPLETED'
)
update t_participant p
set
    participant_rank = ranked_participants.calculated_rank,
    date_updated = current_timestamp
from ranked_participants
where ranked_participants.participant_id = p.participant_id;

insert into t_leaderboard_entry (
    contest_count,
    ranking,
    score,
    user_id,
    leaderboard_id,
    date_created,
    date_updated,
    version
)
select
    0,
    0,
    0,
    completed_users.user_id,
    leaderboard.leaderboard_id,
    current_timestamp,
    current_timestamp,
    0
from (
    select distinct p.user_id
    from t_participant p
    join t_contest c on c.contest_id = p.contest_id
    where c.contest_status = 'COMPLETED'
) completed_users
cross join t_leaderboard leaderboard
where not exists (
    select 1
    from t_leaderboard_entry existing_entry
    where existing_entry.user_id = completed_users.user_id
);

delete from t_medal;

insert into t_medal (
    medal_value,
    position,
    contest_id,
    leaderboard_entry_id,
    date_created,
    date_updated,
    version
)
select
    case p.participant_rank
        when 1 then 'GOLD'
        when 2 then 'SILVER'
        when 3 then 'BRONZE'
    end,
    p.participant_rank,
    p.contest_id,
    le.leaderboard_entry_id,
    current_timestamp,
    current_timestamp,
    0
from t_participant p
join t_contest c on c.contest_id = p.contest_id
join t_leaderboard_entry le on le.user_id = p.user_id
where c.contest_status = 'COMPLETED'
  and p.participant_rank between 1 and 3;

update t_leaderboard_entry
set
    contest_count = 0,
    score = 0,
    date_updated = current_timestamp;

with user_results as (
    select
        p.user_id,
        count(*)::int as contest_count,
        coalesce(
            sum(
                case p.participant_rank
                    when 1 then 3
                    when 2 then 2
                    when 3 then 1
                    else 0
                end
            ),
            0
        )::int as score
    from t_participant p
    join t_contest c on c.contest_id = p.contest_id
    where c.contest_status = 'COMPLETED'
    group by p.user_id
)
update t_leaderboard_entry le
set
    contest_count = user_results.contest_count,
    score = user_results.score,
    date_updated = current_timestamp
from user_results
where user_results.user_id = le.user_id;

with ranked_entries as (
    select
        leaderboard_entry_id,
        rank() over (order by score desc)::int as calculated_ranking
    from t_leaderboard_entry
)
update t_leaderboard_entry le
set
    ranking = ranked_entries.calculated_ranking,
    date_updated = current_timestamp
from ranked_entries
where ranked_entries.leaderboard_entry_id = le.leaderboard_entry_id;

update t_leaderboard
set
    contest_count = (
        select count(*)::int
        from t_contest
        where contest_status = 'COMPLETED'
    ),
    date_updated = current_timestamp;

alter table t_leaderboard_entry
    add constraint uq_t_leaderboard_entry_user unique (user_id);

alter table t_medal
    add constraint uq_t_medal_entry_contest unique (leaderboard_entry_id, contest_id);

create index idx_t_participant_contest_total_value
    on t_participant (contest_id, total_value desc);
