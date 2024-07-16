package com.ucpb.tfs.swift.validator.xml.parser;

import com.ucpb.tfs.swift.validator.ValidationError;

public class InvalidMaxLengthParser extends ErrorMessageParser{

	@Override
	protected ValidationError mapToValidationError(String[] response) {
		ValidationError validationError=new ValidationError(response[3],response[0],
				"field" + response[3] + " with length value=" + response[1] + 
				" is invalid because it does not comply to the maximum required length value="+ response[2]);
		return validationError;
	}

	@Override
	protected String getFormat() {
		return "cvc-maxLength-valid: Value '(.*)' with length = '(\\d*)' is not facet-valid with respect to maxLength '(\\d*)' for type 'field(.+)'.";
	}
}
