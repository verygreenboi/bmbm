package net.glassstones.bambammusic.models;

import com.devmarvel.creditcardentry.library.CreditCard;

import io.realm.RealmObject;

/**
 * Created by Thompson on 02/04/2016.
 * For BambamMusic
 */

@SuppressWarnings("unused")
public class LocalCreditCard extends RealmObject {
    private String cardNumber, cvc;
    private int expiryYear, expiryMonth;
    private boolean isExpired;

    public LocalCreditCard() {
    }

    public LocalCreditCard(CreditCard creditCard, boolean isDefault) {
        this.cardNumber = creditCard.getCardNumber();
        this.expiryMonth = creditCard.getExpMonth();
        this.expiryYear = creditCard.getExpYear();
        this.cvc = creditCard.getSecurityCode();
//        this.isExpired = CardUtils.isExpired(creditCard.getExpYear(), creditCard.getExpMonth());
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(int expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public int getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(int expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }
}
