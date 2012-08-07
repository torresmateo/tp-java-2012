--
-- PostgreSQL database dump
--

-- Dumped from database version 8.4.9
-- Dumped by pg_dump version 9.1.4
-- Started on 2012-07-31 14:35:15 PYT

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 1778 (class 1262 OID 24720)
-- Name: tpjava; Type: DATABASE; Schema: -; Owner: -
--




SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

SET default_with_oids = false;

--
-- TOC entry 141 (class 1259 OID 24723)
-- Dependencies: 1772 1773 3
-- Name: bitacora_servicios; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE bitacora_servicios (
    id_bitacora_servicios bigint NOT NULL,
    alias character varying(30),
    direccion_ip character varying(40) NOT NULL,
    puerto integer NOT NULL,
    email character varying(250) NOT NULL,
    estado character varying(10) DEFAULT 'N/A'::character varying,
    marca_tiempo timestamp without time zone DEFAULT now()
);


--
-- TOC entry 1780 (class 0 OID 0)
-- Dependencies: 141
-- Name: COLUMN bitacora_servicios.alias; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN bitacora_servicios.alias IS 'El alias para identificar en forma de string al host o nro. de IP que tiene el servicio a ser monitoreado. ';


--
-- TOC entry 1781 (class 0 OID 0)
-- Dependencies: 141
-- Name: COLUMN bitacora_servicios.direccion_ip; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN bitacora_servicios.direccion_ip IS 'La direcci—n IP del servicio monitoreado. Soporta hasta direcciones IPv6';


--
-- TOC entry 1782 (class 0 OID 0)
-- Dependencies: 141
-- Name: COLUMN bitacora_servicios.puerto; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN bitacora_servicios.puerto IS 'El puerto TCP del servicio monitoreado.';


--
-- TOC entry 1783 (class 0 OID 0)
-- Dependencies: 141
-- Name: COLUMN bitacora_servicios.email; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN bitacora_servicios.email IS 'El correo electr—nico del destinatario responsable de este servicio.';


--
-- TOC entry 1784 (class 0 OID 0)
-- Dependencies: 141
-- Name: COLUMN bitacora_servicios.estado; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN bitacora_servicios.estado IS 'El estado del servicio en el momento de la revisi—n. ';


--
-- TOC entry 1785 (class 0 OID 0)
-- Dependencies: 141
-- Name: COLUMN bitacora_servicios.marca_tiempo; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN bitacora_servicios.marca_tiempo IS 'La fecha correspondiente a esta revisi—n.';


--
-- TOC entry 140 (class 1259 OID 24721)
-- Dependencies: 3 141
-- Name: bitacora_servicios_id_bitacora_servicios_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE bitacora_servicios_id_bitacora_servicios_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 1786 (class 0 OID 0)
-- Dependencies: 140
-- Name: bitacora_servicios_id_bitacora_servicios_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE bitacora_servicios_id_bitacora_servicios_seq OWNED BY bitacora_servicios.id_bitacora_servicios;


--
-- TOC entry 1771 (class 2604 OID 24726)
-- Dependencies: 141 140 141
-- Name: id_bitacora_servicios; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY bitacora_servicios ALTER COLUMN id_bitacora_servicios SET DEFAULT nextval('bitacora_servicios_id_bitacora_servicios_seq'::regclass);


--
-- TOC entry 1775 (class 2606 OID 24728)
-- Dependencies: 141 141
-- Name: pk_bitacora_servicios; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY bitacora_servicios
    ADD CONSTRAINT pk_bitacora_servicios PRIMARY KEY (id_bitacora_servicios);


-- Completed on 2012-07-31 14:35:16 PYT

--
-- PostgreSQL database dump complete
--

