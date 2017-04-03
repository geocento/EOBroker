/*
 * #%L
 * GwtMaterial
 * %%
 * Copyright (C) 2015 - 2016 GwtMaterialDesign
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.geocento.webapps.eobroker.common.client.widgets.material;

//@formatter:off

import gwt.material.design.client.ui.MaterialListBox;

/**
 * <p>Material ListBox is another dropdown component that will set / get the value depends on the selected index
 * <h3>UiBinder Usage:</h3>
 * <p>
 * <pre>
 * {@code
 *    <m:MaterialListBox ui:field="lstBox" />
 * }
 * </pre>
 * <h3>Java Usage:</h3>
 * <p>
 * <pre>
 * {@code
 *     // functions
 *    lstBox.setSelectedIndex(2);
 *    lstBox.getSelectedIndex();
 *    lstBox.addValueChangeHandler(handler);
 * }
 * </pre>
 * </p>
 *
 * @author kevzlou7979
 * @author Ben Dol
 * @see <a href="http://gwtmaterialdesign.github.io/gwt-material-demo/#forms">Material ListBox</a>
 */
//@formatter:on
public class MaterialListValueBox<T extends Enum> extends MaterialListBox {

    public void addTypedItem(T item, String name) {
        super.addItem(item.toString(), name);
    }

    public void addNullItem(String name) {
        super.addItem("", name);
    }

    public T getTypedValue(Class enumType) {
        String value = super.getSelectedValue();
        return value.length() == 0 ? null : (T) T.valueOf(enumType, value);
    }
}