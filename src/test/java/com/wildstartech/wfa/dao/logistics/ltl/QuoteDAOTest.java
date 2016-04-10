/*
 * Copyright (c) 2013 - 2016 Wildstar Technologies, LLC.
 *
 * This file is part of Wildstar Foundation Architecture.
 *
 * Wildstar Foundation Architecture is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Wildstar Foundation Architecture is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Wildstar Foundation Architecture.  If not, see 
 * <http://www.gnu.org/licenses/>.
 * 
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the 
 * GNU General Public License cover the whole combination.
 * 
 * As a special exception, the copyright holders of this library give you 
 * permission to link this library with independent modules to produce an 
 * executable, regardless of the license terms of these independent modules, 
 * and to copy and distribute the resulting executable under terms of your 
 * choice, provided that you also meet, for each linked independent module, the
 * terms and conditions of the license of that module. An independent module is
 * a module which is not derived from or based on this library. If you modify 
 * this library, you may extend this exception to your version of the library, 
 * but you are not obliged to do so. If you do not wish to do so, delete this 
 * exception statement from your version.
 * 
 * If you need additional information or have any questions, please contact:
 *
 *      Wildstar Technologies, LLC.
 *      63 The Greenway Loop
 *      Panama City Beach, FL 32413
 *      USA
 *
 *      derek.berube@wildstartech.com
 *      www.wildstartech.com
 */
package com.wildstartech.wfa.dao.logistics.ltl;

import java.util.Date;
import java.util.List;

import org.testng.annotations.Test;

import com.wildstartech.gae.wfa.UserData;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.UserContextDAOFactory;
import com.wildstartech.wfa.dao.WildDAOTest;
import com.wildstartech.wfa.logistics.ltl.Quote;
import com.wildstartech.wfa.logistics.ltl.QuoteLineItem;


/**
 * Test case for the <code>QuoteDAO</code> class.
 * 
 * <p>This test case exercises the implementation of the <code>QuoteDAO</code>
 * interface from the <code>com.wildstartech.wfa.dao</code> package of the 
 * Wildstar Foundation Architecture designed to run on the Google App Engine
 * persisting data in the Datastore. The following is a description of the
 * test methods execute for this class.</p>
 * 
 * @author Derek Berube, Wildstar Technologies, LLC.
 * @version 0.1
 */
public class QuoteDAOTest extends WildDAOTest {
  PersistentQuote quote=null;
  // Quote Test Data
  // Test1
  private float test1Distance=327.63f;
  private String test1ContactName="Derek Berube";
  private String test1ContactPhone="404-444-5283";
  private String test1ContactEmail="derek.berube@me.com";
  private String test1OriginCity="Panama City Beach";
  private String test1OriginState="FL";
  private String test1OriginZip="32413";
  private String test1DestinationCity="Suwanee";
  private String test1DestinationState="GA";
  private String test1DestinationZip="30024";
  private String test1Notes="Some notes would go here.";  
 
  // Accessorial Test Data
  // Test1
  private float accessorial1Amount=100.52f; 
  private int accessorial1Quantity=1;
  private String accessorial1Description="First accessorial charge.";
  // Test 2
  private float accessorial2Amount=52.10f; 
  private int accessorial2Quantity=5;
  private String accessorial2Description="Second accessorial charge.";
  
  // Quote Line Item Test Data
  // Test 1
  private int qli1Length=10;
  private int qli1Width=20;
  private int qli1Height=30;
  private int qli1Weight=40;
  private int qli1Quantity=10;
  private String qli1Description="Test description for line item 1.";
  //Test 2
  private int qli2Length=15;
  private int qli2Width=25;
  private int qli2Height=35;
  private int qli2Weight=45;
  private int qli2Quantity=5;
  private String qli2Description="Second line item.";
  
  /**
   * Tests the basic creation of an <code>Quote</code> object.
   * 
   * <p>Performs a basic test of the data access object's ability to create an
   * object that implements both the 
   * <code>com.wildstartech.wfa.logistics.ltl.Quote</code> interface and the
   * <code>com.wildstartech.wfa.dao.logistics.ltl.PersistentQuote</code>
   * interface.</p> 
   */
  @Test
  public void testCreate() {
    QuoteDAO dao=null;
    QuoteDAOFactory factory=null;
    
    factory=new QuoteDAOFactory();
    dao=factory.getDAO();
    this.quote=(PersistentQuote) dao.create();
    assert this.quote instanceof Quote;
    assert this.quote instanceof PersistentQuote;
  }
  
  /**
   * Tests the ability to properly save an instance of a <code>Quote</code>.
   */
  @Test(dependsOnMethods = { "testCreate" })
  public void testSaveNoLineItems() {
    Date createDate=null;
    Date modifiedDate=null;
    QuoteDAO dao=null;
    QuoteDAOFactory factory=null;
    String id=null;
    String name=null;
    UserContext ctx=null;
    
    ctx=UserContextDAOFactory.authenticate(UserData.getAdminUserName(),
        UserData.getAdminPassword());
    assert ctx != null;
    // Let's prepare the data
    this.quote.setContactName(this.test1ContactName);
    this.quote.setContactPhone(this.test1ContactPhone);
    this.quote.setContactEmail(this.test1ContactEmail);
    this.quote.setOriginCity(this.test1OriginCity);
    this.quote.setOriginState(this.test1OriginState);
    this.quote.setOriginZip(this.test1OriginZip);
    this.quote.setDestinationCity(this.test1DestinationCity);
    this.quote.setDestinationState(this.test1DestinationState);
    this.quote.setDestinationZip(this.test1DestinationZip);
    this.quote.setDistance(this.test1Distance);
    this.quote.setNotes(this.test1Notes);
    // Let's get ready to save.
    factory=new QuoteDAOFactory();
    dao=factory.getDAO();    
    this.quote=(PersistentQuote) dao.save(this.quote,ctx);
    assert this.quote instanceof PersistentQuote;
    id=this.quote.getIdentifier();
    assert id != null;
    name=this.quote.getCreatedBy();
    assert name != null;
    // Ensure the user who created the object is the admin user
    assert name.compareTo(UserData.getAdminUserName()) == 0;
    name=this.quote.getModifiedBy();
    assert name != null;
    // Ensure the user who modified the object is the admin user
    assert name.compareTo(UserData.getAdminUserName()) == 0;
    // Validate dateCreated
    createDate=this.quote.getDateCreated();
    assert createDate != null;
    /* Ensure the value stored as the dateCreated is within 1000 milliseconds of
     * the present. */
    //assert new Date().getTime() - 1000 <= createDate.getTime();
    // Validate dateModified
    modifiedDate=this.quote.getDateModified();
    assert modifiedDate != null;
    // Ensure the modified date is equal to the createDate.
    assert modifiedDate.getTime() == createDate.getTime();
    
    assert this.quote.getContactName().compareTo(this.test1ContactName) == 0;
    assert this.quote.getContactEmail().compareTo(this.test1ContactEmail) == 0;
    assert this.quote.getContactPhone().compareTo(this.test1ContactPhone) == 0;
    assert this.quote.getOriginCity().compareTo(this.test1OriginCity) == 0;
    assert this.quote.getOriginState().compareTo(this.test1OriginState) == 0;
    assert this.quote.getOriginZip().compareTo(this.test1OriginZip) == 0;
    assert this.quote.getDestinationCity().compareTo(
        this.test1DestinationCity) == 0;
    assert this.quote.getDestinationState().compareTo(
        this.test1DestinationState) == 0;
    assert this.quote.getDestinationZip().compareTo(
        this.test1DestinationZip) == 0;
    assert this.quote.getDistance() == this.test1Distance;
    assert this.quote.getNotes().compareTo(this.test1Notes) == 0;    
  }
    
  @Test(dependsOnMethods = {"testSaveNoLineItems" }) 
  public void testFindByIdentifier() {
    Quote quote=null;
    QuoteDAO dao=null;
    String id=null;
    UserContext ctx=null;
    
    ctx=UserContextDAOFactory.authenticate(UserData.getAdminUserName(),
        UserData.getAdminPassword());
    dao=new QuoteDAOFactory().getDAO();
    id=this.quote.getIdentifier();
    quote=dao.findByIdentifier(id,ctx);
    assert quote.getContactName().compareTo(this.test1ContactName) == 0;
    assert quote.getContactEmail().compareTo(this.test1ContactEmail) == 0;
    assert quote.getContactPhone().compareTo(this.test1ContactPhone) == 0;
    assert quote.getOriginCity().compareTo(this.test1OriginCity) == 0;
    assert quote.getOriginState().compareTo(this.test1OriginState) == 0;
    assert quote.getOriginZip().compareTo(this.test1OriginZip) == 0;
    assert quote.getDestinationCity().compareTo(
        this.test1DestinationCity) == 0;
    assert quote.getDestinationState().compareTo(
        this.test1DestinationState) == 0;
    assert quote.getDestinationZip().compareTo(
        this.test1DestinationZip) == 0;
    assert quote.getDistance() == this.test1Distance;
    assert quote.getNotes().compareTo(this.test1Notes) == 0;   
  }
  
  /**
   * Test the method to find ALL the quotes.
   */
  @Test(dependsOnMethods = { "testSaveNoLineItems","testFindByIdentifier" })
  public void testFindAllQuotes() {
    List<PersistentQuote> quotes=null;
    QuoteDAO dao=null;
    QuoteDAOFactory factory=null;
    UserContext ctx=null;
    
    // Log in.
    ctx=UserContextDAOFactory.authenticate(UserData.getAdminUserName(),
        UserData.getAdminPassword());
    factory=new QuoteDAOFactory();
    dao=factory.getDAO();
    quotes=dao.findAll(ctx); 
    assert quotes != null;
    assert quotes.size() == 1;
  }
  
  
  @Test(dependsOnMethods = {"testFindAllQuotes"})
  public void testQuoteLineItems() {
    QuoteLineItem item=null;
    QuoteLineItemDAO dao=null;
    Date modifiedDate=null;
    QuoteDAO qDao=null;
    UserContext ctx=null;

    ctx=UserContextDAOFactory.authenticate(UserData.getAdminUserName(),
        UserData.getAdminPassword());
    dao=new QuoteLineItemDAOFactory().getDAO();
    qDao=new QuoteDAOFactory().getDAO();
    item=dao.create();
    // Populate the line item with data.
    item.setDescription("Test Line Item");
    item.setHeight(this.qli1Height);
    item.setLength(this.qli1Length);
    item.setWidth(this.qli1Width);
    item.setWeight(this.qli1Weight);
    item.setQuantity(this.qli1Quantity);
    item.setDescription(this.qli1Description);
    this.quote.addLineItem(item);
    this.quote=(PersistentQuote) qDao.save(this.quote,ctx);

    // Validate dateModified
    modifiedDate=this.quote.getDateModified();
    assert modifiedDate != null;    
  }
  
  @Test(dependsOnMethods = {"testQuoteLineItems"})
  public void testQuoteLineItemSave() {
    List<QuoteLineItem> lineItems=null;
    Quote quote=null;
    QuoteDAO dao=null;
    QuoteLineItem lineItem=null;
    String quoteId=null;
    UserContext ctx=null;
    
    ctx=UserContextDAOFactory.authenticate(UserData.getAdminUserName(),
        UserData.getAdminPassword());
    dao=new QuoteDAOFactory().getDAO();
    quoteId=this.quote.getIdentifier();
    quote=dao.findByIdentifier(quoteId,ctx);
    assert quote != null;
    lineItems=quote.getLineItems();
    assert lineItems != null;
    assert lineItems.size() == 1;
    lineItem=lineItems.get(0);
    assert lineItem != null;
    assert lineItem.getLength() == this.qli1Length;
    assert lineItem.getWidth() == this.qli1Width;
    assert lineItem.getHeight() == this.qli1Height;
    assert lineItem.getQuantity() == this.qli1Quantity;
    assert lineItem.getDescription().equals(this.qli1Description);    
  }  
  
  @Test(dependsOnMethods={"testQuoteLineItems", "testQuoteLineItemSave"})
  public void testCorrectLineItems() {
    QuoteLineItem newQli=null;
    QuoteLineItemDAO dao=null;
    List<QuoteLineItem> items=null;
    PersistentQuote quote=null;
    QuoteDAO qDao=null;
    UserContext ctx=null;
    
    ctx=UserContextDAOFactory.authenticate(UserData.getAdminUserName(),
        UserData.getAdminPassword());
    dao=new QuoteLineItemDAOFactory().getDAO();
    qDao=new QuoteDAOFactory().getDAO();
    newQli=dao.create();
    newQli.setLength(this.qli2Length);
    newQli.setWidth(this.qli2Width);
    newQli.setHeight(this.qli2Height);
    newQli.setWeight(this.qli2Weight);
    newQli.setQuantity(this.qli2Quantity);
    newQli.setDescription(this.qli2Description);
    newQli=dao.save(newQli, ctx);
    newQli=dao.findByIdentifier(
        ((PersistentQuoteLineItem) newQli).getIdentifier(), 
        ctx);
    assert newQli != null;
    quote=(PersistentQuote) 
        qDao.findByIdentifier(this.quote.getIdentifier(),ctx);
    for (int i=0; i < 1000; i++) {
      items=quote.getLineItems();
      assert items != null;
      assert items.size() == 1;
      items.get(0).setDescription("LineItem Description Updated "+i+" times.");
      quote=(PersistentQuote) qDao.save(quote, ctx);
    } // END for (int i=0; i < 1000; i++)
    
  }
}