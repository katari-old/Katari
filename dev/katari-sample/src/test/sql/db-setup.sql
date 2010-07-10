insert into clients(id, name, status) values ('1', 'Google', 'ACTIVE');
insert into clients(id, name, status) values ('2', 'OAG', 'INACTIVE');
insert into projects values ('1', 'Google HR', '1');
insert into projects values ('2', 'Google CRM', '1');
insert into activities values ('1', 'Coding', '1');
insert into activities values ('2', 'Testing', '1');

-- Adds 3 sample gadgets to the dashboard page.
insert into gadget_groups (owner_id, name, number_of_columns)
 values (1, 'main', 3);
insert into gadget_instances (gadget_position, url, gadget_group_id)
 values ('1#1',
 'http://www.labpixies.com/campaigns/minesweeper/minesweeper.xml', 1);
insert into gadget_instances (gadget_position, url, gadget_group_id)
 values ('2#1',
 'http://www.labpixies.com/campaigns/minesweeper/minesweeper.xml', 1);
insert into gadget_instances (gadget_position, url, gadget_group_id)
 values ('1#2',
 'http://www.labpixies.com/campaigns/minesweeper/minesweeper.xml', 1);

