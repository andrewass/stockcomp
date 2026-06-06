alter table t_participant
    alter column remaining_funds type numeric(19, 4) using remaining_funds::numeric(19, 4),
    alter column total_value type numeric(19, 4) using total_value::numeric(19, 4),
    alter column total_investment_value type numeric(19, 4) using total_investment_value::numeric(19, 4);

alter table t_investment
    alter column average_unit_cost type numeric(19, 4) using average_unit_cost::numeric(19, 4),
    alter column total_profit type numeric(19, 4) using total_profit::numeric(19, 4),
    alter column total_value type numeric(19, 4) using total_value::numeric(19, 4);

alter table t_investment_order
    alter column accepted_price type numeric(19, 4) using accepted_price::numeric(19, 4);
