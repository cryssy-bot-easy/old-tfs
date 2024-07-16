package com.ucpb.tfs.domain.product;

import java.util.Date;
import java.util.List;

public interface LetterOfCreditRepository {

	public List<LetterOfCredit> getLcsWithEarmarking(Date date);
}
