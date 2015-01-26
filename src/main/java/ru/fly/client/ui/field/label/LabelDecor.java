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
import com.google.gwt.resources.client.ClientBundle;
import ru.fly.client.ui.CommonDecor;

/**
 * User: fil
 * Date: 01.09.13
 * Time: 21:38
 */
public class LabelDecor {

    public static interface Resources extends ClientBundle{

        @Source({"ru/fly/client/ui/common.css", "label.css"})
        public CssFieldLabel css();

    }

    public static interface CssFieldLabel extends CommonDecor.Styles {

        int pLabelMargin();

        @ClassName("field-label")
        String fieldLabel();

        String label();

    }

    public final Resources res = GWT.create(Resources.class);

    public LabelDecor(){
        res.css().ensureInjected();
    }

    public CssFieldLabel css(){
        return res.css();
    }

}
