create table t_user(
	user_id     bigint(20)  not null auto_increment,
	username    varchar(50) not null,
	password    varchar(200) not null,
	email       varchar(60) not null,
	primary key (user_id)
);