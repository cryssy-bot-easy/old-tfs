package com.ucpb.tfs.domain.service.utils;

import com.ucpb.tfs.interfaces.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 */
public class ReferenceNumberGenerator implements IdentifierGenerator {

    private static final String PREFIX = "TFSS";
    private static final String DATE_FORMAT = "MMddyyhh";

    public String generate(String gltsNumber) {

        StringBuilder referenceNumber = new StringBuilder(PREFIX);
        referenceNumber.append((RandomStringUtils.random(10, true, true)).toUpperCase());
        referenceNumber.append(gltsNumber);

        return referenceNumber.toString();
    }

    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        String referenceNumber = null;
        Connection connection = session.connection();
        try {
            PreparedStatement ps = connection.prepareStatement("VALUES (NEXT VALUE FOR REF_NUM_SEQUENCE)");

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                referenceNumber = PREFIX + DateUtil.formatToString(DATE_FORMAT,new Date()) + StringUtils.leftPad(String.valueOf(id), 6, '0');
            }else{
                throw new HibernateException("Failed to get value from sequence");
            }

        } catch (SQLException e) {
            throw new HibernateException("Failed to generate transaction reference number",e);
        }
        return referenceNumber;
    }
}
