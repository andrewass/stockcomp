create table t_user(
	user_id         bigint(20)  not null auto_increment,
	username        varchar(50) not null,
	password        varchar(200) not null,
	email           varchar(60) not null,
	date_created    datetime not null,
	date_updated    datetime not null,
	user_role       varchar(10) not null,
	primary key (user_id)
);

create table t_contest(
	contest_id          bigint(20)  not null auto_increment,
	contest_number      int not null,
	start_time          datetime,
	completed           boolean,
	running             boolean,
	date_created    	datetime not null,
	date_updated    	datetime not null,
	primary key (contest_id)
);

create table t_portfolio(
	portfolio_id    bigint(20)  not null auto_increment,
	date_created    datetime not null,
	date_updated    datetime not null,
	primary key (portfolio_id)
);

create table t_investment(
    investment_id           bigint(20) not null auto_increment,
    investment_name         varchar(100),
    symbol                  varchar(20),
    portfolio_id            bigint(20)  not null,
    amount                  int,
    average_unit_cost       double,
    total_profit            double,
    date_created    		datetime not null,
    date_updated    		datetime not null,
    primary key (investment_id),
    foreign key (portfolio_id) references t_portfolio(portfolio_id)
);

create table t_participant(
    participant_id      bigint(20) not null auto_increment,
    contest_id          bigint(20) not null,
    user_id             bigint(20) not null,
    portfolio_id        bigint(20) not null,
    remaining_fund      double,
    participant_rank    int,
    participant_score   int,
    date_created    	datetime not null,
    date_updated    	datetime not null,
    primary key (participant_id),
    foreign key (contest_id) references t_contest(contest_id),
    foreign key (user_id) references t_user(user_id),
    foreign key (portfolio_id) references t_portfolio(portfolio_id)
);

create table t_investment_order(
    investment_order_id   bigint(20) not null auto_increment,
    symbol                varchar(20) not null,
    total_amount          int not null,
    remaining_amount      int not null,
    accepted_price        double not null,
    currency              varchar(10) not null,
    expiration_time       datetime not null,
    transaction_type      varchar(20) not null,
    order_status          varchar(20) not null,
    error_message         varchar(200),
    participant_id        bigint(20)  not null,
    date_created    	datetime not null,
    date_updated    	datetime not null,
    primary key (investment_order_id),
    foreign key (participant_id) references t_participant(participant_id)
);

create table t_refresh_token(
    refresh_token_id    bigint(20) not null auto_increment,
    token               varchar(100),
    user_id             bigint(20) not null,
    expiration_time     datetime not null,
    date_created    	datetime not null,
    date_updated    	datetime not null,
    primary key (refresh_token_id),
    foreign key (user_id) references t_user(user_id)
);