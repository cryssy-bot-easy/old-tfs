package com.ucpb.tfs.domain.product;

public interface BookingSettlementRepository {

	public void persist(BookingSettlement bookingSettlement);

    public void update(BookingSettlement bookingSettlement);

    public void merge(BookingSettlement bookingSettlement);

    public BookingSettlement load(DocumentNumber documentNumber);
}
