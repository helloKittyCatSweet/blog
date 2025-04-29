insert into fs_users (username, password, email, is_active, is_deleted) values
('admin', 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWM3NzhmM2EtZjdiZS00NzAyLWExZTgtYmEwMzQzMzg5MDEwIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTg0OTg3LCJleHAiOjE3NDU1ODg1ODd9.T38lhnmvgXF8FuKXprvlnUPbhP00ZUtzgAHLI3G_dGU', 'admin@admin.com', true, false);
insert into fs_user_roles (user_id, role_id) values (1, 1);
insert into fs_user_roles (user_id, role_id) values (1, 8);