package com.ucpb.tfs.swift.message;

import javax.validation.constraints.Pattern;

/**
 */
public class MT701 extends SwiftMessage {
    
    private String field27;

    @Pattern(regexp = SwiftFields.DOCUMENT_NUMBER)
    private String field20;

    private String field45A;
    
    private String field46A;

    private String field47A;
    
    private String field49G;

    private String field49H;

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

	public String getField46A() {
		return field46A;
	}

	public void setField46A(String field46a) {
		field46A = field46a;
	}

	public String getField47A() {
		return field47A;
	}

	public void setField47A(String field47a) {
		field47A = field47a;
	}

	public String getField45A() {
		return field45A;
	}

	public void setField45A(String field45a) {
		field45A = field45a;
	}

	public String getField49G() {
		return field49G;
	}

	public void setField49G(String field49g) {
		field49G = field49g;
	}

	public String getField49H() {
		return field49H;
	}

	public void setField49H(String field49h) {
		field49H = field49h;
	}
}
