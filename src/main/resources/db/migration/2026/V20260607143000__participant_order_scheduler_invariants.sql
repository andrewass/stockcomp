alter table t_participant
    add constraint uq_t_participant_user_contest unique (user_id, contest_id),
    add constraint ck_t_participant_remaining_funds_non_negative check (remaining_funds >= 0),
    add constraint ck_t_participant_total_value_non_negative check (total_value >= 0),
    add constraint ck_t_participant_total_investment_value_non_negative check (total_investment_value >= 0);

alter table t_investment
    alter column symbol set not null,
    alter column amount set not null,
    alter column average_unit_cost set not null,
    alter column total_profit set not null,
    alter column total_value set not null,
    add constraint ck_t_investment_amount_positive check (amount > 0),
    add constraint ck_t_investment_average_unit_cost_non_negative check (average_unit_cost >= 0),
    add constraint ck_t_investment_total_value_non_negative check (total_value >= 0);

alter table t_investment_order
    add constraint ck_t_investment_order_total_amount_positive check (total_amount > 0),
    add constraint ck_t_investment_order_remaining_amount_non_negative check (remaining_amount >= 0),
    add constraint ck_t_investment_order_remaining_amount_not_above_total check (remaining_amount <= total_amount),
    add constraint ck_t_investment_order_accepted_price_positive check (accepted_price > 0);

alter table t_leaderboard_job
    add constraint ck_t_leaderboard_job_attempts_non_negative check (attempts >= 0);

create index idx_t_contest_status
    on t_contest (contest_status);

create index idx_t_participant_contest_rank
    on t_participant (contest_id, participant_rank);

create index idx_t_leaderboard_entry_user_id
    on t_leaderboard_entry (user_id);

create index idx_t_leaderboard_entry_leaderboard_id
    on t_leaderboard_entry (leaderboard_id);

create index idx_t_leaderboard_entry_score
    on t_leaderboard_entry (score);

create index idx_t_medal_contest_id
    on t_medal (contest_id);

create index idx_t_medal_leaderboard_entry_id
    on t_medal (leaderboard_entry_id);

create index idx_t_investment_participant_symbol
    on t_investment (participant_id, symbol);

create index idx_t_investment_order_participant_status_symbol
    on t_investment_order (participant_id, order_status, symbol);

create index idx_t_refresh_token_user_id
    on t_refresh_token (user_id);

create index idx_t_user_subject_external_subject_valid
    on t_user_subject (external_subject_id, is_valid);

create index idx_t_leaderboard_job_contest_id
    on t_leaderboard_job (contest_id);
