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

package ru.fly.client.ui.tree.decor;

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
public class TreeDecor {

    public static interface Resources extends ClientBundle{

        @Source({"ru/fly/client/ui/common.css", "tree.css"})
        public Styles css();

        @Source("down-arrow.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource downArrow();

        @Source("right-arrow.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource rightArrow();

        @Source("open-folder.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource folderOpen();

        @Source("closed-folder.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource folderClosed();

        @Source("item.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource item();

    }

    @CssResource.Shared
    public interface Styles extends CommonDecor.Styles {
        String tree();
        String treeRowItem();
        String treeRowItemHeader();
        String treeRowItemHeaderInner();
        String folder();
        String arrow();
        String icon();
        String expanded();
        String text();
        String gridHdrCol();
        String gridHdr();
        String treeGridView();
        String treeGridCol();
        String treeGridRowItemHeader();
        String empty();
    }

    public final Resources res;

    public TreeDecor() {
        this(GWT.<Resources>create(Resources.class));
    }

    public TreeDecor(Resources res){
        this.res = res;
        res.css().ensureInjected();
    }

    public Styles css(){
        return res.css();
    }

}
