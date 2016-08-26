package com.geocento.webapps.eobroker.common.client.widgets.table.celltable;

public interface ChildGetter<P, C>
{
    C getChild(P parent, int index);
}
