package com.ornoma.phoenix.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by de76 on 5/4/17.
 */

public class Transaction {
    private int rawId;
    private String transactionId;
    private String description;
    private List<RawTransaction> rawTransactionList;
    private long timeInMillis;

    public Transaction(){rawTransactionList = new ArrayList<>();}

    public void setRawId(int rawId){this.rawId = rawId;}
    public void setTransactionId(String transactionId){this.transactionId = transactionId;}
    public void setDescription(String description){this.description = description;}
    public void setRawTransactionList(List<RawTransaction> rawTransactionList){this.rawTransactionList = rawTransactionList;}
    public void setTimeInMillis(long timeInMillis){this.timeInMillis = timeInMillis;}

    public int getRawId(){return this.rawId;}
    public String getTransactionId(){return this.transactionId;}
    public String getDescription(){return this.description;}
    public List<RawTransaction> getRawTransactionList(){return this.rawTransactionList;}
    public long getTimeInMillis(){return this.timeInMillis;}

    public void addRawTransaction(RawTransaction rawTransaction){
        rawTransactionList.add(rawTransaction);
    }

    public void removeRawTransaction(RawTransaction rawTransaction){
        rawTransactionList.remove(rawTransaction);
    }

    public String getDifString(){
        long millis = System.currentTimeMillis() - timeInMillis;

        int Minutes = (int) (millis/(1000*60)) % 60;
        int Hours = (int) (millis/(1000*60*60)) % 60;
        int days = (int) (millis/(1000*60*60*24));

        if (days>0)
            return getDateString();
        else if(Hours>0)
            return (days * 24 + Hours) + " hours ago";
        else
            return (Hours * 60 + Minutes) + " minutes ago";
    }

    private static final String[] monthArray = new String[]{"Jan","Feb", "Mar",
            "Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    private String getDateString(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int monthInt = calendar.get(Calendar.MONTH);
        String month = monthArray[monthInt];
        String date = calendar.get(Calendar.DAY_OF_MONTH) + " " + month + " " +calendar.get(Calendar.YEAR);
        return date
                + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
    }

}
