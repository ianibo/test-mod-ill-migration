package com.k_int.ill.iso18626.codes.open;

import com.k_int.ill.iso18626.codes.Code;

public class PublicationType extends Code {
	// The initial valid publication type codes
	public static final String ARCHIVE_MATERIAL = "ArchiveMaterial";
	public static final String ARTICLE          = "Article";
	public static final String AUDIO_BOOK       = "AudioBook";
	public static final String BOOK             = "Book";
	public static final String CHAPTER          = "Chapter";
	public static final String CONFERENCE_PROC  = "ConferenceProc";
	public static final String GAME             = "Game";
	public static final String GOVERNMENT_PUBL  = "GovernmentPubl";
	public static final String IMAGE            = "Image";
	public static final String JOURNAL          = "Journal";
	public static final String MANUSCRIPT       = "Manuscript";
	public static final String MAP              = "Map";
	public static final String MOVIE            = "Movie";
	public static final String MUSIC_RECORDING  = "MusicRecording";
	public static final String MUSIC_SCORE      = "MusicScore";
	public static final String NEWSPAPER        = "Newspaper";
	public static final String PATENT           = "Patent";
	public static final String REPORT           = "Report";
	public static final String SOUND_RECORDING  = "SoundRecording";
	public static final String THESIS           = "Thesis";

	static {
		add(ARCHIVE_MATERIAL);
		add(ARTICLE);
		add(AUDIO_BOOK);
		add(BOOK);
		add(CHAPTER);
		add(CONFERENCE_PROC);
		add(GAME);
		add(GOVERNMENT_PUBL);
		add(IMAGE);
		add(JOURNAL);
		add(MANUSCRIPT);
		add(MAP);
		add(MOVIE);
		add(MUSIC_RECORDING);
		add(MUSIC_SCORE);
		add(NEWSPAPER);
		add(PATENT);
		add(REPORT);
		add(SOUND_RECORDING);
		add(THESIS);
	}

	public PublicationType() {
	};

	public PublicationType(String publicationType) {
		super(publicationType);
	};
}
