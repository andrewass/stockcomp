create table t_user(
	user_id         bigserial  primary key ,
	username        varchar(50) unique not null,
	password        varchar(200) not null,
	email           varchar(60) unique not null,
	date_created    timestamp not null,
	date_updated    timestamp not null,
	user_role       varchar(10) not null
);

create table t_contest(
	contest_id          bigserial primary key,
	contest_number      int not null,
	start_time          timestamp,
	completed           boolean,
	running             boolean,
	date_created    	timestamp not null,
	date_updated    	timestamp not null
);

create table t_participant(
    participant_id      bigserial primary key,
    contest_id          bigserial not null,
    user_id             bigserial not null,
    remaining_fund      double precision,
    participant_rank    int,
    participant_score   int,
    date_created    	timestamp not null,
    date_updated    	timestamp not null,
    foreign key (contest_id) references t_contest(contest_id),
    foreign key (user_id) references t_user(user_id)
);

create table t_investment(
    investment_id           bigserial primary key,
    symbol                  varchar(20),
    amount                  int,
    average_unit_cost       double precision,
    total_profit            double precision,
    total_value             double precision,
    participant_id          bigserial not null,
    date_created    		timestamp not null,
    date_updated    		timestamp not null,
    foreign key (participant_id) references t_participant(participant_id)
);

create table t_investment_order(
    investment_order_id   bigserial primary key ,
    symbol                varchar(20) not null,
    total_amount          int not null,
    remaining_amount      int not null,
    accepted_price        double precision not null,
    currency              varchar(10) not null,
    expiration_time       timestamp not null,
    transaction_type      varchar(20) not null,
    order_status          varchar(20) not null,
    error_message         varchar(200),
    participant_id        bigserial  not null,
    date_created    	  timestamp not null,
    date_updated    	  timestamp not null,
    foreign key (participant_id) references t_participant(participant_id)
);

create table t_refresh_token(
    refresh_token_id    bigserial primary key ,
    token               varchar(100),
    user_id             bigserial not null,
    expiration_time     timestamp not null,
    date_created    	timestamp not null,
    date_updated    	timestamp not null,
    foreign key (user_id) references t_user(user_id)
);