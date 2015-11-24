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

package ru.fly.client.ui.validator;

/**
 * for String fields validation string length, value not must be null or empty
 * @author fil
 */
public class LengthValidator extends EmptyValidator<String> {

    private final String MIN_MSG = "Не должно быть короче ";
    private final String MAX_MSG = "Не должно быть длиннее ";
    private final Integer min;
    private final Integer max;

    public LengthValidator(Integer min, Integer max){
        this.min = min;
        this.max = max;
    }

    @Override
    public String validate(String value) {
        String ret = super.validate(value);
        if(ret != null){
            return ret;
        }
        if(min != null && value.length() < min){
            return MIN_MSG+min;
        }
        if(max != null && value.length() < max){
            return MAX_MSG+max;
        }
        return null;
    }
}
