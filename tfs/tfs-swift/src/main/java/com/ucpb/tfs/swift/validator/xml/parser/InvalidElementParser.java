package com.ucpb.tfs.swift.validator.xml.parser;

import com.ucpb.tfs.swift.validator.ValidationError;

public class InvalidElementParser extends ErrorMessageParser{

	@Override
	protected ValidationError mapToValidationError(String[] response) {
		ValidationError validationError=new ValidationError(response[1],response[0],
				response[1] + " with value=" + response[0] + " is invalid.");
		return validationError;
	}

	@Override
	protected String getFormat() {
		return "cvc-type.3.1.3: The value '(.*)' of element '(.+)' is not valid.";
	}
}
