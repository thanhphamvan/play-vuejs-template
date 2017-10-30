# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table account (
  id                            bigint auto_increment not null,
  email                         varchar(255) not null,
  password                      varbinary(64) not null,
  full_name                     varchar(255) not null,
  constraint uq_account_email unique (email),
  constraint pk_account primary key (id)
);


# --- !Downs

drop table if exists account;

