/*
 * Copyright (c) 2013 - 2015 Wildstar Technologies, LLC.
 *
 * This file is part of Wildstar Foundation Architecture for Google App Engine.
 *
 * Wildstar Foundation Architecture for Google App Engine is free software: you
 * can redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either version
 * 3 of the License, or (at your option) any later version.
 *
 * Wildstar Foundation Architecture for Google App Engine is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Wildstar Foundation Architecture for Google App Engine.  If not, see 
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
import com.wildstartech.gae.wfa.dao.WildObjectImpl;
import com.wildstartech.gae.wfa.dao.logistics.ltl.PersistentQuoteLineItemImpl;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.UserContextDAOFactory;
import com.wildstartech.wfa.dao.WildDAOTest;
import com.wildstartech.wfa.dao.WildObject;
import com.wildstartech.wfa.logistics.ltl.QuoteLineItem;

public class QuoteLineItemDAOTest extends WildDAOTest {
  private PersistentQuoteLineItem pQli = null;
  // Test case 1 data
  private int test1Height = 30;
  private int test1Length = 10;
  private int test1Quantity = 5;
  private int test1Weight = 40;
  private int test1Width = 20;
  private String test1Description = "This is a test description.";
  private String test1QuoteIdentifier = "999";

  @Test
  public void testCreate() {
    QuoteLineItem qli = null;
    QuoteLineItemDAO dao = null;

    dao = new QuoteLineItemDAOFactory().getDAO();
    qli = dao.create();
    assert qli instanceof QuoteLineItem;
    assert qli instanceof PersistentQuoteLineItem;
    assert qli instanceof WildObject;
    assert qli instanceof WildObjectImpl;
    assert qli instanceof PersistentQuoteLineItemImpl;
    this.pQli = (PersistentQuoteLineItem) qli;
  }

  @Test(dependsOnMethods = { "testCreate" })
  public void testSave() {
    Date createDate = null;
    Date modifiedDate = null;
    QuoteLineItemDAO dao = null;
    PersistentQuoteLineItem savedQli = null;
    UserContext ctx = null;

    ctx = UserContextDAOFactory.authenticate(UserData.getAdminUserName(),
        UserData.getAdminPassword());
    dao = new QuoteLineItemDAOFactory().getDAO();
    this.pQli.setDescription(this.test1Description);
    this.pQli.setLength(this.test1Length);
    this.pQli.setWidth(this.test1Width);
    this.pQli.setHeight(this.test1Height);
    this.pQli.setQuantity(this.test1Quantity);
    this.pQli.setWeight(this.test1Weight);
    this.pQli.setQuoteIdentifier(this.test1QuoteIdentifier);
    savedQli = (PersistentQuoteLineItem) dao.save(this.pQli, ctx);
    assert savedQli.getLength() == this.test1Length;
    assert savedQli.getWidth() == this.test1Width;
    assert savedQli.getHeight() == this.test1Height;
    assert savedQli.getWeight() == this.test1Weight;
    assert savedQli.getQuoteIdentifier().equals(this.test1QuoteIdentifier);

    // Ensure the user who modified the object is the admin user
    assert savedQli.getModifiedBy().compareTo(UserData.getAdminUserName()) == 0;
    // Validate dateCreated
    createDate = savedQli.getDateCreated();
    assert createDate != null;
    /*
     * Ensure the value stored as the dateCreated is within 1000 milliseconds of
     * the present.
     */
    // assert new Date().getTime() - 1000 <= createDate.getTime();
    // Validate dateModified
    modifiedDate = savedQli.getDateModified();
    assert modifiedDate != null;
    // Ensure the modified date is equal to the createDate.
    assert modifiedDate.getTime() == createDate.getTime();
  }

  @Test(dependsOnMethods = { "testSave" })
  public void testFindByIdentifier() {
    QuoteLineItemDAO dao = null;
    PersistentQuoteLineItem found = null;
    String identifier = null;
    UserContext ctx = null;

    ctx = UserContextDAOFactory.authenticate(UserData.getAdminUserName(),
        UserData.getAdminPassword());
    dao = new QuoteLineItemDAOFactory().getDAO();
    identifier = this.pQli.getIdentifier();
    found = (PersistentQuoteLineItem) dao.findByIdentifier(identifier, ctx);
    assert found != null;
    assert this.pQli.getIdentifier().equals(found.getIdentifier());
    assert this.pQli.getCreatedBy().equals(found.getCreatedBy());
    assert this.pQli.getDateCreated().getTime() == found.getDateCreated()
        .getTime();
    assert this.pQli.getModifiedBy().equals(found.getModifiedBy());
    assert this.pQli.getDateModified().getTime() == found.getDateModified()
        .getTime();
    assert this.pQli.getHeight() == found.getHeight();
    assert this.pQli.getLength() == found.getLength();
    assert this.pQli.getQuantity() == found.getQuantity();
    assert this.pQli.getQuoteIdentifier().equals(found.getQuoteIdentifier());
    assert this.pQli.getWeight() == found.getWeight();
    assert this.pQli.getWidth() == found.getWidth();
  }

  @Test(dependsOnMethods = { "testSave" })
  public void testFindByQuoteIdentifier() {
    QuoteLineItemDAO dao = null;
    PersistentQuoteLineItem found = null;
    List<PersistentQuoteLineItem> lineItems = null;
    String quoteIdentifier = null;
    UserContext ctx = null;

    ctx = UserContextDAOFactory.authenticate(UserData.getAdminUserName(),
        UserData.getAdminPassword());
    dao = new QuoteLineItemDAOFactory().getDAO();
    quoteIdentifier = this.pQli.getQuoteIdentifier();
    lineItems = dao.findByQuoteIdentifier(quoteIdentifier, ctx);
    assert lineItems != null;
    assert lineItems.size() == 1;
    found = (PersistentQuoteLineItem) lineItems.get(0);
    assert found != null;
    assert this.pQli.getIdentifier().equals(found.getIdentifier());
    assert this.pQli.getCreatedBy().equals(found.getCreatedBy());
    assert this.pQli.getDateCreated().getTime() == found.getDateCreated()
        .getTime();
    assert this.pQli.getModifiedBy().equals(found.getModifiedBy());
    assert this.pQli.getDateModified().getTime() == found.getDateModified()
        .getTime();
    assert this.pQli.getHeight() == found.getHeight();
    assert this.pQli.getLength() == found.getLength();
    assert this.pQli.getQuantity() == found.getQuantity();
    assert this.pQli.getQuoteIdentifier().equals(found.getQuoteIdentifier());
    assert this.pQli.getWeight() == found.getWeight();
    assert this.pQli.getWidth() == found.getWidth();
  }

  @Test(dependsOnMethods = { "testSave" })
  public void testFindInstance() {
    PersistentQuoteLineItem found = null;
    QuoteLineItem template = null;
    QuoteLineItemDAO dao = null;
    UserContext ctx = null;

    ctx = UserContextDAOFactory.authenticate(UserData.getAdminUserName(),
        UserData.getAdminPassword());
    dao=new QuoteLineItemDAOFactory().getDAO();
    template=dao.create();
    ((PersistentQuoteLineItem) template).setQuoteIdentifier(
        this.test1QuoteIdentifier);
    template.setDescription(this.test1Description);
    template.setLength(this.test1Length);
    template.setWidth(this.test1Width);
    template.setHeight(this.test1Height);
    template.setQuantity(this.test1Quantity);
    template.setWeight(this.test1Weight);
    found=dao.findInstance(template, ctx);
    
    assert found != null;
    assert this.pQli.getIdentifier().equals(found.getIdentifier());
    assert this.pQli.getCreatedBy().equals(found.getCreatedBy());
    assert this.pQli.getDateCreated().getTime() == found.getDateCreated()
        .getTime();
    assert this.pQli.getModifiedBy().equals(found.getModifiedBy());
    assert this.pQli.getDateModified().getTime() == found.getDateModified()
        .getTime();
    assert this.pQli.getHeight() == found.getHeight();
    assert this.pQli.getLength() == found.getLength();
    assert this.pQli.getQuantity() == found.getQuantity();
    assert this.pQli.getQuoteIdentifier().equals(found.getQuoteIdentifier());
    assert this.pQli.getWeight() == found.getWeight();
    assert this.pQli.getWidth() == found.getWidth();
  }

}
