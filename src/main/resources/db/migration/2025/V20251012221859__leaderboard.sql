create table t_leaderboard
(
	leaderboard_id bigserial primary key,
	contest_count  int       not null,
	date_created   timestamp not null,
	date_updated   timestamp not null,
	version        bigint
);

alter table t_leaderboard_entry
	add column leaderboard_id bigint;

alter table t_leaderboard_entry
	add constraint fk_leaderboard foreign key (leaderboard_id) references t_leaderboard (leaderboard_id);