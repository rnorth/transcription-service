package models;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author richardnorth
 *
 */
public class TranscriptionResult {

	public List<Ruby> rubies = Lists.newArrayList();

	public int getSize() {
		return rubies.size();
	}
}
