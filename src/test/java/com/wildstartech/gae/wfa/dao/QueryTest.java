package com.wildstartech.gae.wfa.dao;

import org.testng.annotations.Test;

import com.wildstartech.wfa.dao.PropertyFilter;
import com.wildstartech.wfa.dao.Query;

public class QueryTest {
   @Test
   public void createPropertyFilter() {
      Query query=null;
      PropertyFilter<String> filter=null;
      
      filter=new PropertyFilter<String>();
      query=new QueryImpl();
      
            
   }
}