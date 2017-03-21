CREATE TABLE users (
  username VARCHAR(45) NOT NULL,
  password VARCHAR(45) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  accountNonExpired BOOLEAN NOT NULL DEFAULT TRUE,
  accountNonLocked BOOLEAN NOT NULL DEFAULT TRUE,
  credentialsNonExpired BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (username)
);

CREATE TABLE user_roles (
  user_role_id int(11) NOT NULL AUTO_INCREMENT,
  username varchar(45) NOT NULL,
  role varchar(45) NOT NULL,
  PRIMARY KEY (user_role_id),
  UNIQUE KEY uni_username_role (role,username),
  KEY fk_username_idx (username),
  CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES users (username)
);

CREATE TABLE user_attempts (
  id int(11) NOT NULL AUTO_INCREMENT,
  username varchar(45) NOT NULL,
  attempts varchar(45) NOT NULL,
  lastModified datetime,
  PRIMARY KEY (id)
);

INSERT INTO users(username,password,enabled) VALUES ('hxf','123456', true);
INSERT INTO users(username,password,enabled) VALUES ('wpp','123456', true);

INSERT INTO user_roles (username, role) VALUES ('hxf', 'ROLE_USER');
INSERT INTO user_roles (username, role) VALUES ('hxf', 'ROLE_ADMIN');
INSERT INTO user_roles (username, role) VALUES ('wpp', 'ROLE_USER');