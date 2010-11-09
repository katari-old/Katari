-- vim: set ts=2 et sw=2:

insert into users(id, name, email, password, user_type, is_active) values (1, 'admin',
  'admin@none', 'admin', 'user', true);
insert into roles(id, name) values (1, 'ADMINISTRATOR');
insert into roles(id, name) values (2, 'REPORT_ADMIN');
insert into users_roles(users_id, roles_id) select users.id, roles.id
  from users, roles where users.name='admin' and roles.name='ADMINISTRATOR';

-- Create the inital home page.
insert into pages(id, site_name, name, title, content, modifier,
  publication_date) values
  (1, 'default', 'home','home_title', '<p>Welcome to <b>Katari</b>. This is
    the home page. It can be edited if you have the right
    privileges.</p>','admin', now());

-- Create a sample about page.
insert into pages(id, site_name, name, title, content, modifier, publication_date) values
  (2,'default', 'about','about_title', '<p>This is an about page example.</p>',
    'admin', now());

-- We want the autoincrement for table to start from 1000.
alter table pages auto_increment = 1000;

