alter table t_contest
    alter column start_time type timestamp(6) with time zone using start_time at time zone 'UTC',
    alter column end_time type timestamp(6) with time zone using end_time at time zone 'UTC';

alter table t_investment_order
    alter column expiration_time type timestamp(6) with time zone using expiration_time at time zone 'UTC';
