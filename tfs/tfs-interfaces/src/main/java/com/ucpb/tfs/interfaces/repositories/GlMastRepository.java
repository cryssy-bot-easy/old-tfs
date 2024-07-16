package com.ucpb.tfs.interfaces.repositories;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface GlMastRepository {

    public List<Map<String, ?>> getEntries(@Param("currency") String currency, @Param("code") String accountingCode);
    
    public List<Map<String, ?>> getEntriesByCurrencyBookCodeAccountingCode(@Param("currency") String currency,@Param("bookCode") String bookCode, @Param("code") String accountingCode);

    public List<Map<String, ?>> getAllEntries();

    public List<Map<String, ?>> getEntriesByCurrencyBookCodeLbpAccountingCodeUnitCode(@Param("currency") String currency,@Param("bookCode") String bookCode, @Param("code") String accountingCode, @Param("branch") String unitCode);

    public List<Map<String, ?>> getEntriesByCurrencyBookCodeAccountingCodeUnitCode(@Param("currency") String currency,@Param("bookCode") String bookCode, @Param("code") String accountingCode, @Param("branch") String unitCode);

    public List<Map<String, ?>> getEntriesChecking(@Param("code") String accountingCode);
}
