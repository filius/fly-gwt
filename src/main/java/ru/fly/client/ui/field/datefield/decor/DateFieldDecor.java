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

package ru.fly.client.ui.field.datefield.decor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import ru.fly.client.ui.field.FieldDecor;

/**
 * User: fil
 * Date: 10.09.13
 * Time: 17:51
 */
public class DateFieldDecor {

    public static interface Resources extends FieldDecor.Resources {

        @Source({"ru/fly/client/ui/common.css", "ru/fly/client/ui/field/field.css", "datefield.css"})
        public Styles css();

        @Source("trigger.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource trigger();

    }

    public static interface Styles extends FieldDecor.Styles {

        @ClassName("datefield")
        String dateField();

        @ClassName("datefield-view")
        String dateFieldView();

        @ClassName("date-picker")
        String datePicker();

        String dateFieldTrigger();

        String dateFieldTriggerIcon();

    }

    private final Resources res = GWT.create(Resources.class);

    public DateFieldDecor(){
        res.css().ensureInjected();
    }

    public Styles css(){
        return res.css();
    }

}
