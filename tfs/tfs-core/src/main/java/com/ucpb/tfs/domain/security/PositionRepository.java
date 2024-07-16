package com.ucpb.tfs.domain.security;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 11/28/13
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PositionRepository {

    public Position loadPosition(PositionCode code);

    public List<Position> getAllPositions();

    public void merge(Position position);

    public List<Position> getAllPositionsMatching(String positionName, BigDecimal signingLimitFrom, BigDecimal signingLimitTo);
}

