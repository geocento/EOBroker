package com.geocento.webapps.eobroker.common.client.widgets.table.celltable;

/**
 * Permet d'extraire une valeur T d'un objet C
 *
 * @author Nicolas Morel
 *
 * @param <C> Type de l'objet dont on souhaite extraire la valeur
 * @param <V> Type de la valeur
 */
public interface ValueGetter<C, V>
{
    V getValue(C object);
}
