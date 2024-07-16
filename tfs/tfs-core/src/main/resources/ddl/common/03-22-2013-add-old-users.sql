INSERT INTO sec_user (ID) VALUES ('branchm');
INSERT INTO sec_employee (ID, firstname, lastname, fullname, unitcode) VALUES ('branchm', 'Maker', 'Branch', 'Maker Branch', '001');
INSERT INTO sec_user_roles (userid, roleId) VALUES ('branchm', 'BRM');

INSERT INTO sec_user (ID) VALUES ('branchc');
INSERT INTO sec_employee (ID, firstname, lastname, fullname, unitcode) VALUES ('branchc', 'Checker', 'Branch', 'Check Branch', '001');
INSERT INTO sec_user_roles (userid, roleId) VALUES ('branchc', 'BRO');

INSERT INTO sec_user (ID) VALUES ('brancha');
INSERT INTO sec_employee (ID, firstname, lastname, fullname, unitcode) VALUES ('brancha', 'Approver', 'Branch', 'Approver Branch', '001');
INSERT INTO sec_user_roles (userid, roleId) VALUES ('brancha', 'BRO');

INSERT INTO sec_user (ID) VALUES ('mmaneses');
INSERT INTO sec_employee (ID, firstname, lastname, fullname, unitcode, post_auth, limit, level) VALUES ('mmaneses', 'Maryel', 'Maneses', 'Maryel Meneses', '909', 0, 0, 1);
INSERT INTO sec_user_roles (userid, roleId) VALUES ('mmaneses', 'TSDM');

INSERT INTO sec_user (ID) VALUES ('mcueto');
INSERT INTO sec_employee (ID, firstname, lastname, fullname, unitcode, post_auth, limit, level) VALUES ('mcueto', 'Marvin', 'Cueto', 'Marvin Cueto', '909', 0, 0, 1);
INSERT INTO sec_user_roles (userid, roleId) VALUES ('mcueto', 'TSDM');

INSERT INTO sec_user (ID) VALUES ('gpadilla');
INSERT INTO sec_employee (ID, firstname, lastname, fullname, unitcode, post_auth, limit, level) VALUES ('gpadilla', 'Genielou', 'Padilla', 'Genielou Padilla', '909', 0, 0, 1);
INSERT INTO sec_user_roles (userid, roleId) VALUES ('gpadilla', 'TSDM');

INSERT INTO sec_user (ID) VALUES ('jmisterio');
INSERT INTO sec_employee (ID, firstname, lastname, fullname, unitcode, post_auth, limit, level) VALUES ('jmisterio', 'Julie Anne', 'Misterio', 'Julie Anne Misterio', '909', 0, 0, 1);
INSERT INTO sec_user_roles (userid, roleId) VALUES ('jmisterio', 'TSDM');

INSERT INTO sec_user (ID) VALUES ('lmejos');
INSERT INTO sec_employee (ID, firstname, lastname, fullname, unitcode, post_auth, limit, level) VALUES ('lmejos', 'Leticia', 'Mejos', 'Leticia Mejos', '909', 1, 20000000, 10);
INSERT INTO sec_user_roles (userid, roleId) VALUES ('lmejos', 'TSDO');

INSERT INTO sec_user (ID) VALUES ('jbabate');
INSERT INTO sec_employee (ID, firstname, lastname, fullname, unitcode, post_auth, limit, level) VALUES ('jbabate', 'Justiniano', 'Babate', 'Justiniano Babate', '909', 1, 49999999, 20);
INSERT INTO sec_user_roles (userid, roleId) VALUES ('jbabate', 'TSDO');

INSERT INTO sec_user (ID) VALUES ('avalles');
INSERT INTO sec_employee (ID, firstname, lastname, fullname, unitcode, post_auth, limit, level) VALUES ('avalles', 'Arnel', 'Valles', 'Arnel Valles', '909', 1, 49999999, 40);
INSERT INTO sec_user_roles (userid, roleId) VALUES ('avalles', 'TSDO');

INSERT INTO sec_user (ID) VALUES ('esamonte');
INSERT INTO sec_employee (ID, firstname, lastname, fullname, unitcode, post_auth, limit, level) VALUES ('esamonte', 'Evangeline', 'Samonte', 'Evangeline Samonte', '909', 1, 999999999999, 50);
INSERT INTO sec_user_roles (userid, roleId) VALUES ('esamonte', 'TSDO');