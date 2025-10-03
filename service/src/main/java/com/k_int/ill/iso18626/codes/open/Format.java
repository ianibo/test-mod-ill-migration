package com.k_int.ill.iso18626.codes.open;

import com.k_int.ill.iso18626.codes.Code;

public class Format extends Code {
	// The initial valid format codes
	public static final String BLU_RAY       = "Blu-ray";
	public static final String BRAILLE       = "Braille";
	public static final String CASSETTE_TAPE = "CassetteTape";
	public static final String CD            = "CD";
	public static final String CD_ROM        = "CD-ROM";
	public static final String DAISY_ROM     = "Daisy-ROM";
	public static final String DVD           = "DVD";
	public static final String EPUB          = "EPUB";
	public static final String EPUB2         = "EPUB2";
	public static final String EPUB3         = "EPUB3";
	public static final String JPEG          = "JPEG";
	public static final String LARGE_PRINT   = "LargePrint";
	public static final String LP            = "LP";
	public static final String MICROFORM     = "Microform";
	public static final String MP3           = "MP3";
	public static final String MULTIMEDIA    = "Multimedia";
	public static final String PAPER_COPY    = "PaperCopy";
	public static final String PDF           = "PDF";
	public static final String PRINTED       = "Printed";
	public static final String TAPE          = "Tape";
	public static final String TIFF          = "TIFF";
	public static final String ULTRA_HD      = "UltraHD";
	public static final String VHS           = "VHS";

	static {
		add(BLU_RAY);
		add(BRAILLE);
		add(CASSETTE_TAPE);
		add(CD);
		add(CD_ROM);
		add(DAISY_ROM);
		add(DVD);
		add(EPUB);
		add(EPUB2);
		add(EPUB3);
		add(JPEG);
		add(LARGE_PRINT);
		add(LP);
		add(MICROFORM);
		add(MP3);
		add(MULTIMEDIA);
		add(PAPER_COPY);
		add(PDF);
		add(PRINTED);
		add(TAPE);
		add(TIFF);
		add(ULTRA_HD);
		add(VHS);
	}

	public Format() {
	}

	public Format(String format) {
		super(format);
	}
}
