--
-- PostgreSQL database dump
--

\restrict vlWWk0xo2JIf06z5LejqUbqODXJTiT6yHz44Jor06ZriWEov19AUffBGVbPFsFK

-- Dumped from database version 18.0
-- Dumped by pg_dump version 18.0

-- Started on 2025-12-22 19:56:53

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 229 (class 1255 OID 24772)
-- Name: log_new_player(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.log_new_player() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO activity_logs(message)
    VALUES ('Yeni oyuncu eklendi: ' || NEW.nickname);
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.log_new_player() OWNER TO postgres;

--
-- TOC entry 232 (class 1255 OID 25044)
-- Name: log_player_changes(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.log_player_changes() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO activity_logs(message) VALUES ('Yeni Oyuncu Eklendi: ' || NEW.first_name);
        RETURN NEW;
    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO activity_logs(message) VALUES ('Oyuncu Silindi: ' || OLD.first_name);
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$;


ALTER FUNCTION public.log_player_changes() OWNER TO postgres;

--
-- TOC entry 230 (class 1255 OID 24999)
-- Name: sp_create_team(character varying, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.sp_create_team(IN p_team_name character varying, IN p_short_code character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO teams (team_name, short_code) 
    VALUES (p_team_name, p_short_code);
END;
$$;


ALTER PROCEDURE public.sp_create_team(IN p_team_name character varying, IN p_short_code character varying) OWNER TO postgres;

--
-- TOC entry 231 (class 1255 OID 25043)
-- Name: transfer_player_proc(integer, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.transfer_player_proc(IN p_player_id integer, IN p_new_team_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    UPDATE players 
    SET team_id = p_new_team_id 
    WHERE player_id = p_player_id;
    
    COMMIT;
END;
$$;


ALTER PROCEDURE public.transfer_player_proc(IN p_player_id integer, IN p_new_team_id integer) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 226 (class 1259 OID 24990)
-- Name: activity_logs; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.activity_logs (
    log_id integer NOT NULL,
    message character varying(255),
    log_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.activity_logs OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 24989)
-- Name: activity_logs_log_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.activity_logs_log_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.activity_logs_log_id_seq OWNER TO postgres;

--
-- TOC entry 5072 (class 0 OID 0)
-- Dependencies: 225
-- Name: activity_logs_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.activity_logs_log_id_seq OWNED BY public.activity_logs.log_id;


--
-- TOC entry 224 (class 1259 OID 24971)
-- Name: players; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.players (
    player_id integer NOT NULL,
    first_name character varying(50) NOT NULL,
    last_name character varying(50) NOT NULL,
    nickname character varying(50) NOT NULL,
    rank_score integer DEFAULT 0,
    team_id integer
);


ALTER TABLE public.players OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 24970)
-- Name: players_player_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.players_player_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.players_player_id_seq OWNER TO postgres;

--
-- TOC entry 5073 (class 0 OID 0)
-- Dependencies: 223
-- Name: players_player_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.players_player_id_seq OWNED BY public.players.player_id;


--
-- TOC entry 222 (class 1259 OID 24958)
-- Name: teams; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.teams (
    team_id integer NOT NULL,
    team_name character varying(100) NOT NULL,
    short_code character varying(5) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    league_points integer DEFAULT 0
);


ALTER TABLE public.teams OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 24957)
-- Name: teams_team_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.teams_team_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.teams_team_id_seq OWNER TO postgres;

--
-- TOC entry 5074 (class 0 OID 0)
-- Dependencies: 221
-- Name: teams_team_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.teams_team_id_seq OWNED BY public.teams.team_id;


--
-- TOC entry 228 (class 1259 OID 25050)
-- Name: tournaments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tournaments (
    tournament_id integer NOT NULL,
    name character varying(100) NOT NULL,
    start_date date,
    prize_pool integer
);


ALTER TABLE public.tournaments OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 25049)
-- Name: tournaments_tournament_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tournaments_tournament_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tournaments_tournament_id_seq OWNER TO postgres;

--
-- TOC entry 5075 (class 0 OID 0)
-- Dependencies: 227
-- Name: tournaments_tournament_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tournaments_tournament_id_seq OWNED BY public.tournaments.tournament_id;


--
-- TOC entry 220 (class 1259 OID 24944)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    user_id integer NOT NULL,
    username character varying(50) NOT NULL,
    password character varying(50) NOT NULL,
    role character varying(20) NOT NULL,
    team_id integer,
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['ADMIN'::character varying, 'KAPTAN'::character varying, 'GUEST'::character varying])::text[])))
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 24943)
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_user_id_seq OWNER TO postgres;

--
-- TOC entry 5076 (class 0 OID 0)
-- Dependencies: 219
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_user_id_seq OWNED BY public.users.user_id;


--
-- TOC entry 4886 (class 2604 OID 24993)
-- Name: activity_logs log_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.activity_logs ALTER COLUMN log_id SET DEFAULT nextval('public.activity_logs_log_id_seq'::regclass);


--
-- TOC entry 4884 (class 2604 OID 24974)
-- Name: players player_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.players ALTER COLUMN player_id SET DEFAULT nextval('public.players_player_id_seq'::regclass);


--
-- TOC entry 4881 (class 2604 OID 24961)
-- Name: teams team_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.teams ALTER COLUMN team_id SET DEFAULT nextval('public.teams_team_id_seq'::regclass);


--
-- TOC entry 4888 (class 2604 OID 25053)
-- Name: tournaments tournament_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tournaments ALTER COLUMN tournament_id SET DEFAULT nextval('public.tournaments_tournament_id_seq'::regclass);


--
-- TOC entry 4880 (class 2604 OID 24947)
-- Name: users user_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN user_id SET DEFAULT nextval('public.users_user_id_seq'::regclass);


--
-- TOC entry 5064 (class 0 OID 24990)
-- Dependencies: 226
-- Data for Name: activity_logs; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.activity_logs (log_id, message, log_date) FROM stdin;
1	Yeni oyuncu eklendi: Slayer	2025-12-09 17:41:27.601812
2	Yeni oyuncu eklendi:  Juve	2025-12-09 18:02:01.101408
3	Yeni oyuncu eklendi: Mirket	2025-12-09 18:02:35.976624
4	Yeni oyuncu eklendi: kolpaci	2025-12-09 18:12:56.942574
5	Yeni oyuncu eklendi: damla	2025-12-09 18:35:24.257794
6	Maç Sonucu: Super Massive vs Galatasaray : Super Massive KAZANDI! 🏆	2025-12-19 18:04:45.850568
7	Maç Sonucu: Super Massive vs Fenerbahçe : Super Massive KAZANDI! 🏆	2025-12-19 18:04:53.253544
8	Maç Sonucu: Super Massive vs Fenerbahçe : Super Massive KAZANDI! 🏆	2025-12-19 18:04:55.434267
9	Maç Sonucu: Super Massive yendi Fenerbahçe (2224-1071)	2025-12-19 18:14:43.454729
10	Maç Sonucu: Super Massive yendi Fenerbahçe (1732-1630)	2025-12-19 18:14:46.597736
11	Maç Sonucu: Super Massive yendi Fenerbahçe (1461-1058)	2025-12-19 18:14:48.696737
12	Maç Sonucu: Super Massive yendi Fenerbahçe (1406-1230)	2025-12-19 18:14:49.933767
13	Maç Sonucu: Super Massive yendi Fenerbahçe (2734-2339)	2025-12-19 18:14:54.224683
14	Maç Sonucu: Super Massive yendi Fenerbahçe (2816-1209)	2025-12-19 18:14:55.274729
15	Maç Sonucu: Super Massive yendi Fenerbahçe (2486-2079)	2025-12-19 18:14:58.650956
16	Maç Sonucu: Super Massive yendi Fenerbahçe (4728-2546)	2025-12-19 18:14:59.435579
17	Maç Sonucu: Super Massive yendi Fenerbahçe (2784-1869)	2025-12-19 18:15:00.162776
18	Maç Sonucu: Super Massive yendi Fenerbahçe (2006-894)	2025-12-19 18:15:00.580094
19	Maç Sonucu: Fenerbahçe yendi Super Massive (1712-2352)	2025-12-19 18:15:01.172188
20	Maç Sonucu: Super Massive yendi Fenerbahçe (2102-1965)	2025-12-19 18:15:01.75965
21	Maç Sonucu: Super Massive yendi Fenerbahçe (1074-1066)	2025-12-19 18:15:02.378028
22	Maç Sonucu: Super Massive yendi Fenerbahçe (1541-2157)	2025-12-19 18:15:06.648724
23	Maç Sonucu: Fenerbahçe yendi Super Massive (2453-1871)	2025-12-19 18:15:07.548725
24	Maç Sonucu: Super Massive yendi Fenerbahçe (1176-1972)	2025-12-19 18:15:08.135953
25	Maç Sonucu: Super Massive yendi Fenerbahçe (1753-2289)	2025-12-19 18:15:08.716351
26	Maç Sonucu: Super Massive yendi Fenerbahçe (1434-2275)	2025-12-19 18:15:09.350837
27	Maç Sonucu: Fenerbahçe yendi Super Massive (1512-1337)	2025-12-19 18:15:10.14337
28	Maç Sonucu: Super Massive yendi Fenerbahçe (2106-2418)	2025-12-19 18:15:10.83286
29	Maç Sonucu: Super Massive yendi Fenerbahçe (2142-7401)	2025-12-19 18:15:11.991331
30	Simülasyon: Super Massive (Blue) yendi Fenerbahçe (Red) [1465-1367]	2025-12-19 18:28:21.000516
31	Simülasyon: Super Massive (Red) yendi Fenerbahçe (Blue) [1519-1660]	2025-12-19 18:28:41.749854
32	Simülasyon: Fenerbahçe (Blue) yendi Super Massive (Red) [2419-1102]	2025-12-19 18:28:42.607432
33	Simülasyon: Super Massive (Red) yendi Fenerbahçe (Blue) [2045-3492]	2025-12-19 18:28:44.47059
34	Simülasyon: Fenerbahçe (Red) yendi Super Massive (Blue) [1962-7383]	2025-12-22 01:58:52.866504
35	Simülasyon: Super Massive (Blue) yendi Fenerbahçe (Red) [1419-1166]	2025-12-22 01:58:54.294595
36	Simülasyon: Fenerbahçe (Red) yendi Super Massive (Blue) [1275-2427]	2025-12-22 01:58:55.217257
37	Simülasyon: Fenerbahçe (Red) yendi Super Massive (Blue) [1528-1690]	2025-12-22 01:58:56.140959
38	Simülasyon: Super Massive (Blue) yendi Fenerbahçe (Red) [3639-886]	2025-12-22 01:58:57.08126
39	Simülasyon: Fenerbahçe (Red) yendi Super Massive (Blue) [2100-3594]	2025-12-22 01:59:01.537553
40	Yeni Oyuncu Eklendi: büş	2025-12-22 18:02:00.287561
41	Yeni oyuncu eklendi: BUSRA	2025-12-22 18:02:00.287561
42	Simülasyon: Fenerbahçe (Blue) yendi Galatasaray (Red) [7868757-87]	2025-12-22 18:12:53.642491
43	Oyuncu Silindi: büş	2025-12-22 18:13:56.482563
44	Yeni Oyuncu Eklendi: ata	2025-12-22 18:14:59.643963
45	Yeni oyuncu eklendi: Slay	2025-12-22 18:14:59.643963
46	Simülasyon: Fenerbahçe (Red) yendi Super Massive (Blue) [1935-16614]	2025-12-22 18:18:30.200294
47	Simülasyon: Fenerbahçe (Red) yendi Super Massive (Blue) [2136-19183]	2025-12-22 18:18:31.311155
48	Simülasyon: Fenerbahçe (Red) yendi Super Massive (Blue) [2408-9015]	2025-12-22 18:18:32.241248
49	Simülasyon: Fenerbahçe (Red) yendi Super Massive (Blue) [1985-8767]	2025-12-22 18:18:32.840225
50	Simülasyon: Fenerbahçe (Red) yendi Super Massive (Blue) [2553-23224]	2025-12-22 18:18:33.382333
51	Simülasyon: Fenerbahçe (Red) yendi Super Massive (Blue) [3399-12380]	2025-12-22 18:18:33.63183
52	Simülasyon: Fenerbahçe (Red) yendi Super Massive (Blue) [3335-17955]	2025-12-22 18:18:33.830701
53	Simülasyon: Fenerbahçe (Red) yendi Super Massive (Blue) [5079-15108]	2025-12-22 18:18:34.129635
\.


--
-- TOC entry 5062 (class 0 OID 24971)
-- Dependencies: 224
-- Data for Name: players; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.players (player_id, first_name, last_name, nickname, rank_score, team_id) FROM stdin;
2	Atacan	Pınar	 Juve	1350	1
3	Büşra	Arslan	Mirket	200	3
5	yagmur	kara	damla	2323	\N
1	Ali	Yilmaz	Slayer	1000	1
4	ali	çatak	essek	20003	3
9	ata	Yilmaz	Slay	1000	1
\.


--
-- TOC entry 5060 (class 0 OID 24958)
-- Dependencies: 222
-- Data for Name: teams; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.teams (team_id, team_name, short_code, created_at, league_points) FROM stdin;
1	Super Massive	SUP	2025-12-09 17:41:27.601812	0
2	Galatasaray	GS	2025-12-09 17:53:02.657094	0
3	Fenerbahçe	FB	2025-12-09 17:53:12.227366	0
5	MISA	MISA	2025-12-09 17:53:32.263378	0
6	BBL	BBL	2025-12-09 17:53:39.109978	0
7	Dark Passage	DP	2025-12-09 17:54:00.509523	0
\.


--
-- TOC entry 5066 (class 0 OID 25050)
-- Dependencies: 228
-- Data for Name: tournaments; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tournaments (tournament_id, name, start_date, prize_pool) FROM stdin;
1	Büyük Kış Turnuvası	2025-12-30	50000
\.


--
-- TOC entry 5058 (class 0 OID 24944)
-- Dependencies: 220
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (user_id, username, password, role, team_id) FROM stdin;
2	kaptan1	4444	KAPTAN	1
1	admin	1234	ADMIN	\N
8	ziyaretci	123	GUEST	\N
\.


--
-- TOC entry 5077 (class 0 OID 0)
-- Dependencies: 225
-- Name: activity_logs_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.activity_logs_log_id_seq', 53, true);


--
-- TOC entry 5078 (class 0 OID 0)
-- Dependencies: 223
-- Name: players_player_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.players_player_id_seq', 9, true);


--
-- TOC entry 5079 (class 0 OID 0)
-- Dependencies: 221
-- Name: teams_team_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.teams_team_id_seq', 7, true);


--
-- TOC entry 5080 (class 0 OID 0)
-- Dependencies: 227
-- Name: tournaments_tournament_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tournaments_tournament_id_seq', 2, true);


--
-- TOC entry 5081 (class 0 OID 0)
-- Dependencies: 219
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_user_id_seq', 8, true);


--
-- TOC entry 4903 (class 2606 OID 24997)
-- Name: activity_logs activity_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.activity_logs
    ADD CONSTRAINT activity_logs_pkey PRIMARY KEY (log_id);


--
-- TOC entry 4899 (class 2606 OID 24983)
-- Name: players players_nickname_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.players
    ADD CONSTRAINT players_nickname_key UNIQUE (nickname);


--
-- TOC entry 4901 (class 2606 OID 24981)
-- Name: players players_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.players
    ADD CONSTRAINT players_pkey PRIMARY KEY (player_id);


--
-- TOC entry 4895 (class 2606 OID 24967)
-- Name: teams teams_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.teams
    ADD CONSTRAINT teams_pkey PRIMARY KEY (team_id);


--
-- TOC entry 4897 (class 2606 OID 24969)
-- Name: teams teams_short_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.teams
    ADD CONSTRAINT teams_short_code_key UNIQUE (short_code);


--
-- TOC entry 4905 (class 2606 OID 25057)
-- Name: tournaments tournaments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tournaments
    ADD CONSTRAINT tournaments_pkey PRIMARY KEY (tournament_id);


--
-- TOC entry 4891 (class 2606 OID 24954)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- TOC entry 4893 (class 2606 OID 24956)
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- TOC entry 4908 (class 2620 OID 25045)
-- Name: players player_audit_trigger; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER player_audit_trigger AFTER INSERT OR DELETE ON public.players FOR EACH ROW EXECUTE FUNCTION public.log_player_changes();


--
-- TOC entry 4909 (class 2620 OID 24998)
-- Name: players trg_player_insert; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_player_insert AFTER INSERT ON public.players FOR EACH ROW EXECUTE FUNCTION public.log_new_player();


--
-- TOC entry 4907 (class 2606 OID 24984)
-- Name: players players_team_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.players
    ADD CONSTRAINT players_team_id_fkey FOREIGN KEY (team_id) REFERENCES public.teams(team_id) ON DELETE SET NULL;


--
-- TOC entry 4906 (class 2606 OID 25012)
-- Name: users users_team_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_team_id_fkey FOREIGN KEY (team_id) REFERENCES public.teams(team_id);


-- Completed on 2025-12-22 19:56:53

--
-- PostgreSQL database dump complete
--

\unrestrict vlWWk0xo2JIf06z5LejqUbqODXJTiT6yHz44Jor06ZriWEov19AUffBGVbPFsFK

