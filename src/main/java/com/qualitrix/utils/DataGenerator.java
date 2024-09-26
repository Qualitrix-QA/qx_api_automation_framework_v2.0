package com.qualitrix.utils;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import org.junit.Test;

import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {


    /**
     * Generates Random FirstName using Faker Library
     * Generally required to create Player FirstName at runtime
     *
     * @return FirstName as string
     */
    public String generateFirstName() {
        Faker faker = new Faker(new Locale("en-GB"));
        //Sometimes the first name contains apostrophe, it needs to be removed
        return faker.name().firstName().replace("'", "");
    }

    /**
     * Generates Random LastName using Faker Library
     * Useful to create Player LastName at runtime
     *
     * @return LastName as string
     */
    public String generateLastName() {
        Faker faker = new Faker(new Locale("en-GB"));
        //Sometimes the Last name contains apostrophe, it needs to be removed
        return faker.name().lastName().replace("'", "");
    }

    /**
     * Generates Random UK Mobile Number.
     * The Mobile number starts with 07 followed by 9 digits.
     *
     * @return Mobile Number as string
     */
    public String generateMobileNumber() {
        Faker faker = new Faker(new Locale("en-GB"));
        //UK Mobile number starts with 07
        return faker.regexify("07[0-9]{9}");
    }

    /**
     * Generates Date of Birth in a specific Date Format mentioned by the Parameter 'argDateFormat' .
     * The Date of Birth is generated in such a way that the age of the player is between 20 and 70
     *
     * @param argDateFormat
     * @return DoB as string
     */
    public String generateDateOfBirth(String argDateFormat) {
        Faker faker = new Faker(new Locale("en-GB"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(argDateFormat);
        return simpleDateFormat.format(faker.date().birthday(25, 70));
    }

    /**
     * Generates Password for the Player Account.
     * The 13 Digit Password starts with uppercase followed by mixed of lowercase, numbers and acceptable special characters
     *
     * @return Password as string
     */
    public String generatePassword() {
        Faker faker = new Faker(new Locale("en-GB"));
        //SOMETIMES PASSWORD IS GENERATED STARTING WITH SPECIAL CHARACTER WHICH IS NOT ACCEPTABLE IN FEW INSTANCES
        return faker.regexify("[A-Z]{2}[a-z]{2}[0-9]{2}[!@#$%^*_()?]{2}[a-z]{5}");
    }

    /**
     * Generates random UK Address
     *
     * @return Address as string
     */
    public Address generateAddress() {
        Faker faker = new Faker(new Locale("en-GB"));
        return faker.address();
    }

    /**
     * Generates 6 Digit Sort Code.
     *
     * @return sortcode as string
     */
    public String generateBankSortCode() {
        Faker faker = new Faker(new Locale("en-GB"));
        return faker.regexify("[0-9]{2}-[1-9]{2}-[1-9]{2}");
    }

    /**
     * Generates 8 Digit Bank Account number with first digit being non-zero number.
     *
     * @return Bank account number as string
     */
    public String generateBankAccountNumber() {
        Faker faker = new Faker(new Locale("en-GB"));
        return faker.regexify("[1-9]{1}[0-9]{7}");
    }

    /**
     * locates the property(identified by map key) and substitutes the Property values with the values in the Map Object .
     *
     * @param argFilePath          - Path of the API Payload Template File
     * @param argPlaceHolderValues Json Property-value pairs
     * @return API Request payload as string
     */
    public String generateJsonPayload(String argFilePath, Map<String, String> argPlaceHolderValues) {
        try {
            // Create a Mustache instance and compile the template from the file
            Mustache mustache = new DefaultMustacheFactory().compile(new FileReader(argFilePath), "dtotemplate.mustache");

            // Replace the placeholders with the actual values
            StringWriter writer = new StringWriter();
            mustache.execute(writer, argPlaceHolderValues).flush();

            // Get the resulting JSON payload
            return writer.toString();
        } catch (Exception argException) {
            argException.printStackTrace();
            return null;
        }
    }

    public String updateJsonPayload(String argString, Map<String, Object> argPlaceHolderValues) {
        try {
            // Create a Mustache instance and compile the template from the file
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(argString), "dtotemplate.mustache");

            // Replace the placeholders with the actual values
            StringWriter writer = new StringWriter();
            mustache.execute(writer, argPlaceHolderValues).flush();

            // Get the resulting JSON payload
            return writer.toString();
        } catch (Exception argException) {
            argException.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the random number between two integers.
     *
     * @param argMin
     * @param argMax
     * @return Random integer value
     */
    public int getARandomNumber(int argMin, int argMax) {
        try {
            int randomNum = ThreadLocalRandom.current().nextInt(argMin, argMax + 1);
            return randomNum;
        } catch (Exception argException) {
            argException.printStackTrace();
        }
        return 0;
    }

    @Test
    public void generateCardTest() {
        String cardNumber = generateCardNumber(CardType.MAESTRO_16);
        System.out.println("Card Number generated: " + cardNumber);
    }

    /***
     * Given the number, the function generates the check sum digit.
     * CheckSum Digit is helpful to generate the Card Number
     * @param argNumber
     * @return
     */
    private int calculateCheckDigit(String argNumber) {
        int sum = 0;
        for (int lengthIter = 0; lengthIter < argNumber.length(); lengthIter++) {
            int digit = Integer.parseInt((argNumber.substring(lengthIter, lengthIter + 1)));
            if (lengthIter % 2 == 0) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
        }
        return (10 - (sum % 10)) % 10;
    }

    /**
     * generates Mailosaur Email Address with Player FirstName and LastName
     * This function is helpful in Test scenarios where Email Verification link is required while creating an account for the player
     *
     * @param argFirstName
     * @param argLastName
     * @return EmailAddress as string
     */
    public String getRandomEmailAddressForMailosaur(String argFirstName, String argLastName) {
        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en-GB"), new RandomService());

        String mailoSaurEmailAddress = argFirstName + "." + argLastName + fakeValuesService.numerify("##") + ConfigReader.getProperty("mailosaur.DomainName");
        //SOME LAST NAME CONTAINS SPECIAL CHARACTER ' AND THE EMAIL ADDRESS BECOMES INVALID. THE FOLLOWING REPLACEMENT HELPS TO SUPPRESS THE UNWANTED SPECIAL CHARACTER
        mailoSaurEmailAddress = mailoSaurEmailAddress.replaceAll("[^a-zA-Z0-9@.]", "");
        return mailoSaurEmailAddress.toLowerCase();
    }

    /***
     * Generates the Random Card Number with Specific Pattern
     * This function helps to generate MASTERCARD, VISA, MAESTRO and AMEX Card Numbers
     * @param artCardType
     * @return Card number
     */
    public String generateCardNumber(CardType artCardType) {
        String cardNumberPrefix = artCardType.prefix;
        int cardNumberLength = artCardType.length;
        String cardNumber = cardNumberPrefix;

        // Generate the remaining numbers based on length and luhn algo
        for (int lengthIter = 0; lengthIter < cardNumberLength - cardNumberPrefix.length() - 1; lengthIter++) {
            int digit = (new Random()).nextInt(10);
            cardNumber += digit;
        }

        // Calculate the Luhn check digit
        int checkDigit = calculateCheckDigit(cardNumber);
        cardNumber += checkDigit;

        return cardNumber;
    }

    @Test
    public void generateCardNumber() {
        String cardNumber = generateCardNumber(CardType.VISA_CREDIT_INTERNATIONAL);
        System.out.println("Card number generated...");
        System.out.print(cardNumber);
    }

    /***
     * Inner class to store Card Type and their associated properties.
     * Properties include Card Prefix( Card Number starting with..), Number of Digits in Card Number and Default Card Number
     */
    public enum CardType {
        VISA("42306000000", 16, "4520444037505818"),
        MASTERCARD("535666", 16, "5555555555554444"),
        MAESTRO_16("67771300000", 16, ""),
        MAESTRO_18("67771300000", 18, ""),
        AMEX("37005500000", 16, "378282246310005"),
        VISA_ELECTRON("491754", 16, ""),
        VISA_DEBIT_INTERNATIONAL("40360500000", 16, ""),
        VISA_CREDIT_INTERNATIONAL("40219300000", 16, "");

        private final String prefix;
        private final int length;
        private final String defaultNumber;

        CardType(String argPrefix, int argLength, String argDefaultNumber) {
            this.prefix = argPrefix;
            this.length = argLength;
            this.defaultNumber = argDefaultNumber;
        }

    }
}


