package models;


public class PunctuationRuby extends Ruby {

	public static final String PUNCTUATION = "記号";

	public PunctuationRuby(String surface) {
		super(surface, PUNCTUATION, null, surface);
	}

}
