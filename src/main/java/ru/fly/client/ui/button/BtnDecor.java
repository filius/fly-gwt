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

package ru.fly.client.ui.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import ru.fly.client.ui.CommonDecor;

/**
 * User: fil
 * Date: 01.09.13
 * Time: 21:38
 */
public class BtnDecor {

    public static interface Resources extends ClientBundle{

        @Source({"ru/fly/client/ui/common.css", "button.css"})
        public Styles css();

        @Source("drop-arrow.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource dropArrow();

    }

    @CssResource.Shared
    public static interface Styles extends CommonDecor.Styles {

        int pBorderWidth();

        String button();

        @ClassName("button-menu")
        String buttonMenu();

        @ClassName("button-icon")
        String buttonIcon();

    }

    public final Resources res;

    public BtnDecor(){
        this(GWT.<Resources>create(Resources.class));
    }

    public BtnDecor(Resources res){
        this.res = res;
        res.css().ensureInjected();
    }

    public Styles css(){
        return res.css();
    }

}
