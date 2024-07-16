package com.ucpb.tfs.domain.mtmessage;

import org.springframework.beans.factory.annotation.Autowired;

/**
 */
public class MtMessageServiceImpl implements MtMessageService{

    @Autowired
    private MtMessageRepository mtMessageRepository;

    @Override
    public void persist(MtMessage message) {
        printMtMessage(message);
        mtMessageRepository.persist(message);
    }

    private void printMtMessage(MtMessage message) {

        System.out.println("id:"+message.getId());
        System.out.println("filename:"+message.getFilename());
        System.out.println("message class:"+message.getMessageClass());
        System.out.println("mt type:"+message.getMtType());
        System.out.println("sequence number:"+message.getSequenceNumber());
        System.out.println("sequence total:"+message.getSequenceTotal());
        System.out.println("tradeservice id:"+message.getTradeServiceId());
        System.out.println("documentNumber:"+message.getDocumentNumber());
        System.out.println("dateReceived:"+message.getDateReceived());
        System.out.println("instruction:"+message.getInstruction());
        System.out.println("direction:"+message.getMtDirection());
        System.out.println("status:"+message.getMtStatus());
        System.out.println("userRoutedTo:"+message.getUserRoutedTo());
        System.out.println("tradeservicereferencenumber:"+message.getTradeServiceReferenceNumber());
        System.out.println("message:"+message.getMessage());
    }


}
