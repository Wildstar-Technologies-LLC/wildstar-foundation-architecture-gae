package com.wildstartech.wfa.finance;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class SampleCreditCardData {

    /* Calculate the expiration date.
    * 
    * To get this date, three years will be added to the current year.
     */
    public static int expirationMonth = 0;
    public static int expirationYear = 0;

    static {
        Calendar calendar = null;
        calendar = new GregorianCalendar();
        calendar.add(Calendar.YEAR, 3);
        SampleCreditCardData.expirationMonth = calendar.get(Calendar.MONTH) + 1;
        SampleCreditCardData.expirationYear = calendar.get(Calendar.YEAR);
    }

    // American Express
    public static CreditCard[] amex = {
        new MockCreditCard(
        "American Express (0005)",
        "378282246310005",
        "American Express",
        "Johnny Appleseed",
        "ACME Bank",
        "1111",
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear),
        new MockCreditCard(
        "American Express (8431)",
        "371449635398431",
        "American Express",
        "Johnny Appleseed",
        "American Express Bank",
        "1212",
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear),
        new MockCreditCard(
        "American Express Corporate (1000)",
        "378734493671000",
        "American Express",
        "Johnny Appleseed",
        "American Express Bank",
        "1313",
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear)
    };
    
    // Australian Bank Card
    public static CreditCard[] austrailianBankCard = {
        new MockCreditCard(
        "Australian Bank Card (8250)",
        "5610591081018250",
        "Australian Bank Card",
        "Johnny Appleseed",
        "ACME Bank",
        "2121" ,
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear)
    };
    
    // Diners Club
    public static CreditCard[] dinersClub = {
        new MockCreditCard(
        "Diner's Club (5904)",
        "30569309025904",
        "Diner's Club",
        "Johnny Appleseed",
        "ACME Bank",
        "3131",
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear),
        new MockCreditCard(
        "Diner's Club (3237)",
        "38520000023237",
        "Diner's Club",
        "Johnny Appleseed",
        "ACME Bank",
        "3232",
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear)
    };
    
    // Discover
    public static CreditCard[] discover = {
        new MockCreditCard(
        "Discover (1117)",
        "6011111111111117",
        "Discover",
        "Johnny Appleseed",
        "ACME Bank",
        "4141",
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear),
        new MockCreditCard(
        "Discover (9424)",
        "6011000990139424",
        "Discover",
        "Johnny Appleseed",
        "ACME Bank",
        "4242",
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear)
    };

    // JCB
    public static CreditCard[] jcb = {
        new MockCreditCard(
        "JCB (0000)",
        "3530111333300000",
        "JCB",
        "Johnny Appleseed",
        "ACME Bank",
        "5151",
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear),
        new MockCreditCard(
        "JCB (0505)",
        "3566002020360505",
        "JCB",
        "Johnny Appleseed",
        "ACME Bank",
        "5252",
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear)
    };
    // MasterCard
    public static CreditCard[] masterCard = {
        new MockCreditCard(
        "MasterCard (4444)",
        "5555555555554444",
        "MasterCard",
        "Johnny Appleseed",
        "ACME Bank",
        "6161",
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear),
        new MockCreditCard(
        "MasterCard (5100)",
        "5105105105105100",
        "MasterCard",
        "Johnny Appleseed",
        "ACME Bank",
        "6262",
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear)
    };
    // Visa
    public static CreditCard[] visa = {
        new MockCreditCard(
        "Visa (1111)",
        "4111111111111111",
        "Visa",
        "Johnny Appleseed",
        "ACME Bank",
        "7171",
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear),
        new MockCreditCard(
        "Visa (1881)",
        "4012888888881881",
        "Visa",
        "Johnny Appleseed",
        "ACME Bank",
        "7272",
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear),
        new MockCreditCard(
        "Visa (2222)",
        "4222222222222",
        "Visa",
        "Johnny Appleseed",
        "ACME Bank",
        "7373",
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear)
    };
    // Dankort (PBS)
    public static CreditCard[] dankort = {
        new MockCreditCard(
        "Dankort (4561)",
        "76009244561",
        "Dankort",
        "Johnny Appleseed",
        "ACME Bank",
        "8181",
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear),
        new MockCreditCard(
        "Dankort (3742)",
        "5019717010103742",
        "Dankort",
        "Johnny Appleseed",
        "ACME Bank",
        "8282",
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear)
    };
    // Switch/Solo (Paymentech)
    public static CreditCard[] switchSolo = {
        new MockCreditCard(
        "Switch/Solo (0016)",
        "6331101999990016",
        "Visa",
        "Johnny Appleseed",
        "ACME Bank",
        "9191",
        SampleCreditCardData.expirationMonth,
        SampleCreditCardData.expirationYear)
    };
}