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

create table t_participant(
    participant_id      bigint(20) not null auto_increment,
    contest_id          bigint(20) not null,
    user_id             bigint(20) not null,
    portfolio_id        bigint(20) not null,
    primary key (participant_id),
    foreign key (contest_id) references t_contest(contest_id),
    foreign key (user_id) references t_user(user_id),
    foreign key (portfolio_id) references t_portfolio(portfolio_id)
);