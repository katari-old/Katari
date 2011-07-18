-- vim: set ts=2 et sw=2:

insert into users(name, email, password, user_type, is_active) values (
  'admin', 'admin@none', 'admin', 'user', true);
insert into roles(name) values ('ADMINISTRATOR');
insert into roles(name) values ('REPORT_ADMIN');
insert into users_roles(users_id, roles_id) select users.id, roles.id
  from users, roles where users.name='admin' and roles.name='ADMINISTRATOR';

-- Create the inital home page.
insert into pages
  (site_name, name, title, content, modifier, publication_date)
  values
  ('default', 'home','home_title',
    '<p>Welcome to <b>Katari</b>. This is the home page. It can be edited if
    you have the right privileges.</p>
    <p>Select the style of menu that you want:
    <ul>
      <li>
        <a href="home?menuType=classic">Classic menu</a>
      </li>
      <li>
        <a href="home?menuType=bar">Javascript based menu bar</a>
      </li>
      <li>
        <a href="home?menuType=context">Javascript based context-like menu</a>
      </li>
    </ul>
    ','admin', now());

-- Create a sample about page.
insert into pages(site_name, name, title, content, modifier,
  publication_date) values
  ('default', 'about','about_title', '<p>This is an about page example.</p>',
    'admin', now());

