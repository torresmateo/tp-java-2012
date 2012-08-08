/*
 Navicat Premium Data Transfer

 Source Server         : LocalHost
 Source Server Type    : PostgreSQL
 Source Server Version : 90104
 Source Host           : localhost
 Source Database       : tpjava
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 90104
 File Encoding         : utf-8

 Date: 08/06/2012 19:22:57 PM
*/

-- ----------------------------
--  Sequence structure for "bitacora_servicios_id_bitacora_servicios_seq"
-- ----------------------------
DROP SEQUENCE IF EXISTS "bitacora_servicios_id_bitacora_servicios_seq";
CREATE SEQUENCE "bitacora_servicios_id_bitacora_servicios_seq" INCREMENT 1 START 1 MAXVALUE 9223372036854775807 MINVALUE 1 CACHE 1;
ALTER TABLE "bitacora_servicios_id_bitacora_servicios_seq" OWNER TO "postgres";

-- ----------------------------
--  Sequence structure for "sys_vars_id_sys_vars_seq"
-- ----------------------------
DROP SEQUENCE IF EXISTS "sys_vars_id_sys_vars_seq";
CREATE SEQUENCE "sys_vars_id_sys_vars_seq" INCREMENT 1 START 1 MAXVALUE 9223372036854775807 MINVALUE 1 CACHE 1;
ALTER TABLE "sys_vars_id_sys_vars_seq" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "sys_vars"
-- ----------------------------
DROP TABLE IF EXISTS "sys_vars";
CREATE TABLE "sys_vars" (
	"id_sys_vars" int8 NOT NULL DEFAULT nextval('sys_vars_id_sys_vars_seq'::regclass),
	"name" varchar NOT NULL,
	"value" varchar NOT NULL
)
WITH (OIDS=FALSE);
ALTER TABLE "sys_vars" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "bitacora_servicios"
-- ----------------------------
DROP TABLE IF EXISTS "bitacora_servicios";
CREATE TABLE "bitacora_servicios" (
	"id_bitacora_servicios" int8 NOT NULL DEFAULT nextval('bitacora_servicios_id_bitacora_servicios_seq'::regclass),
	"alias" varchar(30),
	"direccion_ip" varchar(40) NOT NULL,
	"puerto" int4 NOT NULL,
	"email" varchar(250) NOT NULL,
	"estado" varchar(10) DEFAULT 'N/A'::character varying,
	"marca_tiempo" timestamp(6) NULL DEFAULT now()
)
WITH (OIDS=FALSE);
ALTER TABLE "bitacora_servicios" OWNER TO "postgres";


-- ----------------------------
--  Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "bitacora_servicios_id_bitacora_servicios_seq" OWNED BY "bitacora_servicios"."id_bitacora_servicios";
ALTER SEQUENCE "sys_vars_id_sys_vars_seq" OWNED BY "sys_vars"."id_sys_vars";
-- ----------------------------
--  Primary key structure for table "sys_vars"
-- ----------------------------
ALTER TABLE "sys_vars" ADD CONSTRAINT "sys_vars_pkey" PRIMARY KEY ("id_sys_vars") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "bitacora_servicios"
-- ----------------------------
ALTER TABLE "bitacora_servicios" ADD CONSTRAINT "pk_bitacora_servicios" PRIMARY KEY ("id_bitacora_servicios") NOT DEFERRABLE INITIALLY IMMEDIATE;

