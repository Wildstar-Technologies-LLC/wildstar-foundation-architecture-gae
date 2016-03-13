package com.wildstartech.wfa.dao.ticketing;

import java.util.List;

import com.wildstartech.gae.wfa.dao.ticketing.BasicTicketDAOImpl;
import com.wildstartech.gae.wfa.dao.ticketing.PersistentBasicTicketImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.ticketing.BasicTicket;

public class MockBasicTicketDAO
extends BasicTicketDAOImpl<BasicTicket, PersistentBasicTicket<BasicTicket>> {

   @Override
   public PersistentBasicTicket findInstance(BasicTicket object,
         UserContext ctx) throws DAOException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public PersistentBasicTicket create() {
      PersistentBasicTicket pTicket=
            new PersistentBasicTicketImpl<BasicTicket>();
      return pTicket;
   }

   @SuppressWarnings("unchecked")
   @Override
   public PersistentBasicTicket create(BasicTicket ticket, UserContext ctx) {
      PersistentBasicTicket pTicket=
            new PersistentBasicTicketImpl<BasicTicket>();
      ((PersistentBasicTicketImpl<BasicTicket>) pTicket).populateFromObject(
            ticket);
      return pTicket;
   }

   @Override
   public List<PersistentBasicTicket<BasicTicket>> findActionable(
         UserContext ctx) throws DAOException {
      
      return null;
   }

   @Override
   public List<PersistentBasicTicket<BasicTicket>> findAllOpen(UserContext ctx)
         throws DAOException {
      
      return null;
   }
}