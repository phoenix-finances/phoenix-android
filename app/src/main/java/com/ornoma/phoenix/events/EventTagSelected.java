package com.ornoma.phoenix.events;

import com.ornoma.phoenix.core.TransactionTag;

/**
 * Created by de76 on 5/26/17.
 */

public class EventTagSelected {
    public final TransactionTag transactionTag;
    public EventTagSelected(TransactionTag transactionTag){this.transactionTag = transactionTag;}
}
