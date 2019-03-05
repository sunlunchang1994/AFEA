/*
 * Copyright  2017  zengp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.slc.code.ui.weight.wheel.bean;


import java.util.List;

/**
 * Created by zengp on 2017/11/27.
 */

public class WheelBaseData {
    private int id;
    private String data;
    private List<WheelBaseData> items;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<WheelBaseData> getItems() {
        return items;
    }

    public void setItems(List<WheelBaseData> items) {
        this.items = items;
    }
}
