package com.metapatrol.gitlab.ci.runner.client.messages.marshal;

import com.metapatrol.gitlab.ci.runner.client.messages.marshal.exception.UnmarshallingException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringDateMarshaller extends Marshaller<String, Date>{

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public String marshal(Date in) {
		if(in == null){
			return null;
		}

		return simpleDateFormat.format(in);
	}

	@Override
	public Date unmarshal(String out) throws UnmarshallingException {
		if(out == null){
			return null;
		}

		try {
			return simpleDateFormat.parse(out);
		} catch (ParseException e) {
			throw new UnmarshallingException(e);
		}
	}
}
