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

package ru.fly.client.ui.grid;

import ru.fly.shared.Getter;

/**
 * User: fil
 * Date: 31.08.13
 * Time: 14:46
 */
public class ColumnConfig<T> {

    private Renderer<T> renderer;
    private Getter<T> getter;
    private String title;
    private int width = -1;
    private int calculatedWidth;
    private int left;
    private String orderBy;
    private SortDirection sortDirection;
    private boolean resizable = true;

    public ColumnConfig(Renderer<T> renderer, String title){
        this.renderer = renderer;
        this.title = title;
    }

    public ColumnConfig(Renderer<T> renderer, String title, int width){
        this(renderer,title);
        setWidth(width);
    }

    public ColumnConfig(Getter<T> getter, String title){
        this.getter = getter;
        this.title = title;
    }

    public ColumnConfig(Getter<T> getter, String title, int width){
        this(getter,title);
        setWidth(width);
    }

    public boolean isResizable(){
        return resizable;
    }

    public ColumnConfig<T> setResizable(boolean resizable){
        this.resizable = resizable;
        return this;
    }

    public boolean isOrderable(){
        return orderBy != null;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public ColumnConfig<T> setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public ColumnConfig<T> asc(){
        sortDirection = SortDirection.ASC;
        return this;
    }

    public ColumnConfig<T> desc(){
        sortDirection = SortDirection.DESC;
        return this;
    }

    public SortDirection getSortDirection(){
        return sortDirection;
    }

    public Renderer<T> getRenderer() {
        return renderer;
    }

    public void setRenderer(Renderer<T> renderer) {
        this.renderer = renderer;
    }

    public Getter<T> getGetter() {
        return getter;
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setGetter(Getter<T> getter) {
        this.getter = getter;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCalculatedWidth() {
        return calculatedWidth;
    }

    public void setCalculatedWidth(int calculatedWidth) {
        this.calculatedWidth = calculatedWidth;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }
}
