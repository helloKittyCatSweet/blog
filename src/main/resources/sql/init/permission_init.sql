-- the permissions of table 'category'
INSERT INTO permissions (name, description) VALUES ('创建分类', '用户可以创建新的分类');
INSERT INTO permissions (name, description) VALUES ('编辑分类', '用户可以编辑自己的分类');
INSERT INTO permissions (name, description) VALUES ('查看分类', '用户可以查看所有人创造的分类');

-- the permissions of table 'tag'
INSERT INTO permissions (name, description) VALUES ('创建标签', '用户可以创建新的标签');
INSERT INTO permissions (name, description) VALUES ('编辑标签', '用户可以编辑自己的标签');
INSERT INTO permissions (name, description) VALUES ('查看标签', '用户可以查看所有人的标签');

-- the permissions of table 'userActivity'
INSERT INTO permissions (name, description) VALUES ('创建用户活动', '用户可以创建自己的活动');
INSERT INTO permissions (name, description) VALUES ('查看用户活动', '用户可以查看自己的活动');

-- the permissions of table 'userRole'
INSERT INTO permissions (name, description) VALUES ('查看用户所属的角色', '用户可以查看用户所属的角色');

-- the permissions of table 'comment'
INSERT INTO permissions (name, description) VALUES ('查看评论', '用户可以查看自己的评论');
INSERT INTO permissions (name, description) VALUES ('编辑评论', '用户可以编辑自己的评论');
INSERT INTO permissions (name, description) VALUES ('查看所有人的评论', '用户可以查看所有人的评论');
INSERT INTO permissions (name, description) VALUES ('删除发表的评论', '用户可以删除自己发表的评论');
INSERT INTO permissions (name, description) VALUES ('删除文章评论', '用户可以删除自己文章下的评论');

-- the permissions of table 'favorite'
INSERT INTO permissions (name, description) VALUES ('创建收藏', '用户可以创建自己的收藏');
INSERT INTO permissions (name, description) VALUES ('编辑收藏', '用户可以编辑自己的收藏');
INSERT INTO permissions (name, description) VALUES ('查看收藏', '用户可以查看自己的收藏');
INSERT INTO permissions (name, description) VALUES ('删除收藏', '用户可以删除自己的收藏');

-- the permissions of table 'message'
INSERT INTO permissions (name, description) VALUES ('创建消息', '用户可以创建自己的消息');
INSERT INTO permissions (name, description) VALUES ('编辑消息', '用户可以编辑自己的消息');
INSERT INTO permissions (name, description) VALUES ('查看消息', '用户可以查看自己的消息');
INSERT INTO permissions (name, description) VALUES ('删除消息', '用户可以删除自己的消息');

-- the permissions of table 'postAttachment'
INSERT INTO permissions (name, description) VALUES ('创建附件', '用户可以创建自己的附件');
INSERT INTO permissions (name, description) VALUES ('编辑附件', '用户可以编辑自己的附件');
INSERT INTO permissions (name, description) VALUES ('查看附件', '用户可以删除查看所有人的附件');
INSERT INTO permissions (name, description) VALUES ('删除附件', '用户可以删除自己的附件');

-- the permissions of table 'postVersion'
INSERT INTO permissions (name, description) VALUES ('创建版本', '用户可以创建自己的版本');
INSERT INTO permissions (name, description) VALUES ('编辑版本', '用户可以编辑自己的版本');
INSERT INTO permissions (name, description) VALUES ('查看版本', '用户可以查看自己的版本');
INSERT INTO permissions (name, description) VALUES ('删除版本', '用户可以删除自己的版本');

-- the permissions of table 'report'
INSERT INTO permissions (name, description) VALUES ('创建举报', '用户可以创建自己的举报');
INSERT INTO permissions (name, description) VALUES ('编辑举报', '用户可以编辑自己的举报');
INSERT INTO permissions (name, description) VALUES ('查看举报', '用户可以查看自己的举报');
INSERT INTO permissions (name, description) VALUES ('删除举报', '用户可以删除自己的举报');

-- the permissions of table 'user'
INSERT INTO permissions (name, description) VALUES ('创建用户', '用户可以创建自己的信息');
INSERT INTO permissions (name, description) VALUES ('编辑用户', '用户可以编辑自己的信息');
INSERT INTO permissions (name, description) VALUES ('查看所有用户', '用户可以查看所有人的信息');
INSERT INTO permissions (name, description) VALUES ('删除用户', '用户可以删除自己的信息');


-- admin permissions
-- the permissions of table 'category'
INSERT INTO permissions (name, description) VALUES ('删除分类', '用户可以删除分类');

-- the permissions of table 'rolepermissions'
INSERT INTO permissions (name, description) VALUES ('创建角色所包含的权限', '用户可以创建角色所包含的权限');
INSERT INTO permissions (name, description) VALUES ('编辑角色所包含的权限', '用户可以编辑角色所包含的权限');
INSERT INTO permissions (name, description) VALUES ('查看角色所包含的权限', '用户可以查看角色所包含的权限');
INSERT INTO permissions (name, description) VALUES ('删除角色所包含的权限', '用户可以删除角色所包含的权限');

-- the permissions of table 'tag'
INSERT INTO permissions (name, description) VALUES ('删除所有标签', '用户可以删除所有标签');

-- the permissions of table 'userActivity'
INSERT INTO permissions (name, description) VALUES ('编辑用户活动', '用户可以编辑自己的活动');
INSERT INTO permissions (name, description) VALUES ('删除用户活动', '用户可以删除自己的活动');

-- the permissions of table 'userRole'
INSERT INTO permissions (name, description) VALUES ('创建用户所属的角色', '用户可以创建用户所属的角色');
INSERT INTO permissions (name, description) VALUES ('编辑用户所属的角色', '用户可以编辑用户所属的角色');
INSERT INTO permissions (name, description) VALUES ('删除用户所属的角色', '用户可以删除用户所属的角色');

-- the permissions of table 'comment'
INSERT INTO permissions (name, description) VALUES ('编辑所有评论', '用户可以编辑所有人的评论');
INSERT INTO permissions (name, description) VALUES ('删除所有评论', '用户可以删除所有人的评论');

-- the permissions of table 'message'
INSERT INTO permissions (name, description) VALUES ('删除所有消息', '用户可以删除所有人的消息');

-- the permissions of table 'permissions'
INSERT INTO permissions (name, description) VALUES ('创建权限', '用户可以创建新的权限');
INSERT INTO permissions (name, description) VALUES ('编辑权限', '用户可以编辑自己的权限');
INSERT INTO permissions (name, description) VALUES ('查看权限', '用户可以查看自己的权限');
INSERT INTO permissions (name, description) VALUES ('删除权限', '用户可以删除自己的权限');

-- the permissions of table 'report'
INSERT INTO permissions (name, description) VALUES ('审核举报', '审核用户的举报');
INSERT INTO permissions (name, description) VALUES ('删除所有人的举报', '用户可以删除所有人的举报');

-- the permissions of table 'role'
INSERT INTO permissions (name, description) VALUES ('创建角色', '用户可以创建新的角色');
INSERT INTO permissions (name, description) VALUES ('编辑角色', '用户可以编辑自己的角色');
INSERT INTO permissions (name, description) VALUES ('查看角色', '用户可以查看自己的角色');
INSERT INTO permissions (name, description) VALUES ('删除角色', '用户可以删除自己的角色');

-- the permissions of table 'user'
INSERT INTO permissions (name, description) VALUES ('删除所有用户', '用户可以删除自己的信息');
INSERT INTO permissions (name, description) VALUES ('是否激活账户', '用户可以激活的账户');