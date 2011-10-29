package service.definition;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

import play.Logger;
import play.db.jpa.JPA;

public class DefinitionService {
	
	private static Map<String,String> posMap = Maps.newHashMap();
	static {
		posMap.put("形容詞-自立-*", "adj-i");
		posMap.put("形容詞-自立-基本形", "adj-i");
		posMap.put("形容詞-自立-ガル接続", "adj-i");
		posMap.put("形容詞-非自立-*", "adj-i");
		posMap.put("形容詞-接尾-*", "adj-i");
		posMap.put("連体詞-*-*", "adj-pn");

		posMap.put("動詞-非自立-*", "v%");
		posMap.put("動詞-非自立-基本形", "v%");
		posMap.put("動詞-非自立-連用形", "v%");
		posMap.put("動詞-接尾-*", "v%");
		posMap.put("動詞-自立-一段", "v1");
		posMap.put("動詞-自立-一段・クレル", "v%");
		posMap.put("動詞-自立-カ変・クル", "vk");
		posMap.put("動詞-自立-カ変・来ル", "vk");
		posMap.put("動詞-自立-サ変・スル", "vs");
		posMap.put("動詞-自立-サ変・－スル ", "vs");
		posMap.put("動詞-自立-五段", "v5");
		posMap.put("動詞-自立-*", "v%");
		
		posMap.put("助詞-副助詞-*", "%"); // adverbial particle
		
		
		posMap.put("助動詞-*-*", "%");
		
		posMap.put("副詞-一般-*", "adv");
		posMap.put("副詞-助詞類接続-*", "adv");
		
		posMap.put("感動詞-*", "int");
		
		posMap.put("接続詞-*-*", "conj");
		
		posMap.put("名詞-一般-*", "n%");
		posMap.put("名詞-数-*", "n%");
		posMap.put("名詞-接尾-*", "%"); // ctr or suf
		posMap.put("名詞-特殊-*", "n%");
		posMap.put("名詞-代名詞-*", "%n");
		posMap.put("名詞-非自立-*", "n%");
		posMap.put("名詞-サ変接続-*", "%");
		posMap.put("名詞-副詞可能-*", "n-%");
		posMap.put("名詞-固有名詞-*", "n%");
		posMap.put("名詞-接続詞的-*", "conj");
		posMap.put("名詞-動詞非自立的-*", "n%");
		posMap.put("名詞-形容動詞語幹-*", "adj-na");
		posMap.put("名詞-ナイ形容詞語幹-*", "n%");
		
		posMap.put("助詞-*-*", "prt");
		
		posMap.put("接頭詞-*-*", "pref");
				
		posMap.put("記号-*-*", "%"); // symbol
		
		posMap.put("助詞-副助詞／並立助詞／終助詞-*", "%"); // UNKNOWN
		
	}
	
	public static List<String> define(String written, String reading, String pos, String pos1, String rule) {
		Query glossQuery = getGlossQuery(written, reading, true, true, jpToEngPos(pos, pos1, rule));
		
		List resultList = glossQuery.getResultList();
		
		final int size = resultList.size();
		if (size==0) {
			resultList = getGlossQuery(written, reading, true, true, "%").getResultList();
			if (resultList.size()>0) {
				Logger.info("Didn't find definition for %s[%s] using %s-%s-%s but did with wildcard (%s)", written, reading, pos, pos1, rule, resultList);
			}
		}
		
		return resultList;
	}

	public static List<String> define(String reading, String pos, String pos1, String rule) {
		Query glossQuery = getGlossQuery("", reading, false, true, jpToEngPos(pos, pos1, rule));
		List resultList = glossQuery.getResultList();
		
		final int size = resultList.size();
		if (size==0) {
			resultList = getGlossQuery("", reading, false, true, "%").getResultList();
			if (resultList.size()>0) {
				Logger.info("Didn't find definition for [%s] using %s-%s-%s but did with wildcard (%s)", reading, pos, pos1, rule, resultList);
			}
		}
		
		return resultList;
	}

	private static String jpToEngPos(String pos, String pos1, String rule) {
		String posMapping = pos + "-" + pos1 + "-" + rule;
		String engPos = posMap.get(posMapping);
		if (engPos==null) {
			posMapping = pos + "-" + pos1 + "-*";
			engPos = posMap.get(posMapping);
			if (engPos==null) {
				posMapping = pos + "-*-*";
				engPos = posMap.get(posMapping);
				if (engPos==null) {
					Logger.info("Couldn't find a POS mapping for %s", posMapping);
					return "%";
				}
			}
		}
		return engPos;
	}

	private static Query getGlossQuery(String written, String reading, boolean withWrittenFilter, boolean withReadingFilter, String pos) {
		Query glossQuery = JPA.em().createNativeQuery("select \n" + 
				"  gloss.sens || '. ' ||\n" + 
				"  string_agg(distinct gloss.txt,' / ') || ' (' ||\n" + 
				"  string_agg(distinct kwpos.kw,', ') || ')'\n" + 
				" from entr\n" + 
				(withWrittenFilter ? " join kanj on kanj.entr = entr.id\n" : "") + 
				" join rdng on rdng.entr = entr.id\n" + 
				" join gloss on gloss.entr = entr.id\n" + 
				" join pos on pos.entr = entr.id\n" + 
				" join kwpos on kwpos.id = pos.kw\n" + 
				" join freq on freq.entr = entr.id\n" + 
				" where\n" + 
				"  rdng.txt=? and \n" + 
				"  kwpos.kw like ? and \n" +
				(withWrittenFilter ? "  kanj.txt=? and \n" : "") + 
				"  pos.sens=gloss.sens \n" + 
				" group by gloss.sens\n" + 
//				" order by (kwpos.kw like ?) ASC \n" +
				" limit 50;");
		glossQuery.setParameter(1, reading);
		glossQuery.setParameter(2, pos);
		if (withWrittenFilter) {
			glossQuery.setParameter(3, written);
		}
		return glossQuery;
	}
}
