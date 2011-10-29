package models;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import play.Logger;
import play.db.jpa.JPA;

import com.ibm.icu.text.Transliterator;


public class KanjiRuby extends Ruby {

	public KanjiRuby(String surface, String pos, String inflection, String pronunciation, List<String> definitions) {
		super(surface, pos, inflection, pronunciation, definitions);
	}

	@Override
	public String getFurigana() {

		Transliterator tx = Transliterator.getInstance("Katakana-Hiragana");
		
		return tx.transliterate(pronunciation);
	}
}
