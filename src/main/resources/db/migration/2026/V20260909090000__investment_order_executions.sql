create table t_investment_order_execution (
    investment_order_execution_id bigserial primary key,
    amount                        integer                     not null,
    execution_price               numeric(19, 4)              not null,
    executed_at                   timestamp(6) with time zone not null,
    investment_order_id           bigint                      not null,
    date_created                  timestamp(6)                not null,
    date_updated                  timestamp(6)                not null,
    version                       bigint,
    constraint fk_t_investment_order_execution_order
        foreign key (investment_order_id) references t_investment_order (investment_order_id) on delete cascade,
    constraint ck_t_investment_order_execution_amount_positive check (amount > 0),
    constraint ck_t_investment_order_execution_price_positive check (execution_price > 0)
);

create index idx_t_investment_order_execution_order_executed_at
    on t_investment_order_execution (investment_order_id, executed_at);
