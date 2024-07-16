CREATE TABLE UCDATPLNS.LNAPPF (
		AFAPNO CHAR(20) DEFAULT ' ' NOT NULL,
		AFFCDE CHAR(3) DEFAULT ' ' NOT NULL,
		AFSEQ DECIMAL(11 , 0) DEFAULT 0 NOT NULL,
		"AFBR#" DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		AFLTYP CHAR(2) DEFAULT ' ' NOT NULL,
		AFAPLY DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFFAMT DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFEND6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFEND7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFAPD6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFAPD7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFINST DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFFPMT DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFTERM DECIMAL(5 , 0) DEFAULT 0 NOT NULL,
		AFTCOD CHAR(1) DEFAULT ' ' NOT NULL,
		AFCPNO CHAR(20) DEFAULT ' ' NOT NULL,
		AFPURP CHAR(2) DEFAULT ' ' NOT NULL,
		AFCCBP DECIMAL(4 , 0) DEFAULT 0 NOT NULL,
		AFOFD6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFOFD7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFOAD6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFOAD7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFARD6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFARD7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFAPPR CHAR(2) DEFAULT ' ' NOT NULL,
		AFAPR1 CHAR(3) DEFAULT ' ' NOT NULL,
		AFAPR2 CHAR(3) DEFAULT ' ' NOT NULL,
		AFCANC CHAR(2) DEFAULT ' ' NOT NULL,
		AFCND7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFCND6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFCAR CHAR(2) DEFAULT ' ' NOT NULL,
		AFCARI CHAR(2) DEFAULT ' ' NOT NULL,
		AFCRIP CHAR(2) DEFAULT ' ' NOT NULL,
		AFCID6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFCID7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFCPD6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFCPD7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFCARP CHAR(2) DEFAULT ' ' NOT NULL,
		AFCSD6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFCSD7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFCSP6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFCSP7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFFEE DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFOFFR CHAR(3) DEFAULT ' ' NOT NULL,
		AFLMST CHAR(2) DEFAULT ' ' NOT NULL,
		AFSTAT CHAR(1) DEFAULT ' ' NOT NULL,
		AFBASE DECIMAL(1 , 0) DEFAULT 0 NOT NULL,
		AFMODE CHAR(1) DEFAULT ' ' NOT NULL,
		AFYBSE DECIMAL(1 , 0) DEFAULT 0 NOT NULL,
		AFRATE DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		"AFRAT#" DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		AFVAR DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AFVARC CHAR(1) DEFAULT ' ' NOT NULL,
		AFPFLR DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AFPCEL DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AFPRV6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFPRV7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFRVTM DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		AFRVCD CHAR(1) DEFAULT ' ' NOT NULL,
		AFCUR CHAR(4) DEFAULT ' ' NOT NULL,
		AFTPSQ CHAR(1) DEFAULT ' ' NOT NULL,
		AFEXRT DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AFEXCD CHAR(1) DEFAULT ' ' NOT NULL,
		AFIAMT DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFISTS CHAR(1) DEFAULT ' ' NOT NULL,
		AFIAP6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFIAP7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFIAR6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFIAR7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFICN6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFICN7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFSVAR DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AFSVRC CHAR(1) DEFAULT ' ' NOT NULL,
		AFFLAT DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AFPAR CHAR(1) DEFAULT ' ' NOT NULL,
		AFDLNO CHAR(10) DEFAULT ' ' NOT NULL,
		"AFCIF#" CHAR(7) DEFAULT ' ' NOT NULL,
		AFLIMT DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFSDC6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFSDC7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFRDC6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFRDC7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFLCOD DECIMAL(2 , 0) DEFAULT 0 NOT NULL,
		AFEMRK DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFCOM DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFCOMR DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AFCOMB CHAR(1) DEFAULT ' ' NOT NULL,
		AFCMAX DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFCMIN DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFSBFC CHAR(3) DEFAULT ' ' NOT NULL,
		AFSBSQ DECIMAL(11 , 0) DEFAULT 0 NOT NULL,
		AFEXP6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFEXP7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFCFER DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		"AFCFE#" DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		AFCFEE DECIMAL(17 , 5) DEFAULT 0 NOT NULL,
		AFMDT6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFMDT7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFORGM DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFEXMT DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AFMFAC CHAR(1) DEFAULT ' ' NOT NULL,
		"AFMAA#" CHAR(20) DEFAULT ' ' NOT NULL,
		AFMFCD CHAR(3) DEFAULT ' ' NOT NULL,
		AFMSEQ DECIMAL(11 , 0) DEFAULT 0 NOT NULL,
		AFSTBY CHAR(1) DEFAULT ' ' NOT NULL,
		AFLEVL DECIMAL(1 , 0) DEFAULT 0 NOT NULL,
		AFAMTU DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFCOMP CHAR(1) DEFAULT ' ' NOT NULL,
		AFSPRV DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFINSP DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFOBAL DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFDVID CHAR(7) DEFAULT ' ' NOT NULL,
		LNVUSR CHAR(10) DEFAULT ' ' NOT NULL,
		LNVDT6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		LNVDT7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		LNVTME DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		ASELLP DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AUNREA DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		ABOOKV DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		ASCRDW DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		ASCRGP DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		APPRRT DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		APPRAM DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		ASRVRA DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AMAXTM DECIMAL(5 , 0) DEFAULT 0 NOT NULL,
		AMAXCD CHAR(1) DEFAULT ' ' NOT NULL,
		AOFSTA CHAR(1) DEFAULT ' ' NOT NULL,
		AEMPID CHAR(12) DEFAULT ' ' NOT NULL,
		ADEPTC CHAR(3) DEFAULT ' ' NOT NULL,
		ABDFLG CHAR(1) DEFAULT ' ' NOT NULL,
		ACASA DECIMAL(19 , 0) DEFAULT 0 NOT NULL,
		AVATFL CHAR(1) DEFAULT ' ' NOT NULL,
		AGLBOO CHAR(2) DEFAULT ' ' NOT NULL,
		AORGBR DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		AGROUP DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		ASCRLS DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		ASCRLR DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		ASOOFU CHAR(10) DEFAULT ' ' NOT NULL,
		AFRMST DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		AFRDST DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		AFRMD6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFRMD7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFRDD6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFRDD7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFRTCD CHAR(2) DEFAULT ' ' NOT NULL,
		AFREAS CHAR(20) DEFAULT ' ' NOT NULL,
		AFRREM CHAR(120) DEFAULT ' ' NOT NULL,
		AFSLEN CHAR(1) DEFAULT ' ' NOT NULL,
		AFSCLS CHAR(10) DEFAULT ' ' NOT NULL,
		AFSLMT DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFSDT6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFSDT7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFCHGR CHAR(30) DEFAULT ' ' NOT NULL,
		AFCTYP CHAR(10) DEFAULT ' ' NOT NULL,
		AFCCOD CHAR(10) DEFAULT ' ' NOT NULL,
		AFPROM CHAR(10) DEFAULT ' ' NOT NULL
	);

	CREATE TABLE UCDATULNS2.LNAPPF (
		AFAPNO CHAR(20) DEFAULT ' ' NOT NULL,
		AFFCDE CHAR(3) DEFAULT ' ' NOT NULL,
		AFSEQ DECIMAL(11 , 0) DEFAULT 0 NOT NULL,
		"AFBR#" DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		AFLTYP CHAR(2) DEFAULT ' ' NOT NULL,
		AFAPLY DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFFAMT DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFEND6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFEND7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFAPD6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFAPD7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFINST DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFFPMT DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFTERM DECIMAL(5 , 0) DEFAULT 0 NOT NULL,
		AFTCOD CHAR(1) DEFAULT ' ' NOT NULL,
		AFCPNO CHAR(20) DEFAULT ' ' NOT NULL,
		AFPURP CHAR(2) DEFAULT ' ' NOT NULL,
		AFCCBP DECIMAL(4 , 0) DEFAULT 0 NOT NULL,
		AFOFD6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFOFD7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFOAD6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFOAD7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFARD6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFARD7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFAPPR CHAR(2) DEFAULT ' ' NOT NULL,
		AFAPR1 CHAR(3) DEFAULT ' ' NOT NULL,
		AFAPR2 CHAR(3) DEFAULT ' ' NOT NULL,
		AFCANC CHAR(2) DEFAULT ' ' NOT NULL,
		AFCND7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFCND6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFCAR CHAR(2) DEFAULT ' ' NOT NULL,
		AFCARI CHAR(2) DEFAULT ' ' NOT NULL,
		AFCRIP CHAR(2) DEFAULT ' ' NOT NULL,
		AFCID6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFCID7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFCPD6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFCPD7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFCARP CHAR(2) DEFAULT ' ' NOT NULL,
		AFCSD6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFCSD7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFCSP6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFCSP7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFFEE DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFOFFR CHAR(3) DEFAULT ' ' NOT NULL,
		AFLMST CHAR(2) DEFAULT ' ' NOT NULL,
		AFSTAT CHAR(1) DEFAULT ' ' NOT NULL,
		AFBASE DECIMAL(1 , 0) DEFAULT 0 NOT NULL,
		AFMODE CHAR(1) DEFAULT ' ' NOT NULL,
		AFYBSE DECIMAL(1 , 0) DEFAULT 0 NOT NULL,
		AFRATE DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		"AFRAT#" DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		AFVAR DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AFVARC CHAR(1) DEFAULT ' ' NOT NULL,
		AFPFLR DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AFPCEL DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AFPRV6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFPRV7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFRVTM DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		AFRVCD CHAR(1) DEFAULT ' ' NOT NULL,
		AFCUR CHAR(4) DEFAULT ' ' NOT NULL,
		AFTPSQ CHAR(1) DEFAULT ' ' NOT NULL,
		AFEXRT DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AFEXCD CHAR(1) DEFAULT ' ' NOT NULL,
		AFIAMT DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFISTS CHAR(1) DEFAULT ' ' NOT NULL,
		AFIAP6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFIAP7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFIAR6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFIAR7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFICN6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFICN7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFSVAR DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AFSVRC CHAR(1) DEFAULT ' ' NOT NULL,
		AFFLAT DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AFPAR CHAR(1) DEFAULT ' ' NOT NULL,
		AFDLNO CHAR(10) DEFAULT ' ' NOT NULL,
		"AFCIF#" CHAR(7) DEFAULT ' ' NOT NULL,
		AFLIMT DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFSDC6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFSDC7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFRDC6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFRDC7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFLCOD DECIMAL(2 , 0) DEFAULT 0 NOT NULL,
		AFEMRK DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFCOM DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFCOMR DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AFCOMB CHAR(1) DEFAULT ' ' NOT NULL,
		AFCMAX DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFCMIN DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFSBFC CHAR(3) DEFAULT ' ' NOT NULL,
		AFSBSQ DECIMAL(11 , 0) DEFAULT 0 NOT NULL,
		AFEXP6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFEXP7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFCFER DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		"AFCFE#" DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		AFCFEE DECIMAL(17 , 5) DEFAULT 0 NOT NULL,
		AFMDT6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFMDT7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFORGM DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFEXMT DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AFMFAC CHAR(1) DEFAULT ' ' NOT NULL,
		"AFMAA#" CHAR(20) DEFAULT ' ' NOT NULL,
		AFMFCD CHAR(3) DEFAULT ' ' NOT NULL,
		AFMSEQ DECIMAL(11 , 0) DEFAULT 0 NOT NULL,
		AFSTBY CHAR(1) DEFAULT ' ' NOT NULL,
		AFLEVL DECIMAL(1 , 0) DEFAULT 0 NOT NULL,
		AFAMTU DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFCOMP CHAR(1) DEFAULT ' ' NOT NULL,
		AFSPRV DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFINSP DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFOBAL DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFDVID CHAR(7) DEFAULT ' ' NOT NULL,
		LNVUSR CHAR(10) DEFAULT ' ' NOT NULL,
		LNVDT6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		LNVDT7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		LNVTME DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		ASELLP DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AUNREA DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		ABOOKV DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		ASCRDW DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		ASCRGP DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		APPRRT DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		APPRAM DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		ASRVRA DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		AMAXTM DECIMAL(5 , 0) DEFAULT 0 NOT NULL,
		AMAXCD CHAR(1) DEFAULT ' ' NOT NULL,
		AOFSTA CHAR(1) DEFAULT ' ' NOT NULL,
		AEMPID CHAR(12) DEFAULT ' ' NOT NULL,
		ADEPTC CHAR(3) DEFAULT ' ' NOT NULL,
		ABDFLG CHAR(1) DEFAULT ' ' NOT NULL,
		ACASA DECIMAL(19 , 0) DEFAULT 0 NOT NULL,
		AVATFL CHAR(1) DEFAULT ' ' NOT NULL,
		AGLBOO CHAR(2) DEFAULT ' ' NOT NULL,
		AORGBR DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		AGROUP DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		ASCRLS DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		ASCRLR DECIMAL(7 , 6) DEFAULT 0 NOT NULL,
		ASOOFU CHAR(10) DEFAULT ' ' NOT NULL,
		AFRMST DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		AFRDST DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		AFRMD6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFRMD7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFRDD6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFRDD7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFRTCD CHAR(2) DEFAULT ' ' NOT NULL,
		AFREAS CHAR(20) DEFAULT ' ' NOT NULL,
		AFRREM CHAR(120) DEFAULT ' ' NOT NULL,
		AFSLEN CHAR(1) DEFAULT ' ' NOT NULL,
		AFSCLS CHAR(10) DEFAULT ' ' NOT NULL,
		AFSLMT DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		AFSDT6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		AFSDT7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		AFCHGR CHAR(30) DEFAULT ' ' NOT NULL,
		AFCTYP CHAR(10) DEFAULT ' ' NOT NULL,
		AFCCOD CHAR(10) DEFAULT ' ' NOT NULL,
		AFPROM CHAR(10) DEFAULT ' ' NOT NULL
	);