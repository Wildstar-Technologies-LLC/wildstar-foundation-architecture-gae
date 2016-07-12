package com.wildstartech.gae.wfa.dao.logistics.ltl;

import java.util.logging.Logger;

import com.wildstartech.gae.wfa.dao.ticketing.TicketingShardedCounter;
import com.wildstartech.wfa.logistics.ltl.ReceiverWorkOrderLineItem;

public class ReceiverWorkOrderLineItemRequestIdGenerator 
extends TicketingShardedCounter<ReceiverWorkOrderLineItem> {
	private static String _CLASS=
			ReceiverWorkOrderLineItemRequestIdGenerator.class.getName();
	private static final Logger logger=Logger.getLogger(_CLASS);
	private static final String _PREFIX="RLI";
	
	@Override
	protected String getPrefix() {
		logger.entering(_CLASS, "getPrefix()");
		logger.exiting(_CLASS,"getPrefix()",
				ReceiverWorkOrderLineItemRequestIdGenerator._PREFIX);
		return ReceiverWorkOrderLineItemRequestIdGenerator._PREFIX;
	}
}