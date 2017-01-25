package com.wildstartech.gae.wfa.dao.logistics.ltl.receiver;

import java.util.logging.Logger;

import com.wildstartech.gae.wfa.dao.ticketing.TicketingShardedCounter;
import com.wildstartech.wfa.logistics.ltl.receiver.ReceiverWorkOrder;

public class ReceiverWorkOrderRequestIdGenerator 
extends TicketingShardedCounter<ReceiverWorkOrder> {
	private static String _CLASS=
			ReceiverWorkOrderRequestIdGenerator.class.getName();
	private static final Logger logger=Logger.getLogger(_CLASS);
	
	private static final String _PREFIX="RCV";
	@Override
	protected String getPrefix() {
		logger.entering(_CLASS, "getPrefix()");
		logger.exiting(_CLASS,"getPrefix()",
				ReceiverWorkOrderRequestIdGenerator._PREFIX);
		return ReceiverWorkOrderRequestIdGenerator._PREFIX;
	}
}