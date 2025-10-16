create table t_leaderboard_job
(
	leaderboard_job_id bigserial primary key,
	contest_id         bigint      not null,
	job_status         varchar(20) not null,
	next_run_at        timestamp   not null,
	attempts           bigint      not null,
	date_created       timestamp   not null,
	date_updated       timestamp   not null,
	version            bigint      not null
);