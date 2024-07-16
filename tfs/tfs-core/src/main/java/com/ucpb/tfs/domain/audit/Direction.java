package com.ucpb.tfs.domain.audit;

public enum Direction {

	INCOMING("I"),OUTGOING("O");
	
	private String code;
	
	private Direction(String code){
		this.code = code;
	}
	
	public String getCode(){
		return code;
	}
	
	public static Direction getDirectionByCode(String code){
		for(Direction direction : Direction.values()){
			if(direction.getCode().equalsIgnoreCase(code)){
				return direction;
			}
		}
		return null;
	}

    @Override
    public String toString() {
        return code;
    }
	
}
