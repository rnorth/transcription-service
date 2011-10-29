package service.transcription;

import com.google.common.collect.Lists;

import models.Ruby;

public class RomajiRuby extends Ruby {

	public RomajiRuby(String surface) {
		super(surface, null, null, surface, Lists.newArrayList(surface));
	}

}
