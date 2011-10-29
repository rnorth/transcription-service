package models;

import com.google.common.collect.Lists;


public class PunctuationRuby extends Ruby {

	public static final String PUNCTUATION = "記号";

	public PunctuationRuby(String surface) {
		super(surface, PUNCTUATION, null, surface, Lists.newArrayList(surface));
	}

}
