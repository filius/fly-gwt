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

package ru.fly.client;

import com.google.gwt.event.shared.HandlerRegistration;
import ru.fly.client.event.UpdateEvent;
import ru.fly.client.ui.EventBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: fil
 * Date: 04.08.13
 * Time: 15:28
 */
public class ListStore<T> extends EventBase {

    public interface StoreFilter<T> {
        boolean isVisible(T item);
    }

    private List<T> list = new ArrayList<T>();
    private List<StoreFilter<T>> filters = new ArrayList<StoreFilter<T>>();

    public ListStore() {
    }

    public boolean isEmpty() {
        return list == null || list.size() < 1;
    }

    public void clear() {
        list.clear();
        fireEvent(new UpdateEvent());
    }

    public void addAll(Collection<T> list) {
        this.list.addAll(list);
        fireEvent(new UpdateEvent());
    }

    public void add(T item) {
        list.add(item);
        fireEvent(new UpdateEvent());
    }

    public void remove(T item) {
        if (list.remove(item)) {
            fireEvent(new UpdateEvent());
        }
    }

    public void insert(int idx, T item) {
        list.remove(item);
        list.add(idx, item);
        fireEvent(new UpdateEvent());
    }

    public void replace(T item) {
        int idx = list.indexOf(item);
        if(idx > -1) {
            list.remove(item);
            list.add(idx, item);
        }else{
            list.add(item);
        }
        fireEvent(new UpdateEvent());
    }

    public int indexOf(T item) {
        return list.indexOf(item);
    }

    public boolean contains(T item) {
        return list.contains(item);
    }

    /**
     * exchange two element of store
     *
     * @param idx1 - element one
     * @param idx2 - element two
     */
    public void exchange(int idx1, int idx2) {
        T tmp = list.get(idx1);
        list.set(idx1, list.get(idx2));
        list.set(idx2, tmp);
        fireEvent(new UpdateEvent());
    }

    public int size() {
        return getList().size();
    }

    public T get(int idx) {
        return getList().get(idx);
    }

    public T get(T model) {
        return list.get(list.indexOf(model));
    }

    public void fill(Collection<T> list) {
        this.list.clear();
        if (list != null)
            this.list.addAll(list);
        fireEvent(new UpdateEvent());
    }

    public List<T> getList() {
        List<T> view = new ArrayList<T>();
        if (list != null) {
            if (filters.size() > 0) {
                for (T item : list) {
                    boolean visible = true;
                    for (StoreFilter<T> f : filters) {
                        visible &= f.isVisible(item);
                    }
                    if (visible)
                        view.add(item);
                }
            } else
                view.addAll(list);
        }
        return view;
    }

//    public T get(String field, Object value){
//        for(T item : list){
//            if(value.equals(BeanModelUtil.createBean(item).get(field))){
//                return item;
//            }
//        }
//        return null;
//    }

    public void addFilter(StoreFilter<T> filter) {
        filters.add(filter);
    }

    public HandlerRegistration addUpdateHandler(UpdateEvent.UpdateHandler lnr) {
        return addHandler(lnr, UpdateEvent.getType());
    }

}
