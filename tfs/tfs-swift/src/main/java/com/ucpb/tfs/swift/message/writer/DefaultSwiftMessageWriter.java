package com.ucpb.tfs.swift.message.writer;

import com.ucpb.tfs.swift.message.Tag;
import com.ucpb.tfs.swift.message.*;
import com.ucpb.tfs.util.SimpleStringBuilder;
import org.springframework.util.StringUtils;

import java.io.File;

/**
 */
public class DefaultSwiftMessageWriter implements SwiftMessageWriter {

    public static final String SWIFT_NEWLINE = "\r\n";
    public static final String SWIFT_START_PREFIX = "/";
    
    @Override
	public String write(RawSwiftMessage source) {
		SimpleStringBuilder output = new SimpleStringBuilder();
		appendBasicHeader(output, source.getBasicHeader());
		appendApplicationHeader(output, source.getApplicationHeader());
		appendUserHeader(output, source.getUserHeader());
		appendMessageBlock(output, source.getMessageBlock());
		appendTrailer(output, source.getTrailer());

		return output.toString();
	}

	@Override
	public void write(RawSwiftMessage source, File file) {
		// TODO: for implementation
	}

	private void appendBasicHeader(SimpleStringBuilder sb, BasicHeader header) {
		if (header != null) {
			sb.append("{1:").append(header.getApplicationIdentifier())
					.append(header.getServiceIndentifier())
					.append(header.getLtIdentifier())
					.append(header.getSessionNumber())
					.append(header.getSequenceNumber())
                    .append("}");
		}
	}

	private void appendApplicationHeader(SimpleStringBuilder sb,ApplicationHeader header) {
		if (header != null) {
			sb.append("{2:").append(header.getIoIdentifier())
					.append(header.getMessageType())
					.append(header.getReceiverAddressWithLtPadding())
					.append(header.getMessagePriority())
					.append(header.getDeliveryMonitoring())
					.append(header.getObsolescencePeriod())
					.append("}");
		}
	}

	private void appendUserHeader(SimpleStringBuilder sb, UserHeader userHeader) {
		if (userHeader != null) {
			sb.append("{3:");
			for (UserTag tag : userHeader.getUserTags()) {
				sb.append("{" + tag.getTag() + ":" + tag.getValue() + "}");
			}
			sb.append("}");
		}
	}

	private void appendMessageBlock(SimpleStringBuilder sb, MessageBlock messageBlock) {
		if (messageBlock != null) {
			sb.append("{4:" + SWIFT_NEWLINE);
			for (Tag tag : messageBlock.getTags()) {
				sb.append(":" + tag.getTagName() + ":" + tag.getValue()).append(SWIFT_NEWLINE);
			}
			sb.append("-}");
		}
	}

	private void appendTrailer(SimpleStringBuilder sb, Trailer trailer) {
		// TODO: to update
	}

}
