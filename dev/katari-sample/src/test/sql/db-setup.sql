insert into clients(id, name, status) values ('1', 'Google', 'ACTIVE');
insert into clients(id, name, status) values ('2', 'OAG', 'INACTIVE');
insert into projects values ('1', 'Google HR', '1');
insert into projects values ('2', 'Google CRM', '1');
insert into activities values ('1', 'Coding', '1');
insert into activities values ('2', 'Testing', '1');

-- Creates 4 applications.
insert into applications (id, title, description, author, icon, thumbnail, url)
values (1, 'Minesweeper',
  'Minesweeper, the classic game that will sweep you
  off your feet! The objective is to locate and flag all the mine cells
  as fast as possible, without detonating the minefield. Left-click the
  cells in order to uncover them. Every non-mine cell you uncover will
  have a number indicating how many mines are in its eight neighboring
  cells. If you uncover a mine, the minefield will be detonated, thus you
  lose. Right-click on a cell in order to flag it. Right-click on a cell
  twice if you are uncertain there is a mine underneath - the cell will
  then show a question mark, which will serve you as a reminder when you
  return to the suspected cell later on. Tip: double-click an uncovered
  non-mine cell if all the mines surrounding it are flagged - this will
  reveal all the remaining covered non-mine cells surrounding the
  double-clicked cell as if you clicked each one of them separately.
  Saving clicks - hence saving time. Once all the mine cells are flagged
  you win the game. Sweep carefully, and try not to blow it.',
  'LabPixies', null,
 'http://www.labpixies.com/campaigns/minesweeper/images/thumbnail.jpg',
 'http://www.labpixies.com/campaigns/minesweeper/minesweeper.xml');
insert into applications (id, title, url) values (2, 'Activities',
 'http://localhost:8098/katari-sample/module/gadget/ActivityTest.xml');
insert into applications (id, title, url) values (3, 'ToDo',
 'http://www.labpixies.com/campaigns/todo/todo.xml');
insert into applications (id, title, url) values (4, 'Flood It',
 'http://www.labpixies.com/campaigns/flood/flood.xml');

-- Creates two gadget groups.
insert into gadget_groups (name, type, number_of_columns)
 values ('top', 0, 2);
insert into gadget_instances (application_id, gadget_group_id, group_column,
 group_order) values (1, 1, 0, 0);
insert into gadget_instances (application_id, gadget_group_id, group_column,
 group_order) values (2, 1, 1, 0);

insert into gadget_groups (owner_id, name, type, number_of_columns)
 values (1, 'main', 1, 2);
insert into gadget_instances (application_id, gadget_group_id, group_column,
 group_order) values (3, 2, 0, 1);
insert into gadget_instances (application_id, gadget_group_id, group_column,
 group_order) values (4, 2, 1, 1);

