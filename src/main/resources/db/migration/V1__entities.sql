create table t_user(
	user_id     bigint(20)  not null auto_increment,
	username    varchar(50) not null,
	password    varchar(200) not null,
	email       varchar(60) not null,
	primary key (user_id)
);

create table t_contest(
	contest_id          bigint(20)  not null auto_increment,
	contest_number      int not null,
	start_time          datetime,
	in_pre_start_mode   boolean,
	in_running_mode     boolean,
	primary key (contest_id)
);

create table t_portfolio(
	portfolio_id    bigint(20)  not null auto_increment,
	primary key (portfolio_id)
);

create table t_investment(
    investment_id       bigint(20) not null auto_increment,
    investment_name     varchar(100),
    symbol              varchar(20),
    portfolio_id        bigint(20)  not null,
    amount              int,
    sum_paid            double,
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
    expiration_time       datetime not null,
    transaction_type      varchar(20) not null,
    order_status          varchar(20) not null,
    error_message         varchar(200),
    participant_id        bigint(20)  not null,
    primary key (investment_order_id),
    foreign key (participant_id) references t_participant(participant_id)
);