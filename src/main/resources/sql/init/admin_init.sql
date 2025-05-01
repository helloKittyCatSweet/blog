insert into fs_users (username, password, email, is_active, is_deleted) values
('admin', '$2a$10$3Q83Gv7.8IjPaQzaS0cC9ez4CSnD9123O8oPn.GL3cTjJTeSb.HDK', 'admin@admin.com', true, false);
insert into fs_user_roles (user_id, role_id) values (1, 1);
insert into fs_user_roles (user_id, role_id) values (1, 8);
update fs_roles set count = 1 where id = 1;
update fs_roles set count = 1 where id = 8;