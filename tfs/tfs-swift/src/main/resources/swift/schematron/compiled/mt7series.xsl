<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<!-- Modified by: Rafael Ski Poblete
     Date: 8/28/18
     Description: Removed "." in MT700 condition which causes bug because of "." seperator in error Message. 
     
     Date: 9/19/18
     Description: Added conditions to handle normal MT707 and Outgoing MT707. -->

<!-- Description:   Modified validations for MT707 -->
<!-- Modified by:   Cedrick C. Nungay -->
<!-- Date modified: 08/10/18 -->

<!-- Description:   Removed validations for 39B on MT747 -->
<!-- Modified by:   Cedrick C. Nungay -->
<!-- Date modified: 09/13/18 -->

<!-- Description:   Added validations for MT759 -->
<!-- Modified by:   Cedrick C. Nungay -->
<!-- Date modified: 09/18/18 -->

<!-- Description:   Added validation for MT740 -->
<!-- Modified by:   Cedrick C. Nungay -->
<!-- Date modified: 01/09/19 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:sch="http://www.ascc.net/xml/schematron"
                xmlns:mt7="http://www.ucpb.com.ph/tfs/schemas/mt7series"
                version="2.0"
                mt7:dummy-for-xmlns="">
   <xsl:template match="*|@*" mode="schematron-get-full-path">
      <xsl:apply-templates select="parent::*" mode="schematron-get-full-path"/>
      <xsl:text>/</xsl:text>
      <xsl:if test="count(. | ../@*) = count(../@*)">@</xsl:if>
      <xsl:value-of select="name()"/>
      <xsl:text>[</xsl:text>
      <xsl:value-of select="1+count(preceding-sibling::*[name()=name(current())])"/>
      <xsl:text>]</xsl:text>
   </xsl:template>
   <xsl:template match="/">
      <xsl:apply-templates select="/" mode="M1"/>
   </xsl:template>
   <xsl:template match="mt7:mt700" priority="4000" mode="M1">
      <xsl:choose>
         <xsl:when test="not(//mt7:field39A) or not(//mt7:field39B)"/>
         <xsl:otherwise>Either field 39A or 39B, but not both, may be present (Error D05)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(//mt7:field42C  or  not(//mt7:field42A or //mt7:field42D))       and ((//mt7:field42A or //mt7:field42D)  or not(//mt7:field42C))"/>
         <xsl:otherwise>When used, fields 42C and 42a must both be present (Error C90)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(((//mt7:field42A  and  //mt7:field42C)         and not(//mt7:field42M or //mt7:field42P)) or          (//mt7:field42M and not((//mt7:field42A  and  //mt7:field42C) or //mt7:field42P)) or          (//mt7:field42P and not((//mt7:field42A  and  //mt7:field42C) or //mt7:field42M)))          or not((//mt7:field42A  and  //mt7:field42C) or //mt7:field42M or //mt7:field42P)"/>
         <xsl:otherwise>Either fields 42C and 42a together, or field 42M alone, or field 42P alone may be present (No other combination of these fields is allowed) (Error C90)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="not(//mt7:field42C and //mt7:field42P)"/>
         <xsl:otherwise>Either fields 42C and 42a together, or field 42M alone, or field 42P alone may be present (No other combination of these fields is allowed) (Error C90)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="not(//mt7:field42C and //mt7:field42M)"/>
         <xsl:otherwise>Either fields 42C and 42a together, or field 42M alone, or field 42P alone may be present (No other combination of these fields is allowed) (Error C90)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="not(//mt7:field44C) or not(//mt7:field44D)"/>
         <xsl:otherwise>Either field 44C or 44D, but not both, may be present (Error D06)|</xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates mode="M1"/>
   </xsl:template>
   <xsl:template match="mt7:mt707" priority="3999" mode="M1">
   <!-- contains(substring(//mt7:field30, 3, 5), '/') -->
      <xsl:choose>
         <xsl:when test="//mt7:field23S and starts-with(substring(//mt7:field30, 3, 5), '/') and ends-with(substring(//mt7:field30, 3, 5), '/')">
          Date must contain a valid date expressed as YYMMDD (Error code(s): T50).
        </xsl:when>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="//mt7:field23S and starts-with(//mt7:field20, '/') or contains(substring(//mt7:field20, string-length( //mt7:field20 )), '/') or contains(//mt7:field20, '//')">
            Field 20 must not start or end with a slash and must not contain two consecutive slashes (Error T26)|
         </xsl:when>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="starts-with(//mt7:field21, '/') or contains(substring(//mt7:field21, string-length( //mt7:field21 )), '/') or contains(//mt7:field21, '//')">
            Field 21 must not start or end with a slash and must not contain two consecutive slashes (Error T26)|
       	 </xsl:when>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="//mt7:field23S and starts-with(//mt7:field23, '/') or contains(substring(//mt7:field23, string-length( //mt7:field23 )), '/') or contains(//mt7:field23, '//')">
            Field 23 must not start or end with a slash and must not contain two consecutive slashes (Error T26)|
         </xsl:when>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="//mt7:field32B and //mt7:field33B">
            Either field 32B or field 33B may have values, but not both (Error C12)|
         </xsl:when>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="not(//mt7:field39A) or not(//mt7:field39B)"/>
         <xsl:otherwise>Either field 39A or 39B, but not both, may be present (Error D05)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="not(//mt7:field44C) or not(//mt7:field44D)"/>
         <xsl:otherwise>Either field 44C or 44D, but not both, may have values (Error D06)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(not(//mt7:field32B and //mt7:field33B and //mt7:field34B)) or       (substring(//mt7:field32B/text(),1,3) = substring(//mt7:field33B/text(),1,3) and not(//mt7:field34B)) or        (substring(//mt7:field33B/text(),1,3) = substring(//mt7:field34B/text(),1,3) and not(//mt7:field32B)) or        (substring(//mt7:field32B/text(),1,3) = substring(//mt7:field34B/text(),1,3) and not(//mt7:field33B))       or       (substring(//mt7:field32B/text(),1,3) = substring(//mt7:field33B/text(),1,3) and substring(//mt7:field32B/text(),1,3) = substring(//mt7:field34B/text(),1,3))"/>
         <xsl:otherwise>The currency code in the amount fields 32B, 33B, and 34B must be the same. (Error C02)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
          <xsl:when test="(not(//mt7:field23S)) and not(//mt7:field40A) and not(//mt7:field40E) and not(//mt7:field31D) and not(//mt7:field50) and not(//mt7:field59) and not(//mt7:field32B or //mt7:field33B) and not(//mt7:field39A) and not(//mt7:field39C) and not(//mt7:field41A or //mt7:field41D) and not(//mt7:field42C) and not(//mt7:field42A) and not(//mt7:field42M) and not(//mt7:field42P) and not(//mt7:field43P) and not(//mt7:field43T) and not(//mt7:field44A) and not(//mt7:field44E) and not(//mt7:field44F) and not(//mt7:field44B) and not(//mt7:field44C) and not(//mt7:field44D) and not(//mt7:field45B) and not(//mt7:field46B) and not(//mt7:field47B) and not(//mt7:field49M) and not(//mt7:field49N) and not(//mt7:field71D) and not(//mt7:field48) and not(//mt7:field49) and not(//mt7:field58A) and not(//mt7:field53A or //mt7:field53D) and not(//mt7:field78) and not(//mt7:field57A or //mt7:field57B or //mt7:field57D) and not(//mt7:field72Z)">
            At least one field must have values on fields 40A, 40E, 31D, 50, 59, (32B or 33B), 39A, 39C, (41A or 41D), 42C, 42A, 42M, 42P, 43P, 43T, 44A, 44E, 44F, 44B, 44C, 44D, 45B, 46B, 47B, 49M, 49N, 71D, 48, 49, 58A, (53A or 53D), 78, (57A, 57B or 57D) and 72Z(Error C30)|
          </xsl:when>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="//mt7:field45B and not(contains(substring(//mt7:field45B, 1, 5), '/ADD/') or contains(substring(//mt7:field45B, 1, 8), '/DELETE/') or contains(substring(//mt7:field45B, 1, 8), '/REPALL/'))">
            One or more of the following codes(ADD, DELETE and REPALL) must be used in Code on field 45B (Error code T67)|
         </xsl:when>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="//mt7:field49M and not(contains(substring(//mt7:field49M, 1, 5), '/ADD/') or contains(substring(//mt7:field49M, 1, 8), '/DELETE/') or contains(substring(//mt7:field49M, 1, 8), '/REPALL/'))">
            One or more of the following codes(ADD, DELETE and REPALL) must be used in Code on field 49M (Error code T67)|
         </xsl:when>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="//mt7:field49N and not(contains(substring(//mt7:field49N, 1, 5), '/ADD/') or contains(substring(//mt7:field49N, 1, 8), '/DELETE/') or contains(substring(//mt7:field49N, 1, 8), '/REPALL/'))">
            One or more of the following codes(ADD, DELETE and REPALL) must be used in Code on field 49N (Error code T67)|
         </xsl:when>
      </xsl:choose>
      <xsl:apply-templates mode="M1"/>
   </xsl:template>
   <xsl:template match="mt7:mt730" priority="3998" mode="M1">
      <xsl:choose>
         <xsl:when test="not(//mt7:field25) or not(//mt7:field57A)"/>
         <xsl:otherwise>Either field 25 or 57a, but not both, may be present (Error C77)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="not(//mt7:field32D) or not(//mt7:field57A)"/>
         <xsl:otherwise>If field 32D is present, field 57a must not be present (Error C78)|</xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates mode="M1"/>
   </xsl:template>
   <xsl:template match="mt7:mt740" priority="3997" mode="M1">
      <xsl:choose>
         <xsl:when test="starts-with(//mt7:field20, '/') or contains(substring(//mt7:field20, string-length( //mt7:field20 )), '/') or contains(//mt7:field20, '//')">
            Field 20 must not start or end with a slash and must not contain two consecutive slashes (Error T26)|
         </xsl:when>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="not(//mt7:field39A) or not(//mt7:field39B)"/>
         <xsl:otherwise>Either field 39A or 39B, but not both, may be present (Error D05)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(//mt7:field42C  or  not(//mt7:field42A or //mt7:field42D))       and ((//mt7:field42A or //mt7:field42D)  or not(//mt7:field42C))"/>
         <xsl:otherwise>When used, fields 42C and 42a must both be present (Error C90)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(((//mt7:field42A  and  //mt7:field42C)         and not(//mt7:field42M or //mt7:field42P)) or          (//mt7:field42M and not((//mt7:field42A  and  //mt7:field42C) or //mt7:field42P)) or          (//mt7:field42P and not((//mt7:field42A  and  //mt7:field42C) or //mt7:field42M)))          or not((//mt7:field42A  and  //mt7:field42C) or //mt7:field42M or //mt7:field42P)"/>
         <xsl:otherwise>Either fields 42C and 42a together, or field 42M alone, or field 42P alone may be present (No other combination of these fields is allowed) (Error C90)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="not(//mt7:field58A) or not(//mt7:field59)"/>
         <xsl:otherwise>Either field 58a or 59, but not both, may be present (Error D84)|</xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates mode="M1"/>
   </xsl:template>
   <xsl:template match="mt7:mt747" priority="3996" mode="M1">
      <xsl:choose>
         <xsl:when test="//mt7:field31E or //mt7:field32B or //mt7:field33B or //mt7:field34B or            //mt7:field39A or //mt7:field39B or //mt7:field39C or //mt7:field72 or            //mt7:field77A"/>
         <xsl:otherwise>At least one of the fields 31E, 32B, 33B, 34B, 39A, 39B,39C, 72 or 77A must be present (Error C15)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="not(//mt7:field32B or //mt7:field33B) or //mt7:field34B"/>
         <xsl:otherwise>If either field 32B or 33B is present, field 34B must also be present (Error C12)|</xsl:otherwise>
      </xsl:choose>
      <xsl:if test="//mt7:field34B and (not(//mt7:field32B) and not(//mt7:field33B))">If field 34B is present, either field 32B or 33B must also be present (Error C12)|</xsl:if>
      <xsl:choose>
         <xsl:when test="not(//mt7:field39A) or not(//mt7:field39B)"/>
         <xsl:otherwise>Either field 39A or 39B, but not both, may be present (Error D05)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(not(//mt7:field32B and //mt7:field33B) or        substring(//mt7:field32B/text(),1,3) = substring(//mt7:field33B/text(),1,3)) and       (not(//mt7:field33B and //mt7:field34B) or        substring(//mt7:field33B/text(),1,3) = substring(//mt7:field34B/text(),1,3)) and       (not(//mt7:field32B and //mt7:field34B) or        substring(//mt7:field32B/text(),1,3) = substring(//mt7:field34B/text(),1,3))"/>
         <xsl:otherwise>The currency code in the amount fields 32B, 33B, and 34B must be the same (Error C02)|</xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates mode="M1"/>
   </xsl:template>
   <xsl:template match="mt7:mt750" priority="3995" mode="M1">
      <xsl:choose>
         <xsl:when test="not(//mt7:field33B or //mt7:field71B or //mt7:field73) or //mt7:field34B"/>
         <xsl:otherwise>If field 33B and/or field 71B and/or field 73 is/are present, field 34B must also be present (Error C13)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(not(//mt7:field32B and //mt7:field34B) or substring(//mt7:field32B/text(),1,3) = substring(//mt7:field34B/text(),1,3))"/>
         <xsl:otherwise>The currency code in the amount fields 32B, and 34B must be the same (Error C02)|</xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates mode="M1"/>
   </xsl:template>
   <xsl:template match="mt7:mt752" priority="3994" mode="M1">
      <xsl:choose>
         <xsl:when test="((//mt7:field32B and //mt7:field71B) and (//mt7:field33A or //mt7:field33B)) or not(//mt7:field32B and //mt7:field71B)"/>
         <xsl:otherwise>If fields 32B and 71B are both present, then field 33a must also be present (Error C18)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="((not(//mt7:field32B) and not(//mt7:field33A) and not(//mt7:field33B)) or ((substring(//mt7:field32B/text(),1,3) = substring(//mt7:field33A/text(),7,3)) and not(//mt7:field33B)) or  ((substring(//mt7:field32B/text(),1,3) = substring(//mt7:field33B/text(),1,3)) and not(//mt7:field33A)) or  (//mt7:field32B and not(//mt7:field33A) and not(//mt7:field33B)) or  (not(//mt7:field32B) and //mt7:field33A and not(//mt7:field33B)) or        (not(//mt7:field32B) and not(//mt7:field33A) and //mt7:field33B)     )"/>
         <xsl:otherwise>The currency code in the amount fields 32B and 33a must be the same (Error C02)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="((//mt7:field53A) and (//mt7:field54A) and not(substring(//mt7:field72Z, 1, 5) = '/RCB/'))">
            The code RCB should be used if both fields 53a and 54a are present|
         </xsl:when>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(not(//mt7:field53A) and not(//mt7:field54A) and (substring(//mt7:field72Z, 1, 5) = '/RCB/'))">
            The code RCB may only be used if both fields 53a and 54a are present|
         </xsl:when>
      </xsl:choose>
      <xsl:apply-templates mode="M1"/>
   </xsl:template>
   <xsl:template match="mt7:mt759" priority="3994" mode="M1">
      <xsl:choose>
         <xsl:when test="starts-with(//mt7:field20, '/') or contains(substring(//mt7:field20, string-length( //mt7:field20 )), '/') or contains(//mt7:field20, '//')">
            Field 20 must not start or end with a slash and must not contain two consecutive slashes (Error T26)|
         </xsl:when>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="starts-with(//mt7:field21, '/') or contains(substring(//mt7:field21, string-length( //mt7:field21 )), '/') or contains(//mt7:field21, '//')">
            Field 21 must not start or end with a slash and must not contain two consecutive slashes (Error T26)|
         </xsl:when>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(contains(//mt7:field23H, 'ISSUANCE') or contains(//mt7:field23H, 'REQISSUE') or contains(//mt7:field23H, 'REQAMEND') or contains(//mt7:field23H, 'ISSAMEND')) and not(contains(//mt7:field22D, 'UNDK'))">
            If field 23H is ISSUANCE, REQISSUE, REQAMEND, or ISSAMEND, then field 22D must contain UNDK (Error D87)|
         </xsl:when>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="contains(//mt7:field23H, 'TRANSFER') and not(contains(//mt7:field22D, 'DGAR') or contains(//mt7:field22D, 'STBY') or contains(//mt7:field22D, 'UNDK'))">
            If field 23H is TRANSFER, then field 22D must contain DGAR, STBY, or UNDK (Error D87)|
         </xsl:when>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(contains(//mt7:field23H, 'CLSVOPEN') or contains(//mt7:field23H, 'CLSVCLOS') or contains(//mt7:field23H, 'FRAUDMSG') or contains(//mt7:field23H, 'GENINFAD') or contains(//mt7:field23H, 'OTHERFNC') or contains(//mt7:field23H, 'REIMBURS') or contains(//mt7:field23H, 'REQFINAN')) and not(contains(//mt7:field22D, 'DGAR') or contains(//mt7:field22D, 'DOCR') or contains(//mt7:field22D, 'STBY') or contains(//mt7:field22D, 'UNDK'))">
            If field 23H is CLSVOPEN, CLSVCLOS, FRAUDMSG, GENINFAD, OTHERFNC, REIMBURS, or REQFINAN, then field 22D must contain DGAR, DOCR, STBY, or UNDK (Error D87)|
         </xsl:when>
      </xsl:choose>
      <xsl:apply-templates mode="M1"/>
   </xsl:template>
   <xsl:template match="mt7:mt734" priority="3993" mode="M1">
      <xsl:choose>
         <xsl:when test="not(//mt7:field73) or //mt7:field33A"/>
         <xsl:otherwise>If field 73 is present, field 33a must also be present (Error code(s): C17)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(     (not(//mt7:field32A) and not(//mt7:field33A) and not(//mt7:field33B)) or     ((substring(//mt7:field32A/text(),7,3) = substring(//mt7:field33A/text(),7,3)) and not(//mt7:field33B)) or     ((substring(//mt7:field32A/text(),7,3) = substring(//mt7:field33B/text(),1,3)) and not(//mt7:field33A)) or     (//mt7:field32A and not(//mt7:field33A) and not(//mt7:field33B)) or     (not(//mt7:field32A) and //mt7:field33A and not(//mt7:field33B)) or                 (not(//mt7:field32A) and not(//mt7:field33A) and //mt7:field33B)    )"/>
         <xsl:otherwise>The currency code in the amount fields 32A and 33a must be the same (Error code(s): C02)|</xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates mode="M1"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M1"/>
   <xsl:template match="text()" priority="-1"/>
</xsl:stylesheet>