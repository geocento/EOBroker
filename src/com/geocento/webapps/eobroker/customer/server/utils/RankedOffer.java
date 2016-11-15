package com.geocento.webapps.eobroker.customer.server.utils;

import com.geocento.webapps.eobroker.customer.shared.Offer;

public class RankedOffer {

    double rank;
    Offer offer;

    public RankedOffer(double rank, Offer offer) {
        this.rank = rank;
        this.offer = offer;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }
}

