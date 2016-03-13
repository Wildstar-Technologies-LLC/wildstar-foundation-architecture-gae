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
import com.wildstartech.gae.wfa.dao.logistics.ltl.AccessorialChargeDAOImpl;
import com.wildstartech.gae.wfa.dao.logistics.ltl.PersistentAccessorialChargeImpl;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.UserContextDAOFactory;
import com.wildstartech.wfa.dao.WildDAOTest;
import com.wildstartech.wfa.dao.WildObject;
import com.wildstartech.wfa.finance.ChargeDescriptionTooLongException;
import com.wildstartech.wfa.logistics.ltl.AccessorialCharge;
/**
 * Test case for the GAE implementation of the<code>AccessorialDAO</code> class.
 * 
 * @author Derek Berube, Wildstar Technologies, LLC.
 * @version 1.0
 */
public class AccessorialDAOTest extends WildDAOTest {
  AccessorialCharge charge=null;
  // Test Case 1 Data
  double test1Amount=100.52;
  int test1Quantity=100;
  String test1Description="This is a test description.";
  String test1QuoteIdentifier="100";
  
  @Test
  public void testCreate() {
    AccessorialCharge charge=null;
    AccessorialChargeDAO dao=null;
    AccessorialChargeDAOFactory factory=null;
    
    factory=new AccessorialChargeDAOFactory();
    dao=factory.getDAO();
    assert dao != null;
    assert dao instanceof AccessorialChargeDAOImpl;
    charge=dao.create();
    assert charge != null;
    assert charge instanceof AccessorialCharge;
    assert charge instanceof PersistentAccessorialCharge;
    assert charge instanceof WildObject;
    assert charge instanceof WildObjectImpl;
    assert charge instanceof PersistentAccessorialChargeImpl;
    this.charge=charge;
  }
  
  @Test(dependsOnMethods = { "testCreate" })
  public void testSave() {
    AccessorialChargeDAO dao=null;
    Date createDate=null;
    Date modifiedDate=null;
    PersistentAccessorialCharge pCharge=null;
    UserContext ctx=null;
    
    ctx=UserContextDAOFactory.authenticate(UserData.getAdminUserName(),
        UserData.getAdminPassword());
    pCharge=(PersistentAccessorialCharge) this.charge;
    pCharge.setAmount(this.test1Amount);
    try {
      pCharge.setDescription(this.test1Description);
    } catch (ChargeDescriptionTooLongException ex) {
      // No-Op
    }
    pCharge.setQuantity(this.test1Quantity);
    pCharge.setQuoteIdentifier(this.test1QuoteIdentifier);  
    dao=new AccessorialChargeDAOFactory().getDAO();
    pCharge=(PersistentAccessorialCharge)dao.save(pCharge,ctx);
    assert pCharge != null;
    assert pCharge.getAmount().doubleValue() == this.test1Amount;
    assert pCharge.getDescription().equals(this.test1Description);
    assert pCharge.getQuantity() == this.test1Quantity;
    assert pCharge.getIdentifier() != null;
    // Ensure the user who modified the object is the admin user
    assert pCharge.getModifiedBy().compareTo(UserData.getAdminUserName()) == 0;
    // Validate dateCreated
    createDate=pCharge.getDateCreated();
    assert createDate != null;
    /* Ensure the value stored as the dateCreated is within 1000 milliseconds of
     * the present. */
    //assert new Date().getTime() - 1000 <= createDate.getTime();
    // Validate dateModified
    modifiedDate=pCharge.getDateModified();
    assert modifiedDate != null;
    // Ensure the modified date is equal to the createDate.
    assert modifiedDate.getTime() == createDate.getTime();
    // Update the refernce to the instance-level charge to be the saved version
    // of the charge.
    this.charge=pCharge;
  }
  
  @Test(dependsOnMethods = {"testSave"})
  public void testFindInstance() {
    AccessorialCharge template=null;
    PersistentAccessorialCharge foundCharge=null;
    AccessorialChargeDAO dao=null;
    UserContext ctx=null;
    
    ctx=UserContextDAOFactory.authenticate(UserData.getAdminUserName(),
        UserData.getAdminPassword());
    dao=new AccessorialChargeDAOFactory().getDAO();
    template=dao.create();
    template.setAmount(this.test1Amount);
    template.setQuantity(this.test1Quantity);
    ((PersistentAccessorialCharge)template).setQuoteIdentifier(
        this.test1QuoteIdentifier);
    try {
      template.setDescription(this.test1Description);
    } catch (ChargeDescriptionTooLongException ex) {
      // No-Op
    }
    foundCharge=dao.findInstance(template,ctx);
    assert foundCharge !=null;
    assert foundCharge.getAmount().doubleValue() == 
        template.getAmount().doubleValue();
    assert foundCharge.getDescription().equals(template.getDescription());
    assert foundCharge.getQuantity() == template.getQuantity();
    assert 
      ((PersistentAccessorialCharge)foundCharge).getQuoteIdentifier().equals(
        ((PersistentAccessorialCharge)template).getQuoteIdentifier());
    assert foundCharge.getIdentifier().equals(
        ((PersistentAccessorialCharge)this.charge).getIdentifier()
    );
  }
  @Test(dependsOnMethods = {"testSave","testFindInstance"})
  public void testFindByQuoteIdentifier() {
    PersistentAccessorialCharge foundCharge=null;
    AccessorialChargeDAO dao=null;
    List<PersistentAccessorialCharge> charges=null;
    UserContext ctx=null;
    
    ctx=UserContextDAOFactory.authenticate(UserData.getAdminUserName(),
        UserData.getAdminPassword());
    dao=new AccessorialChargeDAOFactory().getDAO();
    charges=dao.findByQuoteIdentifier(this.test1QuoteIdentifier,ctx);
    assert charges != null;
    assert charges.size() > 0;
    foundCharge=(PersistentAccessorialCharge) charges.get(0);
    assert foundCharge.getAmount().doubleValue() == this.test1Amount;
    assert foundCharge.getQuantity() == this.test1Quantity;
    assert foundCharge.getDescription().equals(this.test1Description);
    assert foundCharge.getQuoteIdentifier().equals(this.test1QuoteIdentifier);
    assert foundCharge.getIdentifier().equals(
        ((PersistentAccessorialCharge) charge).getIdentifier());
  }
}
