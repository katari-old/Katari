-- Creates 5 applications.
insert into applications (title, description, author, icon, thumbnail, url)
values ('Minesweeper',
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
insert into supported_views(application_id, view_name)
  select applications.id, 'default' from applications
    where applications.title = 'Minesweeper';
insert into supported_views(application_id, view_name)
  select applications.id, 'home' from applications
    where applications.title = 'Minesweeper';
insert into supported_views(application_id, view_name)
  select applications.id, 'profile' from applications
    where applications.title = 'Minesweeper';
insert into supported_views(application_id, view_name)
  select applications.id, 'canvas' from applications
    where applications.title = 'Minesweeper';

insert into applications (title, url) values ('Activities',
 'http://localhost:8098/katari-sample/module/gadget/ActivityTest.xml');
insert into supported_views(application_id, view_name)
  select applications.id, 'default' from applications
    where applications.title = 'Activities';

insert into applications (title, url) values ('ToDo',
 'http://www.labpixies.com/campaigns/todo/todo.xml');
insert into supported_views(application_id, view_name)
  select applications.id, 'default'
    from applications where applications.title = 'ToDo';
insert into supported_views(application_id, view_name)
  select applications.id, 'home'
    from applications where applications.title = 'ToDo';
insert into supported_views(application_id, view_name)
  select applications.id, 'profile'
    from applications where applications.title = 'ToDo';
insert into supported_views(application_id, view_name)
  select applications.id, 'canvas'
    from applications where applications.title = 'ToDo';

insert into applications (title, url) values ('Flood It',
 'http://www.labpixies.com/campaigns/flood/flood.xml');
insert into supported_views(application_id, view_name)
  select applications.id, 'default'
    from applications where applications.title = 'Flood It';
insert into supported_views(application_id, view_name)
  select applications.id, 'home'
    from applications where applications.title = 'Flood It';
insert into supported_views(application_id, view_name)
  select applications.id, 'canvas'
    from applications where applications.title = 'Flood It';

insert into applications (title, icon, url) values ('Chess',
 'http://ning.j2play.net/j2play-images/ImageServlet?id=7231',
 'http://ning.j2play.net/ning/web/game-200/app.xml');
insert into supported_views(application_id, view_name)
  select applications.id, 'default'
    from applications where applications.title = 'Chess';
insert into supported_views(application_id, view_name)
  select applications.id, 'home'
    from applications where applications.title = 'Chess';
insert into supported_views(application_id, view_name)
  select applications.id, 'canvas'
    from applications where applications.title = 'Chess';
insert into supported_views(application_id, view_name)
  select applications.id, 'ning.main'
    from applications where applications.title = 'Chess';

-- Creates two gadget groups.
insert into gadget_groups (name, view_name, group_type, number_of_columns)
  values ('top', 'dashboard', 'shared', 2);
insert into gadget_instances (application_id, gadget_group_id, group_column,
  group_order) select applications.id, gadget_groups.id, 0, 0
  from applications, gadget_groups
  where applications.title = 'ToDo' and gadget_groups.name = 'top';

insert into gadget_instances (application_id, gadget_group_id, group_column,
 group_order) select applications.id, gadget_groups.id, 1, 0
  from applications, gadget_groups
  where applications.title = 'Chess' and gadget_groups.name = 'top';

insert into gadget_groups (name, view_name, group_type, number_of_columns)
 values ('main', 'dashboard', 'template', 3);
insert into gadget_instances (application_id, gadget_group_id, group_column,
 group_order)
 select applications.id, gadget_groups.id, 0, 1
  from applications, gadget_groups
  where applications.title = 'Activities' and gadget_groups.name = 'main';
insert into gadget_instances (application_id, gadget_group_id, group_column,
 group_order)
 select applications.id, gadget_groups.id, 0, 1
  from applications, gadget_groups
  where applications.title = 'Flood It' and gadget_groups.name = 'main';

