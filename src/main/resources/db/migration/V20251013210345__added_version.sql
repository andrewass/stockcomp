alter table t_user
	add column version bigint;

alter table t_leaderboard_entry
	add column version bigint;

alter table t_contest
	add column version bigint;

alter table t_participant
	add column version bigint;

alter table t_investment
	add column version bigint;

alter table t_investment_order
	add column version bigint;

alter table t_medal
	add column version bigint;
