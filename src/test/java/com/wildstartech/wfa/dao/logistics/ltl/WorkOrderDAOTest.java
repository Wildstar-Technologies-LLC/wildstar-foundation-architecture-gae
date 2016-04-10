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
import com.wildstartech.gae.wfa.dao.logistics.ltl.PersistentWorkOrderImpl;
import com.wildstartech.gae.wfa.dao.logistics.ltl.PersistentWorkOrderLineItemImpl;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.UserContextDAOFactory;
import com.wildstartech.wfa.dao.WildDAOTest;
import com.wildstartech.wfa.logistics.ltl.WorkOrder;
import com.wildstartech.wfa.logistics.ltl.WorkOrderLineItem;

public class WorkOrderDAOTest extends WildDAOTest {

    PersistentWorkOrder workOrder = null;

    // Work Order Test Data
    // Test1
    private float test1Distance = 327.63f;
    private String test1ContactName = "Derek Berube";
    private String test1ContactPhone = "404-444-5283";
    private String test1ContactEmail = "derek.berube@me.com";
    private String test1OriginCity = "Panama City Beach";
    private String test1OriginState = "FL";
    private String test1OriginZip = "32413";
    private String test1DestinationCity = "Suwanee";
    private String test1DestinationState = "GA";
    private String test1DestinationZip = "30024";
    private String test1Notes = "Some notes would go here.";

    // Accessorial Test Data
    // Test1
    private float accessorial1Amount = 100.52f;
    private int accessorial1Quantity = 1;
    private String accessorial1Description = "First accessorial charge.";
    // Test 2
    private float accessorial2Amount = 52.10f;
    private int accessorial2Quantity = 5;
    private String accessorial2Description = "Second accessorial charge.";

    // Quote Line Item Test Data
    // Test 1
    private int wli1Length = 10;
    private int wli1Width = 20;
    private int wli1Height = 30;
    private int wli1Weight = 40;
    private int wli1Quantity = 10;
    private String wli1ProductId = "1stIDofProduct";
    private String wli1Description = "Test description for line item 1.";

    // Test 2
    private int wli2Length = 15;
    private int wli2Width = 25;
    private int wli2Height = 35;
    private int wli2Weight = 45;
    private int wli2Quantity = 5;
    private String wli2ProductId = "2ndIDofProduct";
    private String wli2Description = "Second line item.";

    @Test
    public void testCreate() {
        WorkOrderDAOFactory factory = null;
        WorkOrderDAO dao = null;

        factory = new WorkOrderDAOFactory();
        dao = factory.getDAO();
        this.workOrder = dao.create();
        assert this.workOrder != null;
        assert this.workOrder instanceof PersistentWorkOrderImpl;
        assert this.workOrder instanceof WorkOrder;
    }

    @Test(dependsOnMethods = {"testCreate"})
    public void testSaveNoLineItems() {
        Date createDate = null;
        Date modifiedDate = null;
        WorkOrderDAO dao = null;
        WorkOrderDAOFactory factory = null;
        String id = null;
        String name = null;
        UserContext ctx = null;

        ctx = UserContextDAOFactory.authenticate(UserData.getAdminUserName(),
                UserData.getAdminPassword());
        assert ctx != null;
        // Let's prepare the data
        this.workOrder.setContactName(this.test1ContactName);
        this.workOrder.setContactPhone(this.test1ContactPhone);
        this.workOrder.setContactEmail(this.test1ContactEmail);
        this.workOrder.setOriginCity(this.test1OriginCity);
        this.workOrder.setOriginState(this.test1OriginState);
        this.workOrder.setOriginZip(this.test1OriginZip);
        this.workOrder.setDestinationCity(this.test1DestinationCity);
        this.workOrder.setDestinationState(this.test1DestinationState);
        this.workOrder.setDestinationZip(this.test1DestinationZip);
        this.workOrder.setDistance(this.test1Distance);
        this.workOrder.setNotes(this.test1Notes);
        // Let's get ready to save.
        factory = new WorkOrderDAOFactory();
        dao = factory.getDAO();
        this.workOrder = (PersistentWorkOrder) dao.save(this.workOrder, ctx);
        assert this.workOrder instanceof PersistentWorkOrder;
        id = this.workOrder.getIdentifier();
        assert id != null;
        name = this.workOrder.getCreatedBy();
        assert name != null;
        // Ensure the user who created the object is the admin user
        assert name.compareTo(UserData.getAdminUserName()) == 0;
        name = this.workOrder.getModifiedBy();
        assert name != null;
        // Ensure the user who modified the object is the admin user
        assert name.compareTo(UserData.getAdminUserName()) == 0;
        // Validate dateCreated
        createDate = this.workOrder.getDateCreated();
        assert createDate != null;
        /* Ensure the value stored as the dateCreated is within 1000 milliseconds of
       * the present. */
        //assert new Date().getTime() - 1000 <= createDate.getTime();
        // Validate dateModified
        modifiedDate = this.workOrder.getDateModified();
        assert modifiedDate != null;
        // Ensure the modified date is equal to the createDate.
        assert modifiedDate.getTime() == createDate.getTime();

        assert this.workOrder.getContactName().compareTo(this.test1ContactName) == 0;
        assert this.workOrder.getContactEmail().compareTo(this.test1ContactEmail) == 0;
        assert this.workOrder.getContactPhone().compareTo(this.test1ContactPhone) == 0;
        assert this.workOrder.getOriginCity().compareTo(this.test1OriginCity) == 0;
        assert this.workOrder.getOriginState().compareTo(this.test1OriginState) == 0;
        assert this.workOrder.getOriginZip().compareTo(this.test1OriginZip) == 0;
        assert this.workOrder.getDestinationCity().compareTo(
                this.test1DestinationCity) == 0;
        assert this.workOrder.getDestinationState().compareTo(
                this.test1DestinationState) == 0;
        assert this.workOrder.getDestinationZip().compareTo(
                this.test1DestinationZip) == 0;
        assert this.workOrder.getDistance() == this.test1Distance;
        assert this.workOrder.getNotes().compareTo(this.test1Notes) == 0;
    }

    @Test(dependsOnMethods = {"testSaveNoLineItems"})
    public void testFindByIdentifier() {
        String workOrderId = null;
        UserContext ctx = null;
        WorkOrderDAOFactory factory = null;
        WorkOrderDAO dao = null;
        WorkOrder workOrder = null;

        // Log in.
        ctx = UserContextDAOFactory.authenticate(
                UserData.getAdminUserName(),
                UserData.getAdminPassword()
        );
        workOrderId = this.workOrder.getIdentifier();
        factory = new WorkOrderDAOFactory();
        dao = factory.getDAO();
        workOrder = dao.findByIdentifier(workOrderId, ctx);
        assert workOrder.getContactName().compareTo(this.test1ContactName) == 0;
        assert workOrder.getContactEmail().compareTo(this.test1ContactEmail) == 0;
        assert workOrder.getContactPhone().compareTo(this.test1ContactPhone) == 0;
        assert workOrder.getOriginCity().compareTo(this.test1OriginCity) == 0;
        assert workOrder.getOriginState().compareTo(this.test1OriginState) == 0;
        assert workOrder.getOriginZip().compareTo(this.test1OriginZip) == 0;
        assert workOrder.getDestinationCity().compareTo(
                this.test1DestinationCity) == 0;
        assert workOrder.getDestinationState().compareTo(
                this.test1DestinationState) == 0;
        assert workOrder.getDestinationZip().compareTo(
                this.test1DestinationZip) == 0;
        assert workOrder.getNotes().compareTo(this.test1Notes) == 0;
    }

    @Test(dependsOnMethods = {"testSaveNoLineItems"})
    public void testFindAll() {
        List<PersistentWorkOrder> workOrders = null;
        UserContext ctx = null;
        WorkOrderDAOFactory factory = null;
        WorkOrderDAO dao = null;

        // Log in.
        ctx = UserContextDAOFactory.authenticate(
                UserData.getAdminUserName(),
                UserData.getAdminPassword()
        );
        factory = new WorkOrderDAOFactory();
        dao = factory.getDAO();
        workOrders = dao.findAll(ctx);
        assert workOrders != null;
        assert workOrders.size() == 1;
    }

    @Test(dependsOnMethods = {"testFindAll"})
    public void testWorkOrderLineItemCreate() {
        WorkOrderLineItemDAOFactory factory = null;
        WorkOrderLineItemDAO dao = null;
        WorkOrderLineItem lineItem = null;

        factory = new WorkOrderLineItemDAOFactory();
        dao = factory.getDAO();
        assert dao != null;
        lineItem = dao.create();
        assert lineItem != null;
        assert lineItem instanceof WorkOrderLineItem;
        assert lineItem instanceof PersistentWorkOrderLineItem;
        assert lineItem instanceof PersistentWorkOrderLineItemImpl;
    }

    @Test(dependsOnMethods = {"testWorkOrderLineItemCreate"})
    public void testLineItemSave() {
        List<WorkOrderLineItem> lineItems = null;
        UserContext ctx = null;
        WorkOrder workOrder = null;
        WorkOrderDAO wDAO = null;
        WorkOrderDAOFactory wDAOFactory = null;
        WorkOrderLineItemDAOFactory factory = null;
        WorkOrderLineItemDAO dao = null;
        WorkOrderLineItem lineItem = null;

        // Log in.
        ctx = UserContextDAOFactory.authenticate(
                UserData.getAdminUserName(),
                UserData.getAdminPassword()
        );
        // Get a reference to the WorkOrderDAO
        wDAOFactory = new WorkOrderDAOFactory();
        wDAO = wDAOFactory.getDAO();
        // Get a reference to the WorkOrderQuoteLineItemDAOFactory
        factory = new WorkOrderLineItemDAOFactory();
        dao = factory.getDAO();

        workOrder = wDAO.findByIdentifier(this.workOrder.getIdentifier(), ctx);
        assert workOrder != null;

        lineItem = dao.create();
        // Populate the line item with data.
        lineItem.setDescription("Test Line Item");
        lineItem.setHeight(this.wli1Height);
        lineItem.setLength(this.wli1Length);
        lineItem.setWidth(this.wli1Width);
        lineItem.setWeight(this.wli1Weight);
        lineItem.setQuantity(this.wli1Quantity);
        lineItem.setDescription(this.wli1Description);
        lineItem.setProductId(this.wli1ProductId);
        lineItem.setQuantity(this.wli1Quantity);
        workOrder.addLineItem(lineItem);
        workOrder = wDAO.save(workOrder, ctx);

        // Let's check the saved work order
        lineItems = workOrder.getLineItems();
        assert lineItems != null;
        assert lineItems.size() == 1;

        lineItem = lineItems.get(0);
        assert lineItem != null;
        assert lineItem.getDescription().equalsIgnoreCase(this.wli1Description);
        assert lineItem.getProductId().equalsIgnoreCase(this.wli1ProductId);
        assert lineItem.getLength() == this.wli1Length;
        assert lineItem.getHeight() == this.wli1Height;
        assert lineItem.getWidth() == this.wli1Width;
        assert lineItem.getWeight() == this.wli1Weight;
        assert lineItem.getQuantity() == this.wli1Quantity;

    }
}
