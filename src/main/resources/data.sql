drop table if exists credentials;

CREATE TABLE if not exists credentials (username TEXT, password TEXT, server TEXT );

insert into CREDENTIALS (username, password, server) values ('tempUSER', 'tempPassword', 'tempURL');
