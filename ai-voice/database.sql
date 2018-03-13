/*
Navicat PGSQL Data Transfer

Source Server         : a4
Source Server Version : 90410
Source Host           : rm-uf6z38pkoozk79044eo.pg.rds.aliyuncs.com:3432
Source Database       : a4
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 90410
File Encoding         : 65001

Date: 2018-03-13 12:11:01
*/


-- ----------------------------
-- Table structure for oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS "public"."oauth_client_details";
CREATE TABLE "public"."oauth_client_details" (
"client_id" varchar(128) COLLATE "default" NOT NULL,
"resource_ids" varchar(256) COLLATE "default",
"client_secret" varchar(256) COLLATE "default",
"scope" varchar(256) COLLATE "default",
"authorized_grant_types" varchar(256) COLLATE "default",
"web_server_redirect_uri" varchar(256) COLLATE "default",
"authorities" varchar(256) COLLATE "default",
"access_token_validity" int4,
"refresh_token_validity" int4,
"additional_information" varchar(4096) COLLATE "default",
"autoapprove" varchar(256) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of oauth_client_details
-- ----------------------------
INSERT INTO "public"."oauth_client_details" VALUES ('meating', null, 'meatingsecret', 'read,write', 'authorization_code,password', 'https://open.bot.tmall.com/oauth/callback', null, null, null, null, null);

-- ----------------------------
-- Table structure for tb_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."tb_role";
CREATE TABLE "public"."tb_role" (
"id" int4 NOT NULL,
"role_name" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of tb_role
-- ----------------------------
INSERT INTO "public"."tb_role" VALUES ('1', 'ROLE_admin');

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS "public"."tb_user";
CREATE TABLE "public"."tb_user" (
"id" int4 NOT NULL,
"password" varchar(255) COLLATE "default",
"username" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO "public"."tb_user" VALUES ('1', '123', 'bob');

-- ----------------------------
-- Table structure for tb_user_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."tb_user_role";
CREATE TABLE "public"."tb_user_role" (
"id" int4 NOT NULL,
"rid" int4 NOT NULL,
"uid" int4 NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of tb_user_role
-- ----------------------------
INSERT INTO "public"."tb_user_role" VALUES ('1', '1', '1');

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table oauth_client_details
-- ----------------------------
ALTER TABLE "public"."oauth_client_details" ADD PRIMARY KEY ("client_id");

-- ----------------------------
-- Primary Key structure for table tb_role
-- ----------------------------
ALTER TABLE "public"."tb_role" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table tb_user
-- ----------------------------
ALTER TABLE "public"."tb_user" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table tb_user_role
-- ----------------------------
CREATE UNIQUE INDEX "UK_q7oskmwmmyn5usc6cqsi2g28g" ON "public"."tb_user_role" USING btree ("rid");
CREATE INDEX "FKe5x46iw9uaj34h12wkbvxj7k5" ON "public"."tb_user_role" USING btree ("uid");

-- ----------------------------
-- Primary Key structure for table tb_user_role
-- ----------------------------
ALTER TABLE "public"."tb_user_role" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Foreign Key structure for table "public"."tb_user_role"
-- ----------------------------
ALTER TABLE "public"."tb_user_role" ADD FOREIGN KEY ("uid") REFERENCES "public"."tb_user" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."tb_user_role" ADD FOREIGN KEY ("uid") REFERENCES "public"."tb_user" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."tb_user_role" ADD FOREIGN KEY ("rid") REFERENCES "public"."tb_role" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."tb_user_role" ADD FOREIGN KEY ("rid") REFERENCES "public"."tb_role" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
