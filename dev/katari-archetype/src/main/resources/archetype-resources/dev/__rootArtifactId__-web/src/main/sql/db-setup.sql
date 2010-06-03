-- vim: set ts=2 et sw=2:

insert into users(id, name, email, password) values (1, 'admin',
  'admin@none', 'admin');
insert into roles(id, name) values (1, 'ADMINISTRATOR');
insert into roles(id, name) values (2, 'REPORT_ADMIN');
insert into users_roles(users_id, roles_id) select users.id, roles.id
  from users, roles where users.name='admin' and roles.name='ADMINISTRATOR';

-- Create the inital home page.
insert into pages(id, modifier, name, publication_date, title, content,
  site_name) values
  (1, 'admin', 'home', now(), 'home', '<p>Welcome to <b>${friendlyName}</b>.
    This is the home page. It can be edited if you have the right
    privileges.</p>', 'default');

-- Create a sample about page.
insert into pages(id, modifier, name, publication_date, title, content,
  site_name) values
  (2, 'admin', 'about', now(), 'about', '<p>This is an about page
    example.</p>', 'default');

-- We want the autoincrement for table to start from 1000.
alter table pages auto_increment = 1000;

