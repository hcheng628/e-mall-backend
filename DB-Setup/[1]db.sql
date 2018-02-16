DELETE FROM mysql.user WHERE user = '';

flush privileges;

INSERT INTO mysql.user(Host, User, Password) VALUES ('localhost', "mmallworker", password('abc123'));

SET PASSWORD FOR root@localhost=password('abc123');
SET PASSWORD FOR root@localhost=password('abc123');
SET PASSWORD FOR root@'miwifi-r3-srv'=password('abc123');


