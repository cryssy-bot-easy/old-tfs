package com.ucpb.tfs.swift.message;

import javax.validation.constraints.Pattern;

/**
 */
public class MT708 extends SwiftMessage {
    
    private String field27;

    @Pattern(regexp = SwiftFields.DOCUMENT_NUMBER)
    private String field20;

    private String field45B;
    
    private String field46B;

    private String field47B;
    
    private String field49M;

    private String field49N;

    public String getField27() {
        return field27;
    }

    public void setField27(String field27) {
        this.field27 = field27;
    }

    public String getField20() {
        return field20;
    }

    public void setField20(String field20) {
        this.field20 = field20;
    }

	public String getField45B() {
		return field45B;
	}

	public void setField45B(String field45b) {
		field45B = field45b;
	}

	public String getField46B() {
		return field46B;
	}

	public void setField46B(String field46b) {
		field46B = field46b;
	}

	public String getField47B() {
		return field47B;
	}

	public void setField47B(String field47b) {
		field47B = field47b;
	}

	public String getField49M() {
		return field49M;
	}

	public void setField49M(String field49m) {
		field49M = field49m;
	}

	public String getField49N() {
		return field49N;
	}

	public void setField49N(String field49n) {
		field49N = field49n;
	}
}
