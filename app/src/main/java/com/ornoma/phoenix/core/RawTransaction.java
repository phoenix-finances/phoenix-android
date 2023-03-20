package com.ornoma.phoenix.core;

/**
 * Created by de76 on 5/4/17.
 */

public class RawTransaction {
    private int rawId;
    private String transactionId;
    private int ledgerId;
    private double debit;
    private double credit;

    public void setRawId(int rawId){this.rawId = rawId;}
    public void setTransactionId(String transactionId){this.transactionId = transactionId;}
    public void setLedgerId(int ledgerId){this.ledgerId = ledgerId;}
    public void setDebit(double debit){this.debit = debit;}
    public void setCredit(double credit){this.credit = credit;}

    public int getRawId(){return this.rawId;}
    public String getTransactionId(){return this.transactionId;}
    public int getLedgerId(){return this.ledgerId;}
    public double getDebit(){return this.debit;}
    public double getCredit(){return this.credit;}

}
