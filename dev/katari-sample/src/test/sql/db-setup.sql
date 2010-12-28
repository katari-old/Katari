insert into clients(id, name, status) values ('1', 'Google', 'ACTIVE');
insert into clients(id, name, status) values ('2', 'OAG', 'INACTIVE');
insert into projects values ('1', 'Google HR', '1');
insert into projects values ('2', 'Google CRM', '1');
insert into activities values ('1', 'Coding', '1');
insert into activities values ('2', 'Testing', '1');

-- Creates 5 applications.
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
insert into supported_views(application_id, view_name) values (1, 'default');
insert into supported_views(application_id, view_name) values (1, 'home');
insert into supported_views(application_id, view_name) values (1, 'profile');
insert into supported_views(application_id, view_name) values (1, 'canvas');

insert into applications (id, title, url) values (2, 'Activities',
 'http://localhost:8098/katari-sample/module/gadget/ActivityTest.xml');
insert into supported_views(application_id, view_name) values (2, 'default');

insert into applications (id, title, url) values (3, 'ToDo',
 'http://www.labpixies.com/campaigns/todo/todo.xml');
insert into supported_views(application_id, view_name) values (3, 'default');
insert into supported_views(application_id, view_name) values (3, 'home');
insert into supported_views(application_id, view_name) values (3, 'profile');
insert into supported_views(application_id, view_name) values (3, 'canvas');

insert into applications (id, title, url) values (4, 'Flood It',
 'http://www.labpixies.com/campaigns/flood/flood.xml');
insert into supported_views(application_id, view_name) values (4, 'default');
insert into supported_views(application_id, view_name) values (4, 'home');
insert into supported_views(application_id, view_name) values (4, 'canvas');

insert into applications (id, title, icon, url) values (5, 'Chess',
 'http://ning.j2play.net/j2play-images/ImageServlet?id=7231',
 'http://ning.j2play.net/ning/web/game-200/app.xml');
insert into supported_views(application_id, view_name) values (5, 'profile');
insert into supported_views(application_id, view_name) values (5, 'canvas');
insert into supported_views(application_id, view_name) values (5, 'about');
insert into supported_views(application_id, view_name) values (5, 'ning.main');

-- Creates two gadget groups.
insert into gadget_groups (name, view_name, group_type, number_of_columns)
 values ('top', 'dashboard', 'shared', 2);
insert into gadget_instances (application_id, gadget_group_id, group_column,
 group_order) values (2, 1, 0, 0);
insert into gadget_instances (application_id, gadget_group_id, group_column,
 group_order) values (4, 1, 1, 0);

insert into gadget_groups (name, view_name, group_type, number_of_columns)
 values ('main', 'dashboard', 'template', 3);
insert into gadget_instances (application_id, gadget_group_id, group_column,
 group_order) values (1, 2, 0, 1);
insert into gadget_instances (application_id, gadget_group_id, group_column,
 group_order) values (3, 2, 1, 1);

