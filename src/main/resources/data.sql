drop table if exists credentials;

CREATE TABLE if not exists credentials (username TEXT, password TEXT, server TEXT, agent TEXT);

insert into CREDENTIALS (username, password, server, agent) values ('tempUSER', 'tempPassword', 'tempURL' ,'tempAgent');
