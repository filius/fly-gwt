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

package ru.fly.client.ui.panel.tab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import ru.fly.client.ui.CommonDecor;

/**
 * User: fil
 * Date: 01.09.13
 * Time: 21:38
 */
public class TabRes {

    public static interface Resources extends ClientBundle{

        @Source({"ru/fly/client/ui/common.css", "tab.css"})
        public TabCss css();

    }

    public static interface TabCss extends CommonDecor.Styles {

        int pHdrHeight();

        @ClassName("tab-panel")
        String tabPanel();

        @ClassName("tab-hdr")
        String tabHdr();

        @ClassName("tab-content")
        String tabContent();

        @ClassName("tab-btn")
        String tabBtn();
    }

    private final Resources res = GWT.create(Resources.class);

    public TabRes(){
        res.css().ensureInjected();
    }

    public TabCss css(){
        return res.css();
    }

}
