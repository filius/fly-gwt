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

package ru.fly.client.ui.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.toolbar.decor.ToolbarDecor;

/**
 * User: fil
 * Date: 09.09.13
 * Time: 17:33
 */
public class ToolbarSplitter extends Component {

    private final ToolbarDecor res = GWT.create(ToolbarDecor.class);

    public ToolbarSplitter(){
        super(DOM.createSpan());
        setStyleName(res.css().toolbarSplitter());
    }

}
