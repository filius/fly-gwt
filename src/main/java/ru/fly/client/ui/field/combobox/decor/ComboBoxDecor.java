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

package ru.fly.client.ui.field.combobox.decor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import ru.fly.client.ui.field.FieldDecor;

/**
 * User: fil
 * Date: 10.09.13
 * Time: 17:51
 */
public class ComboBoxDecor {

    public static interface Resources extends FieldDecor.Resources {

        @Source({"ru/fly/client/ui/common.css", "ru/fly/client/ui/field/field.css", "combobox.css"})
        public Styles css();

        @Source("trigger.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource trigger();


    }

    public static interface Styles extends FieldDecor.Styles {

        @ClassName("combobox")
        String comboBox();

        @ClassName("combobox-view")
        String comboBoxView();

        String comboBoxTrigger();

        String comboBoxTriggerIcon();

        String untriggered();

        @ClassName("list-view")
        String listView();
    }

    private final Resources res;

    public ComboBoxDecor(){
        this(GWT.<Resources>create(Resources.class));
    }

    public ComboBoxDecor(Resources res){
        this.res = res;
        res.css().ensureInjected();
    }

    public Styles css(){
        return res.css();
    }

}
