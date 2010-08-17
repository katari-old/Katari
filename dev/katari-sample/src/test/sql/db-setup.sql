insert into clients(id, name, status) values ('1', 'Google', 'ACTIVE');
insert into clients(id, name, status) values ('2', 'OAG', 'INACTIVE');
insert into projects values ('1', 'Google HR', '1');
insert into projects values ('2', 'Google CRM', '1');
insert into activities values ('1', 'Coding', '1');
insert into activities values ('2', 'Testing', '1');

-- Creates 4 applications.
insert into applications (id, title, url) values (1, 'Minesweeper',
 'http://www.labpixies.com/campaigns/minesweeper/minesweeper.xml');
insert into applications (id, title, url) values (2, 'Activities',
 'http://localhost:8098/katari-sample/module/gadget/ActivityTest.xml');
insert into applications (id, title, url) values (3, 'ToDo',
 'http://www.labpixies.com/campaigns/todo/todo.xml');
insert into applications (id, title, url) values (4, 'Flood It',
 'http://www.labpixies.com/campaigns/flood/flood.xml');

-- Creates two gadget groups.
insert into gadget_groups (name, number_of_columns)
 values ('top', 2);
insert into gadget_instances (application_id, gadget_group_id, group_column,
 group_order) values (1, 1, 0, 0);
insert into gadget_instances (application_id, gadget_group_id, group_column,
 group_order) values (2, 1, 1, 0);

insert into gadget_groups (owner_id, name, number_of_columns)
 values (1, 'main', 2);
insert into gadget_instances (application_id, gadget_group_id, group_column,
 group_order) values (3, 2, 0, 1);
insert into gadget_instances (application_id, gadget_group_id, group_column,
 group_order) values (4, 2, 1, 1);

