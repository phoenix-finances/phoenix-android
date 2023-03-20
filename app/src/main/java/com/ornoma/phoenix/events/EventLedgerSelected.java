package com.ornoma.phoenix.events;

import com.ornoma.phoenix.core.Ledger;

/**
 * Created by de76 on 5/5/17.
 */

public class EventLedgerSelected {
    public final Ledger ledger;
    public EventLedgerSelected(Ledger ledger){this.ledger = ledger;}
}
