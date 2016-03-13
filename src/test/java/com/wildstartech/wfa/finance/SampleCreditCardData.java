package com.wildstartech.wfa.finance;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class SampleCreditCardData {
   /* Calculate the expiration date.
    * 
    * To get this date, three years will be added to the current year.  Then the
    * calendar will be set equal to the 12:00:00.0000 on the first of that 
    * month. One millisecond will be subtracted from that date to get 
    * 11:59:59.9999 PM on the last day of the previous month.
    * 
    * E.g., if today is February 15, 2016, then the expiration date will be set 
    * equal to January 31, 2019 11:59:59.0001 PM.
    */
   public static Date expirationDate=null;
   static {
      Calendar calendar=null;
      calendar=new GregorianCalendar();
      calendar.add(Calendar.YEAR, 3);
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      calendar.add(Calendar.MILLISECOND, -1);
      SampleCreditCardData.expirationDate=calendar.getTime();
   }
   
   // American Express
   public static CreditCard[] amex={
         new MockCreditCard(
               "American Express (0005)",
               "378282246310005",
               "American Express",
               "Johnny Appleseed",
               "ACME Bank",
               "1111",
               SampleCreditCardData.expirationDate),
         new MockCreditCard(
               "American Express (8431)",
               "371449635398431",
               "American Express",
               "Johnny Appleseed",
               "American Express Bank",
               "1212",
               SampleCreditCardData.expirationDate),
         new MockCreditCard(
               "American Express Corporate (1000)",
               "378734493671000",
               "American Express",
               "Johnny Appleseed",
               "American Express Bank",
               "1313",
               SampleCreditCardData.expirationDate)
   };
   // Australian Bank Card
   public static CreditCard[] austrailianBankCard={
         new MockCreditCard(
               "Australian Bank Card (8250)",
               "5610591081018250",
               "Australian Bank Card",
               "Johnny Appleseed",
               "ACME Bank",
               "2121",
               SampleCreditCardData.expirationDate)  
   };
   // Diners Club
   public static CreditCard[] dinersClub={
         new MockCreditCard(
               "Diner's Club (5904)",
               "30569309025904",
               "Diner's Club",
               "Johnny Appleseed",
               "ACME Bank",
               "3131",
               SampleCreditCardData.expirationDate),
         new MockCreditCard(
               "Diner's Club (3237)",
               "38520000023237",
               "Diner's Club",
               "Johnny Appleseed",
               "ACME Bank",
               "3232",
               SampleCreditCardData.expirationDate)
   };
   // Discover
   public static CreditCard[] discover={
         new MockCreditCard(
               "Discover (1117)",
               "6011111111111117",
               "Discover",
               "Johnny Appleseed",
               "ACME Bank",
               "4141",
               SampleCreditCardData.expirationDate),
         new MockCreditCard(
               "Discover (9424)",
               "6011000990139424",
               "Discover",
               "Johnny Appleseed",
               "ACME Bank",
               "4242",
               SampleCreditCardData.expirationDate)
   };
   // JCB
   public static CreditCard[] jcb={
         new MockCreditCard(
               "JCB (0000)",
               "3530111333300000",
               "JCB",
               "Johnny Appleseed",
               "ACME Bank",
               "5151",
               SampleCreditCardData.expirationDate),
         new MockCreditCard(
               "JCB (0505)",
               "3566002020360505",
               "JCB",
               "Johnny Appleseed",
               "ACME Bank",
               "5252",
               SampleCreditCardData.expirationDate)
   };
   // MasterCard
   public static CreditCard[] masterCard={
         new MockCreditCard(
               "MasterCard (4444)",
               "5555555555554444",
               "MasterCard",
               "Johnny Appleseed",
               "ACME Bank",
               "6161",
               SampleCreditCardData.expirationDate),
         new MockCreditCard(
               "MasterCard (5100)",
               "5105105105105100",
               "MasterCard",
               "Johnny Appleseed",
               "ACME Bank",
               "6262",
               SampleCreditCardData.expirationDate)
   };
   // Visa
   public static CreditCard[] visa={
         new MockCreditCard(
               "Visa (1111)",
               "4111111111111111",
               "Visa",
               "Johnny Appleseed",
               "ACME Bank",
               "7171",
               SampleCreditCardData.expirationDate),
         new MockCreditCard(
               "Visa (1881)",
               "4012888888881881",
               "Visa",
               "Johnny Appleseed",
               "ACME Bank",
               "7272",
               SampleCreditCardData.expirationDate),
         new MockCreditCard(
               "Visa (2222)",
               "4222222222222",
               "Visa",
               "Johnny Appleseed",
               "ACME Bank",
               "7373",
               SampleCreditCardData.expirationDate)
   };
   // Dankort (PBS)
   public static CreditCard[] dankort={
         new MockCreditCard(
               "Dankort (4561)",
               "76009244561",
               "Dankort",
               "Johnny Appleseed",
               "ACME Bank",
               "8181",
               SampleCreditCardData.expirationDate),
         new MockCreditCard(
               "Dankort (3742)",
               "5019717010103742",
               "Dankort",
               "Johnny Appleseed",
               "ACME Bank",
               "8282",
               SampleCreditCardData.expirationDate)
   };
   // Switch/Solo (Paymentech)
   public static CreditCard[] switchSolo={
         new MockCreditCard(
               "Switch/Solo (0016)",
               "6331101999990016",
               "Visa",
               "Johnny Appleseed",
               "ACME Bank",
               "9191",
               SampleCreditCardData.expirationDate)
   };   
}