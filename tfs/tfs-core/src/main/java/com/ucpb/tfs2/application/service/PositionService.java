package com.ucpb.tfs2.application.service;

import com.ucpb.tfs.domain.security.Position;
import com.ucpb.tfs.domain.security.PositionCode;
import com.ucpb.tfs.domain.security.PositionRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 12/26/13
 * Time: 5:38 PM
 * To change this template use File | Settings | File Templates.
 */

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class PositionService {

    @Autowired
    PositionRepository positionRepository;

    public void savePosition(Map parameter) {
        PositionCode positionCode = new PositionCode((String) parameter.get("positionCode"));

        Position position = positionRepository.loadPosition(positionCode);

        if (position == null) {
            position = new Position(positionCode);
        }

        BigDecimal signingLimit = BigDecimal.ZERO;

        if (parameter.get("signingLimit") != null && StringUtils.isNotBlank((String) parameter.get("signingLimit"))) {
            signingLimit = new BigDecimal(parameter.get("signingLimit").toString().replaceAll(",", ""));
        }

        position.setDetails(parameter.get("positionName").toString(),
                signingLimit);

        positionRepository.merge(position);
    }

    public List<Position> searchPosition(Map parameter) {

        String positionName = (String) parameter.get("positionName");

        BigDecimal signingLimitFrom = null;

        if (parameter.get("signingLimitFrom") != null && StringUtils.isNotBlank((String) parameter.get("signingLimitFrom"))) {
            signingLimitFrom = new BigDecimal(parameter.get("signingLimitFrom").toString().replace(",", ""));
        }

        BigDecimal signingLimitTo = null;

        if (parameter.get("signingLimitTo") != null && StringUtils.isNotBlank((String) parameter.get("signingLimitTo"))) {
            signingLimitTo = new BigDecimal(parameter.get("signingLimitTo").toString().replace(",", ""));
        }

        List<Position> positionList = positionRepository.getAllPositionsMatching(positionName, signingLimitFrom, signingLimitTo);

        return positionList;
    }

}
