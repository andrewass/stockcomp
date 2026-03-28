create unique index if not exists uq_t_leaderboard_job_open_per_contest
    on t_leaderboard_job (contest_id)
    where job_status in ('CREATED', 'FAILED');

create index if not exists idx_t_leaderboard_job_status_next_run_at
    on t_leaderboard_job (job_status, next_run_at);
