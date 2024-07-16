package com.ucpb.tfs.domain.sysparams;

import java.util.List;
import java.util.Map;

public interface RefTransmittalLetterRepository {

	public void saveOrUpdate(RefTransmittalLetter refTransmittalLetter);
    
    public RefTransmittalLetter getRefTransmittalLetter(String transmittalLetterCode);
    
    public void delete(Long id);
    
    public List<RefTransmittalLetter> getAllRefTransmittalLetter();
	
}
