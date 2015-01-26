/*
 * Copyright 2015 Valeriy Filatov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package ru.fly.client.ui.field.label;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import ru.fly.client.ui.Component;

public class Label extends Component {

    public Label(String text){
        super(DOM.createLabel());
        LabelDecor decor = GWT.create(LabelDecor.class);
        setStyleName(decor.css().label());
        getElement().setInnerHTML(text);
    }

    public void setHtml(String text){
        getElement().setInnerHTML(text);
    }

}
